package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.InitializationListener
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object IronSourceAdManager {
    private const val TAG = "IronSourceAds"

    // User's ironSource App Key
    const val APP_KEY = "28a1fd6ad"

    // Ad Unit ID / Placement for Banner
    const val BANNER_UNIT_ID = "dxk7ixq630tpjwzu"

    // Interstitial frequency: every 6 clicks
    const val INTERSTITIAL_CLICK_THRESHOLD = 6

    private val adScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var isInitialized = false
    private var currentActivityRef: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityRef?.get()

    // Click counter for Interstitial ad trigger
    private var clickCount = 0
    private var pendingInterstitialShow = false
    private var isInterstitialLoading = false

    private val _isBannerLoaded = MutableStateFlow(false)
    val isBannerLoaded: StateFlow<Boolean> = _isBannerLoaded.asStateFlow()

    private val _lastBannerError = MutableStateFlow<String?>(null)
    val lastBannerError: StateFlow<String?> = _lastBannerError.asStateFlow()

    private val _isInterstitialReady = MutableStateFlow(false)
    val isInterstitialReady: StateFlow<Boolean> = _isInterstitialReady.asStateFlow()

    private val _isRewardedAvailable = MutableStateFlow(false)
    val isRewardedAvailable: StateFlow<Boolean> = _isRewardedAvailable.asStateFlow()

    private var pendingRewardedCallback: (() -> Unit)? = null

    fun init(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        if (isInitialized) {
            Log.d(TAG, "IronSource already initialized")
            return
        }

        try {
            Log.d(TAG, "Initializing IronSource LevelPlay with App Key: $APP_KEY")

            // Setup listeners prior to init
            setupInterstitialListener()
            setupRewardedVideoListener()

            // Initialize IronSource with InitializationListener for Banner, Interstitial, and Rewarded Video
            IronSource.init(
                activity,
                APP_KEY,
                object : InitializationListener {
                    override fun onInitializationComplete() {
                        Log.d(TAG, "IronSource onInitializationComplete triggered! Preloading interstitial...")
                        isInitialized = true
                        loadInterstitial()
                    }
                },
                IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL,
                IronSource.AD_UNIT.REWARDED_VIDEO
            )

            Log.d(TAG, "IronSource init sequence started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize IronSource: ${e.message}", e)
        }
    }

    fun onResume(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        try {
            IronSource.onResume(activity)
        } catch (e: Exception) {
            Log.e(TAG, "Error in IronSource.onResume: ${e.message}")
        }
    }

    fun onPause(activity: Activity) {
        try {
            IronSource.onPause(activity)
        } catch (e: Exception) {
            Log.e(TAG, "Error in IronSource.onPause: ${e.message}")
        }
    }

    /**
     * Records an interactive user click.
     * When the counter reaches INTERSTITIAL_CLICK_THRESHOLD (6 clicks),
     * an interstitial ad is triggered. If not yet loaded, it will show immediately once ready.
     */
    fun recordClick(activity: Activity? = currentActivity) {
        val targetAct = activity ?: currentActivity
        clickCount++
        Log.d(TAG, "Click recorded: $clickCount / $INTERSTITIAL_CLICK_THRESHOLD")

        if (clickCount >= INTERSTITIAL_CLICK_THRESHOLD) {
            if (IronSource.isInterstitialReady()) {
                Log.d(TAG, "Threshold reached ($clickCount clicks), interstitial ready -> showing now")
                clickCount = 0
                pendingInterstitialShow = false
                showInterstitialIfReady(targetAct)
            } else {
                Log.d(TAG, "Threshold reached ($clickCount clicks), but interstitial not ready yet. Preloading...")
                pendingInterstitialShow = true
                loadInterstitial()
                // Do not reset clickCount so user does not lose their progress until the ad is served
            }
        }
    }

    fun recordClick(context: Context?) {
        val activity = context?.findActivity() ?: currentActivity
        recordClick(activity)
    }

    fun loadInterstitial() {
        if (IronSource.isInterstitialReady()) {
            Log.d(TAG, "Interstitial is already ready, skip loadInterstitial")
            _isInterstitialReady.value = true
            return
        }
        if (isInterstitialLoading) {
            Log.d(TAG, "Interstitial load is already in progress, skipping duplicate request")
            return
        }
        try {
            isInterstitialLoading = true
            IronSource.loadInterstitial()
        } catch (e: Exception) {
            isInterstitialLoading = false
            Log.e(TAG, "Error loading interstitial: ${e.message}", e)
        }
    }

    fun showInterstitialIfReady(activity: Activity? = currentActivity) {
        val targetAct = activity ?: currentActivity
        try {
            if (IronSource.isInterstitialReady()) {
                Log.d(TAG, "Showing Interstitial Ad (threshold reached)")
                clickCount = 0
                pendingInterstitialShow = false
                isInterstitialLoading = false
                targetAct?.runOnUiThread {
                    IronSource.showInterstitial(targetAct)
                } ?: run {
                    IronSource.showInterstitial()
                }
            } else {
                Log.d(TAG, "Interstitial not ready yet. Requesting load...")
                loadInterstitial()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while showing interstitial: ${e.message}", e)
        }
    }

    private fun setupInterstitialListener() {
        IronSource.setLevelPlayInterstitialListener(object : LevelPlayInterstitialListener {
            override fun onAdReady(adInfo: AdInfo?) {
                Log.d(TAG, "Interstitial ad ready! Network: ${adInfo?.adNetwork}")
                isInterstitialLoading = false
                _isInterstitialReady.value = true

                // If user already reached 6 clicks while the ad was loading, show it immediately now
                if (pendingInterstitialShow || clickCount >= INTERSTITIAL_CLICK_THRESHOLD) {
                    Log.d(TAG, "Fulfilling pending interstitial display now that ad is ready")
                    pendingInterstitialShow = false
                    clickCount = 0
                    currentActivity?.let { act ->
                        act.runOnUiThread {
                            showInterstitialIfReady(act)
                        }
                    }
                }
            }

            override fun onAdLoadFailed(error: IronSourceError?) {
                Log.w(TAG, "Interstitial load failed: ${error?.errorMessage} (code: ${error?.errorCode})")
                isInterstitialLoading = false
                _isInterstitialReady.value = false
                // Schedule retry in background
                adScope.launch {
                    delay(5000)
                    loadInterstitial()
                }
            }

            override fun onAdOpened(adInfo: AdInfo?) {
                Log.d(TAG, "Interstitial opened")
                isInterstitialLoading = false
            }

            override fun onAdShowSucceeded(adInfo: AdInfo?) {
                Log.d(TAG, "Interstitial show succeeded")
            }

            override fun onAdShowFailed(error: IronSourceError?, adInfo: AdInfo?) {
                Log.w(TAG, "Interstitial show failed: ${error?.errorMessage}")
                isInterstitialLoading = false
                _isInterstitialReady.value = false
                loadInterstitial()
            }

            override fun onAdClicked(adInfo: AdInfo?) {
                Log.d(TAG, "Interstitial clicked")
            }

            override fun onAdClosed(adInfo: AdInfo?) {
                Log.d(TAG, "Interstitial ad closed; loading next interstitial for future cycle")
                _isInterstitialReady.value = false
                isInterstitialLoading = false
                pendingInterstitialShow = false
                clickCount = 0
                loadInterstitial()
            }
        })
    }

    fun loadRewardedVideo() {
        // In ironSource SDK, Rewarded Video uses automatic waterfall caching by default.
        // Calling IronSource.loadRewardedVideo() in auto mode causes:
        // "API: Rewarded Video is not initiated with manual load"
    }

    /**
     * Shows a Rewarded Video ad when the user wants to view nearby mosques on Google Maps.
     * In automatic mode, the SDK handles pre-caching. If ready, it displays immediately.
     * When completed, closed, or if unavailable, runs [onFinished].
     */
    fun showRewardedVideoForMosque(
        activity: Activity? = currentActivity,
        onFinished: () -> Unit
    ) {
        val targetActivity = activity ?: currentActivity
        if (targetActivity == null) {
            onFinished()
            return
        }

        if (IronSource.isRewardedVideoAvailable()) {
            Log.d(TAG, "Showing Rewarded Video for viewing nearby mosque on Google Maps")
            pendingRewardedCallback = onFinished
            targetActivity.runOnUiThread {
                try {
                    IronSource.showRewardedVideo(targetActivity)
                } catch (e: Exception) {
                    Log.e(TAG, "Error displaying rewarded video: ${e.message}", e)
                    val cb = pendingRewardedCallback
                    pendingRewardedCallback = null
                    cb?.invoke()
                }
            }
        } else {
            Log.d(TAG, "Rewarded video not ready at this moment. Waiting briefly for auto-cache...")

            // Wait up to 2 seconds while polling in case the ad was finishing its automatic cache cycle
            var isHandled = false
            adScope.launch {
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < 2000) {
                    delay(250)
                    if (!isHandled && IronSource.isRewardedVideoAvailable()) {
                        isHandled = true
                        Log.d(TAG, "Rewarded video became ready during wait! Showing now...")
                        pendingRewardedCallback = onFinished
                        targetActivity.runOnUiThread {
                            try {
                                IronSource.showRewardedVideo(targetActivity)
                            } catch (e: Exception) {
                                val cb = pendingRewardedCallback
                                pendingRewardedCallback = null
                                cb?.invoke()
                            }
                        }
                        return@launch
                    }
                }

                // If still not available after timeout, gracefully proceed to Maps
                if (!isHandled) {
                    isHandled = true
                    Log.d(TAG, "Rewarded video unavailable after wait timeout. Proceeding to Maps gracefully.")
                    targetActivity.runOnUiThread {
                        onFinished()
                    }
                }
            }
        }
    }

    fun showRewardedVideoForMosque(
        context: Context?,
        onFinished: () -> Unit
    ) {
        val act = context?.findActivity() ?: currentActivity
        showRewardedVideoForMosque(act, onFinished)
    }

    private fun setupRewardedVideoListener() {
        IronSource.setLevelPlayRewardedVideoListener(object : LevelPlayRewardedVideoListener {
            override fun onAdAvailable(adInfo: AdInfo?) {
                Log.d(TAG, "Rewarded video available! Network: ${adInfo?.adNetwork}")
                _isRewardedAvailable.value = true
            }

            override fun onAdUnavailable() {
                Log.d(TAG, "Rewarded video unavailable")
                _isRewardedAvailable.value = false
            }

            override fun onAdOpened(adInfo: AdInfo?) {
                Log.d(TAG, "Rewarded video opened")
            }

            override fun onAdShowFailed(error: IronSourceError?, adInfo: AdInfo?) {
                Log.w(TAG, "Rewarded video show failed: ${error?.errorMessage}")
                _isRewardedAvailable.value = false
                val cb = pendingRewardedCallback
                pendingRewardedCallback = null
                cb?.invoke()
            }

            override fun onAdClicked(placement: Placement?, adInfo: AdInfo?) {
                Log.d(TAG, "Rewarded video clicked: ${placement?.placementName}")
            }

            override fun onAdRewarded(placement: Placement?, adInfo: AdInfo?) {
                Log.d(TAG, "User rewarded for watching ad: ${placement?.placementName}")
            }

            override fun onAdClosed(adInfo: AdInfo?) {
                Log.d(TAG, "Rewarded video closed. Fulfilling action (opening Google Maps).")
                _isRewardedAvailable.value = false
                val cb = pendingRewardedCallback
                pendingRewardedCallback = null
                cb?.invoke()
            }
        })
    }

    fun createAndLoadBanner(
        activity: Activity,
        placementName: String = BANNER_UNIT_ID,
        bannerSize: ISBannerSize = ISBannerSize.BANNER,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((String) -> Unit)? = null
    ): IronSourceBannerLayout? {
        return try {
            val bannerLayout = IronSource.createBanner(activity, bannerSize)
            if (bannerLayout == null) {
                Log.e(TAG, "Failed to create banner layout: returned null")
                return null
            }

            bannerLayout.levelPlayBannerListener = object : LevelPlayBannerListener {
                override fun onAdLoaded(adInfo: AdInfo?) {
                    Log.d(TAG, "Banner Ad loaded. Network: ${adInfo?.adNetwork}, Placement: $placementName")
                    _isBannerLoaded.value = true
                    _lastBannerError.value = null
                    onLoaded?.invoke()
                }

                override fun onAdLoadFailed(error: IronSourceError?) {
                    val msg = "Banner Ad load failed: ${error?.errorMessage} (code: ${error?.errorCode})"
                    Log.w(TAG, msg)
                    _isBannerLoaded.value = false
                    _lastBannerError.value = msg
                    onFailed?.invoke(msg)
                }

                override fun onAdClicked(adInfo: AdInfo?) {
                    Log.d(TAG, "Banner Ad clicked")
                }

                override fun onAdLeftApplication(adInfo: AdInfo?) {
                    Log.d(TAG, "Banner Ad left application")
                }

                override fun onAdScreenPresented(adInfo: AdInfo?) {
                    Log.d(TAG, "Banner Ad screen presented")
                }

                override fun onAdScreenDismissed(adInfo: AdInfo?) {
                    Log.d(TAG, "Banner Ad screen dismissed")
                }
            }

            // Load the banner using placement or default
            if (placementName.isNotBlank()) {
                IronSource.loadBanner(bannerLayout, placementName)
            } else {
                IronSource.loadBanner(bannerLayout)
            }

            bannerLayout
        } catch (e: Exception) {
            Log.e(TAG, "Exception while creating/loading banner: ${e.message}", e)
            null
        }
    }

    fun destroyBanner(banner: IronSourceBannerLayout?) {
        if (banner != null) {
            try {
                IronSource.destroyBanner(banner)
                _isBannerLoaded.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying banner: ${e.message}")
            }
        }
    }
}

fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
