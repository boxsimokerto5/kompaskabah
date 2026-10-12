package com.example.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Singleton manager for Google Play Billing Subscription (Bebas Iklan Bulanan).
 * Manages Google Play BillingClient connection, product querying, purchase flow,
 * purchase acknowledgment, and active subscription status persistence.
 */
object BillingManager : PurchasesUpdatedListener {
    private const val TAG = "BillingManager"

    // Primary Monthly Subscription Product ID on Google Play Console
    const val SUBSCRIPTION_MONTHLY_ID = "bebas_iklan_bulanan"
    const val SUBSCRIPTION_MONTHLY_FALLBACK_ID = "kompas_bebas_iklan_monthly"

    // SharedPreferences for local caching so ad-free persists offline
    private const val PREFS_NAME = "kompas_billing_prefs"
    private const val KEY_IS_SUBSCRIBED = "pref_is_monthly_subscribed"
    private const val KEY_PURCHASE_TOKEN = "pref_subscription_token"
    private const val KEY_EXPIRY_TIMESTAMP = "pref_subscription_expiry_time"
    private const val KEY_LAST_VERIFIED = "pref_last_verified_time"

    private val billingScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var appContext: Context? = null
    private var billingClient: BillingClient? = null
    private var isConnecting = false

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    private val _formattedPrice = MutableStateFlow("Rp 4.999 / bulan")
    val formattedPrice: StateFlow<String> = _formattedPrice.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private val _isProcessingPurchase = MutableStateFlow(false)
    val isProcessingPurchase: StateFlow<Boolean> = _isProcessingPurchase.asStateFlow()

    fun init(context: Context) {
        val appCtx = context.applicationContext
        appContext = appCtx

        // Restore cached subscription state immediately so UI and ads respond with zero delay
        val cachedSubscribed = isCachedSubscribed(appCtx)
        _isSubscribed.value = cachedSubscribed
        Log.d(TAG, "BillingManager initialized. Cached subscription status: $cachedSubscribed")

        initializeBillingClient(appCtx)
    }

    private fun initializeBillingClient(context: Context) {
        try {
            val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()

            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .build()

            startBillingConnection()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing BillingClient: ${e.message}", e)
        }
    }

    fun startBillingConnection(onConnected: (() -> Unit)? = null) {
        val client = billingClient ?: return
        if (client.isReady) {
            _isConnected.value = true
            onConnected?.invoke()
            querySubscriptionProducts()
            queryActivePurchases()
            return
        }

        if (isConnecting) return
        isConnecting = true

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isConnecting = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "BillingClient connected successfully!")
                    _isConnected.value = true
                    _billingStatusMessage.value = null
                    onConnected?.invoke()
                    querySubscriptionProducts()
                    queryActivePurchases()
                } else {
                    val msg = "Billing setup failed: ${billingResult.debugMessage} (code ${billingResult.responseCode})"
                    Log.w(TAG, msg)
                    _isConnected.value = false
                    _billingStatusMessage.value = msg
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnecting = false
                _isConnected.value = false
                Log.w(TAG, "Billing service disconnected. Will retry connection later...")
                billingScope.launch {
                    delay(3000)
                    startBillingConnection()
                }
            }
        })
    }

    /**
     * Queries Google Play for the monthly subscription product details and real pricing.
     */
    fun querySubscriptionProducts() {
        val client = billingClient
        if (client == null || !client.isReady) {
            Log.d(TAG, "BillingClient not ready yet for querySubscriptionProducts, deferring...")
            return
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_MONTHLY_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_MONTHLY_FALLBACK_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = queryProductDetailsResult.productDetailsList
                Log.d(TAG, "Products queried successfully. Found ${productDetailsList.size} subscription products.")
                if (productDetailsList.isNotEmpty()) {
                    val primary = productDetailsList.firstOrNull { it.productId == SUBSCRIPTION_MONTHLY_ID }
                        ?: productDetailsList.first()
                    _productDetails.value = primary

                    // Extract formatted price from subscription offer details
                    val offer = primary.subscriptionOfferDetails?.firstOrNull()
                    val pricingPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
                    val price = pricingPhase?.formattedPrice
                    if (!price.isNullOrBlank()) {
                        _formattedPrice.value = "$price / bulan"
                    }
                }
            } else {
                Log.w(TAG, "queryProductDetailsAsync error: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Checks if user already holds an active Google Play subscription.
     */
    fun queryActivePurchases(onComplete: ((Boolean) -> Unit)? = null) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onComplete?.invoke(_isSubscribed.value)
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var foundActiveSubscription = false

                for (purchase in purchasesList) {
                    if (purchase.products.contains(SUBSCRIPTION_MONTHLY_ID) ||
                        purchase.products.contains(SUBSCRIPTION_MONTHLY_FALLBACK_ID)
                    ) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            foundActiveSubscription = true
                            handleValidPurchase(purchase)
                        }
                    }
                }

                if (!foundActiveSubscription) {
                    // Only revoke if cache is not in offline test mode
                    val context = appContext
                    if (context != null && !isTestSubscriptionActive(context)) {
                        setSubscribed(context, false, null)
                    }
                }

                onComplete?.invoke(foundActiveSubscription)
            } else {
                Log.w(TAG, "queryPurchasesAsync failed: ${billingResult.debugMessage}")
                onComplete?.invoke(_isSubscribed.value)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        _isProcessingPurchase.value = false
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "Purchase succeeded with ${purchases.size} purchases returned")
            for (purchase in purchases) {
                handleValidPurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "User cancelled the subscription purchase flow")
            _billingStatusMessage.value = "Pembelian dibatalkan"
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i(TAG, "Subscription is already owned, restoring ad-free status")
            appContext?.let { setSubscribed(it, true, null) }
            queryActivePurchases()
        } else {
            val msg = "Gagal memproses langganan: ${billingResult.debugMessage} (kode ${billingResult.responseCode})"
            Log.w(TAG, msg)
            _billingStatusMessage.value = msg
        }
    }

    private fun handleValidPurchase(purchase: Purchase) {
        val context = appContext ?: return
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge the purchase if not acknowledged yet (mandatory within 3 days or Google auto-refunds)
            if (!purchase.isAcknowledged) {
                val client = billingClient
                if (client != null && client.isReady) {
                    val ackParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    client.acknowledgePurchase(ackParams) { ackResult ->
                        if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "Purchase successfully acknowledged with token: ${purchase.purchaseToken}")
                        } else {
                            Log.w(TAG, "Acknowledge purchase warning: ${ackResult.debugMessage}")
                        }
                    }
                }
            }

            setSubscribed(context, true, purchase.purchaseToken)
            Log.d(TAG, "Active monthly subscription confirmed & saved.")
        }
    }

    /**
     * Launches the official Google Play Billing subscription checkout sheet.
     */
    fun launchBillingFlow(
        activity: Activity,
        onFailed: (String) -> Unit = {}
    ) {
        val client = billingClient
        if (client == null || !client.isReady) {
            val errMsg = "Layanan Google Play Store sedang menghubungkan. Silakan coba lagi."
            startBillingConnection {
                launchBillingFlow(activity, onFailed)
            }
            onFailed(errMsg)
            return
        }

        val details = _productDetails.value
        if (details == null) {
            // Product details not yet returned by Play Console (e.g. unpublished in draft or testing environment)
            Log.w(TAG, "ProductDetails for $SUBSCRIPTION_MONTHLY_ID is null. Checking fallback or test activation.")
            querySubscriptionProducts()
            onFailed("Produk langganan bulanan sedang disinkronkan dengan Google Play. Pastikan aplikasi telah terhubung ke Play Console.")
            return
        }

        try {
            _isProcessingPurchase.value = true
            val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""

            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .apply {
                        if (offerToken.isNotBlank()) {
                            setOfferToken(offerToken)
                        }
                    }
                    .build()
            )

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val result = client.launchBillingFlow(activity, flowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                _isProcessingPurchase.value = false
                val msg = "Gagal membuka checkout Google Play: ${result.debugMessage}"
                _billingStatusMessage.value = msg
                onFailed(msg)
            }
        } catch (e: Exception) {
            _isProcessingPurchase.value = false
            Log.e(TAG, "Exception launching billing flow: ${e.message}", e)
            onFailed("Terjadi kendala saat membuka langganan Google Play: ${e.message}")
        }
    }

    /**
     * Opens Google Play Subscription Management page in the browser/Play Store app
     * so user can easily manage, renew, or cancel their monthly subscription.
     */
    fun openGooglePlaySubscriptions(activity: Activity) {
        val pkg = activity.packageName
        val url = "https://play.google.com/store/account/subscriptions?package=$pkg&sku=$SUBSCRIPTION_MONTHLY_ID"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
        } catch (e: Exception) {
            try {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
                )
            } catch (_: Exception) {
                // Ignore if browser unavailable
            }
        }
    }

    /**
     * Restores purchases by re-querying Google Play Store.
     */
    fun restorePurchases(onResult: (Boolean, String) -> Unit) {
        val client = billingClient
        if (client == null || !client.isReady) {
            startBillingConnection {
                restorePurchases(onResult)
            }
            onResult(_isSubscribed.value, "Menghubungkan ke Google Play Store...")
            return
        }

        queryActivePurchases { isFound ->
            if (isFound) {
                onResult(true, "Langganan bulanan aktif berhasil dipulihkan! Semua iklan telah dinonaktifkan.")
            } else {
                onResult(false, "Tidak ditemukan langganan aktif untuk akun Google Play ini.")
            }
        }
    }

    /**
     * Testing / Dev environment helper: allows toggling subscription status in emulator / sandbox.
     */
    fun toggleDevSubscription(context: Context, enabled: Boolean) {
        setSubscribed(context, enabled, if (enabled) "dev_test_token" else null, isTest = true)
    }

    private fun setSubscribed(context: Context, subscribed: Boolean, token: String?, isTest: Boolean = false) {
        _isSubscribed.value = subscribed
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_IS_SUBSCRIBED, subscribed)
                .putString(KEY_PURCHASE_TOKEN, token ?: "")
                .putLong(KEY_LAST_VERIFIED, System.currentTimeMillis())
                .putBoolean("pref_is_dev_test", isTest)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving subscription preference: ${e.message}")
        }
    }

    private fun isCachedSubscribed(context: Context): Boolean {
        return try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_SUBSCRIBED, false)
        } catch (e: Exception) {
            false
        }
    }

    private fun isTestSubscriptionActive(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(KEY_IS_SUBSCRIBED, false) && prefs.getBoolean("pref_is_dev_test", false)
        } catch (e: Exception) {
            false
        }
    }

    fun getSubscriptionDetailsSummary(): String {
        return if (_isSubscribed.value) {
            "Langganan Bulanan Aktif • Bebas Semua Iklan"
        } else {
            "Belum Berlangganan (${_formattedPrice.value})"
        }
    }
}
