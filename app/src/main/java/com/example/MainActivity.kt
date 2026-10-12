package com.example

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppLanguageManager
import com.example.ui.QiblaMainScreen
import com.example.ui.QiblaNavigationTab
import com.example.ui.QiblaViewModel
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import com.example.ads.IronSourceAdManager
import com.example.billing.BillingManager
import com.example.update.GooglePlayUpdateManager
import com.example.widget.QiblaPrayerWidgetProvider

class MainActivity : ComponentActivity() {

    private val qiblaViewModel: QiblaViewModel by viewModels()
    private lateinit var playUpdateManager: GooglePlayUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureWebViewCacheDirs()
        enableEdgeToEdge()
        handleWidgetDeepLinkIntent(intent)

        // Initialize ironSource LevelPlay SDK with Meta Audience Network
        IronSourceAdManager.init(this)

        // Initialize Google Play Billing for Monthly Ad-Free Subscription
        BillingManager.init(this)

        // Initialize official Google Play In-App Updates (backend service)
        playUpdateManager = GooglePlayUpdateManager(this)
        playUpdateManager.checkForUpdates()

        setContent {
            val uiState by qiblaViewModel.uiState.collectAsStateWithLifecycle()

            val localizedContext = remember(uiState.appLanguageCode) {
                AppLanguageManager.createLocalizedContext(this, uiState.appLanguageCode)
            }
            val localizedConfiguration = remember(uiState.appLanguageCode) {
                val config = Configuration(resources.configuration)
                config.setLocale(AppLanguageManager.getLocaleForCode(uiState.appLanguageCode))
                config
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfiguration,
                LocalActivityResultRegistryOwner provides this,
                LocalOnBackPressedDispatcherOwner provides this
            ) {
                MyApplicationTheme(darkTheme = uiState.darkThemeEnabled) {
                    if (!uiState.isLanguageOnboardingCompleted) {
                        LanguageSelectionDialog(
                            currentLanguageCode = uiState.appLanguageCode,
                            isOnboarding = true,
                            onDismiss = {},
                            onLanguageSelected = { chosenLang ->
                                qiblaViewModel.completeLanguageOnboarding(chosenLang)
                            }
                        )
                    }

                    QiblaMainScreen(
                        uiState = uiState,
                        onSelectTab = { tab ->
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.selectTab(tab)
                        },
                        onPermissionResult = qiblaViewModel::onPermissionResult,
                        onRefreshGps = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.refreshLiveGpsLocation()
                        },
                        onManualRotate = qiblaViewModel::setManualHeadingAngle,
                        onSnapToQibla = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.snapToQiblaForPreview()
                        },
                        onResumeSensor = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.resumeLiveSensorTracking()
                        },
                        onToggleTrueNorth = qiblaViewModel::toggleTrueNorthCorrection,
                        onToggleHaptic = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.toggleHapticFeedback(it)
                        },
                        onToggleDarkTheme = qiblaViewModel::toggleDarkTheme,
                        onToggleAdhanAuto = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.toggleAdhanAutoEnabled(it)
                        },
                        onSelectMuezzin = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.selectMuezzin(it)
                        },
                        onTogglePrayerAdhan = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.togglePrayerAdhanAlarm(it)
                        },
                        onTestOrStopAdhan = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.testOrStopAdhanPlayback(it)
                        },
                        onStopAdhan = qiblaViewModel::stopAdhanPlayback,
                        onRefreshMosques = {
                            IronSourceAdManager.recordClick(this)
                            qiblaViewModel.refreshNearbyMosques(force = true)
                        },
                        onSelectLanguage = { chosenLang ->
                            qiblaViewModel.selectLanguage(chosenLang)
                        },
                        onIncrementTasbih = qiblaViewModel::incrementTasbih,
                        onResetTasbih = qiblaViewModel::resetTasbih,
                        onSetTasbihTarget = qiblaViewModel::setTasbihTarget,
                        onSelectTasbihDzikir = qiblaViewModel::selectTasbihDzikir,
                        onSelectTasbihBeadTheme = qiblaViewModel::setTasbihBeadTheme,
                        onToggleTasbihSound = qiblaViewModel::toggleTasbihSound,
                        onToggleTasbihVibration = qiblaViewModel::toggleTasbihVibration
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        IronSourceAdManager.onResume(this)
        BillingManager.queryActivePurchases()
        playUpdateManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        IronSourceAdManager.onPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        playUpdateManager.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWidgetDeepLinkIntent(intent)
    }

    private fun handleWidgetDeepLinkIntent(intent: Intent?) {
        val targetTab = intent?.getStringExtra(QiblaPrayerWidgetProvider.EXTRA_OPEN_TAB) ?: return
        when (targetTab) {
            QiblaPrayerWidgetProvider.TAB_PRAYER_TIMES -> {
                qiblaViewModel.selectTab(QiblaNavigationTab.PRAYER_TIMES)
            }
            QiblaPrayerWidgetProvider.TAB_COMPASS -> {
                qiblaViewModel.selectTab(QiblaNavigationTab.COMPASS)
            }
        }
    }

    private fun ensureWebViewCacheDirs() {
        try {
            val codeCacheDir = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache")
            File(codeCacheDir, "js").mkdirs()
            File(codeCacheDir, "wasm").mkdirs()
        } catch (_: Exception) {
            // Ignore any cache directory initialization errors
        }
    }
}
