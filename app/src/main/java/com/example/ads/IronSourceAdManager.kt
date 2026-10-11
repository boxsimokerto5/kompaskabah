package com.example.ads

import android.app.Activity
import android.util.Log
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.integration.IntegrationHelper
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object IronSourceAdManager {
    private const val TAG = "IronSourceAds"

    // User's ironSource App Key
    const val APP_KEY = "28a1fd6ad"

    // Ad Unit ID / Placement for Banner
    const val BANNER_UNIT_ID = "dxk7ixq630tpjwzu"

    private var isInitialized = false

    private val _isBannerLoaded = MutableStateFlow(false)
    val isBannerLoaded: StateFlow<Boolean> = _isBannerLoaded.asStateFlow()

    private val _lastBannerError = MutableStateFlow<String?>(null)
    val lastBannerError: StateFlow<String?> = _lastBannerError.asStateFlow()

    fun init(activity: Activity) {
        if (isInitialized) {
            Log.d(TAG, "IronSource already initialized")
            return
        }

        try {
            Log.d(TAG, "Initializing IronSource LevelPlay with App Key: $APP_KEY")

            // Initialize IronSource for Banner and Interstitial
            IronSource.init(
                activity,
                APP_KEY,
                IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL
            )

            // Validate integration to check adapters (including Meta Audience Network)
            try {
                IntegrationHelper.validateIntegration(activity)
            } catch (e: Throwable) {
                Log.w(TAG, "IntegrationHelper validation check: ${e.message}")
            }

            isInitialized = true
            Log.d(TAG, "IronSource initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize IronSource: ${e.message}", e)
        }
    }

    fun onResume(activity: Activity) {
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
