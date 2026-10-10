package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.QiblaMainScreen
import com.example.ui.QiblaViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val qiblaViewModel: QiblaViewModel = viewModel()
            val uiState by qiblaViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.darkThemeEnabled) {
                QiblaMainScreen(
                    uiState = uiState,
                    onSelectTab = qiblaViewModel::selectTab,
                    onPermissionResult = qiblaViewModel::onPermissionResult,
                    onRefreshGps = qiblaViewModel::refreshLiveGpsLocation,
                    onSelectCity = qiblaViewModel::selectPresetCity,
                    onSaveCustomCoordinates = qiblaViewModel::saveCustomCoordinates,
                    onManualRotate = qiblaViewModel::setManualHeadingAngle,
                    onSnapToQibla = qiblaViewModel::snapToQiblaForPreview,
                    onResumeSensor = qiblaViewModel::resumeLiveSensorTracking,
                    onToggleTrueNorth = qiblaViewModel::toggleTrueNorthCorrection,
                    onToggleHaptic = qiblaViewModel::toggleHapticFeedback,
                    onToggleDarkTheme = qiblaViewModel::toggleDarkTheme,
                    onSetShowCityPicker = qiblaViewModel::setShowCityPickerSheet
                )
            }
        }
    }
}
