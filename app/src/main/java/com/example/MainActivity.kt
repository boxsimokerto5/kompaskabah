package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.QiblaMainScreen
import com.example.ui.QiblaNavigationTab
import com.example.ui.QiblaViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ads.IronSourceAdManager
import com.example.widget.QiblaPrayerWidgetProvider

class MainActivity : ComponentActivity() {

    private val qiblaViewModel: QiblaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleWidgetDeepLinkIntent(intent)

        // Initialize ironSource LevelPlay SDK with Meta Audience Network
        IronSourceAdManager.init(this)

        setContent {
            val uiState by qiblaViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.darkThemeEnabled) {
                QiblaMainScreen(
                    uiState = uiState,
                    onSelectTab = qiblaViewModel::selectTab,
                    onPermissionResult = qiblaViewModel::onPermissionResult,
                    onRefreshGps = qiblaViewModel::refreshLiveGpsLocation,
                    onManualRotate = qiblaViewModel::setManualHeadingAngle,
                    onSnapToQibla = qiblaViewModel::snapToQiblaForPreview,
                    onResumeSensor = qiblaViewModel::resumeLiveSensorTracking,
                    onToggleTrueNorth = qiblaViewModel::toggleTrueNorthCorrection,
                    onToggleHaptic = qiblaViewModel::toggleHapticFeedback,
                    onToggleDarkTheme = qiblaViewModel::toggleDarkTheme,
                    onToggleAdhanAuto = qiblaViewModel::toggleAdhanAutoEnabled,
                    onSelectMuezzin = qiblaViewModel::selectMuezzin,
                    onTogglePrayerAdhan = qiblaViewModel::togglePrayerAdhanAlarm,
                    onTestOrStopAdhan = qiblaViewModel::testOrStopAdhanPlayback,
                    onStopAdhan = qiblaViewModel::stopAdhanPlayback,
                    onRefreshMosques = { qiblaViewModel.refreshNearbyMosques(force = true) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        IronSourceAdManager.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSourceAdManager.onPause(this)
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
}
