package com.example.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.QiblaCalculator
import com.example.ui.components.CityPickerSheet
import com.example.ui.components.PrayerTimesScreen
import com.example.ui.components.QiblaCompassCanvas
import com.example.ui.components.SensorDiagnosticsScreen
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import com.example.ui.theme.MeccaGold
import com.example.ui.theme.MeccaGoldBright

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaMainScreen(
    uiState: QiblaUiState,
    onSelectTab: (QiblaNavigationTab) -> Unit,
    onPermissionResult: (Boolean) -> Unit,
    onRefreshGps: () -> Unit,
    onSelectCity: (com.example.domain.CityPreset) -> Unit,
    onSaveCustomCoordinates: (Double, Double, String) -> Unit,
    onManualRotate: (Float) -> Unit,
    onSnapToQibla: () -> Unit,
    onResumeSensor: () -> Unit,
    onToggleTrueNorth: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSetShowCityPicker: (Boolean) -> Unit
) {
    val context = LocalContext.current

    if (uiState.selectedTab != QiblaNavigationTab.COMPASS) {
        BackHandler {
            onSelectTab(QiblaNavigationTab.COMPASS)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onPermissionResult(granted)
    }

    // Automatically request location permission silently on first launch if not yet granted
    var hasAutoRequestedGps by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!uiState.hasLocationPermission && !hasAutoRequestedGps) {
            hasAutoRequestedGps = true
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Trigger gentle haptic feedback pulse when crossing into alignment with the Holy Kaaba
    var wasAlignedPreviously by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isAlignedWithKaaba, uiState.hapticFeedbackEnabled) {
        if (uiState.isAlignedWithKaaba && !wasAlignedPreviously && uiState.hapticFeedbackEnabled) {
            triggerGentleHapticPulse(context)
        }
        wasAlignedPreviously = uiState.isAlignedWithKaaba
    }

    if (uiState.showCityPickerSheet) {
        CityPickerSheet(
            currentLocationLabel = uiState.locationLabel,
            currentLatitude = uiState.latitude,
            currentLongitude = uiState.longitude,
            isLiveGpsActive = uiState.isLiveGpsActive,
            onRequestLiveGps = {
                if (uiState.hasLocationPermission) {
                    onRefreshGps()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            onSelectCity = onSelectCity,
            onSaveCustomCoordinates = onSaveCustomCoordinates,
            onDismiss = { onSetShowCityPicker(false) }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isWideScreen) {
                    QiblaBottomNavigationBar(
                        selectedTab = uiState.selectedTab,
                        onSelectTab = onSelectTab
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    QiblaSideNavigationRail(
                        selectedTab = uiState.selectedTab,
                        onSelectTab = onSelectTab
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (uiState.selectedTab) {
                        QiblaNavigationTab.COMPASS -> {
                            CompassDashboardContent(
                                uiState = uiState,
                                isWideScreen = isWideScreen,
                                onOpenCityPicker = { onSetShowCityPicker(true) },
                                onRequestGpsClick = {
                                    if (uiState.hasLocationPermission) {
                                        onRefreshGps()
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                },
                                onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) },
                                onManualRotate = onManualRotate,
                                onSnapToQibla = onSnapToQibla,
                                onResumeSensor = onResumeSensor
                            )
                        }

                        QiblaNavigationTab.PRAYER_TIMES -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            ) {
                                TopQiblaHeaderBar(
                                    uiState = uiState,
                                    onOpenLocationPicker = { onSetShowCityPicker(true) },
                                    onRequestGpsClick = {
                                        if (uiState.hasLocationPermission) {
                                            onRefreshGps()
                                        } else {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        }
                                    },
                                    onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) }
                                )
                                PrayerTimesScreen(
                                    uiState = uiState,
                                    onOpenCityPicker = { onSetShowCityPicker(true) }
                                )
                            }
                        }

                        QiblaNavigationTab.SENSOR_DIAGNOSTICS -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            ) {
                                TopQiblaHeaderBar(
                                    uiState = uiState,
                                    onOpenLocationPicker = { onSetShowCityPicker(true) },
                                    onRequestGpsClick = {
                                        if (uiState.hasLocationPermission) {
                                            onRefreshGps()
                                        } else {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        }
                                    },
                                    onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) }
                                )
                                SensorDiagnosticsScreen(
                                    uiState = uiState,
                                    onToggleTrueNorth = onToggleTrueNorth,
                                    onToggleHaptic = onToggleHaptic,
                                    onToggleDarkTheme = onToggleDarkTheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopQiblaHeaderBar(
    uiState: QiblaUiState,
    onOpenLocationPicker: () -> Unit,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onOpenLocationPicker() }
                    .padding(vertical = 2.dp)
                    .testTag("header_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(id = R.string.cd_location_button),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = uiState.locationLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onRequestGpsClick,
                modifier = Modifier.testTag("refresh_gps_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(id = R.string.btn_refresh_gps),
                    tint = if (uiState.isLiveGpsActive) AlignedEmerald else MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onToggleHaptic,
                modifier = Modifier.testTag("toggle_haptic_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = stringResource(id = R.string.cd_haptic_button),
                    tint = if (uiState.hapticFeedbackEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    }
                )
            }
        }
    }
}

@Composable
private fun CompassDashboardContent(
    uiState: QiblaUiState,
    isWideScreen: Boolean,
    onOpenCityPicker: () -> Unit,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit,
    onManualRotate: (Float) -> Unit,
    onSnapToQibla: () -> Unit,
    onResumeSensor: () -> Unit
) {
    val scrollState = rememberScrollState()

    if (isWideScreen) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Full-bleed edge-to-edge Kaaba Daylight Hero Banner across the top
            FullWidthKaabaDaylightHero(
                uiState = uiState,
                onOpenCityPicker = onOpenCityPicker,
                onRequestGpsClick = onRequestGpsClick,
                onToggleHaptic = onToggleHaptic
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    QiblaCompassCanvas(
                        deviceHeadingDeg = uiState.effectiveDeviceHeadingDeg,
                        qiblaBearingDeg = uiState.qiblaBearingTrueNorthDeg,
                        pitchDeg = uiState.pitchDeg,
                        rollDeg = uiState.rollDeg,
                        isAligned = uiState.isAlignedWithKaaba,
                        isNearAlignment = uiState.isNearAlignmentWithKaaba,
                        isDarkTheme = uiState.darkThemeEnabled,
                        onManualRotate = onManualRotate,
                        modifier = Modifier.widthIn(max = 390.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CleanTelemetryStrip(uiState = uiState)
                    QuickSimulationActionsRow(
                        uiState = uiState,
                        onSnapToQibla = onSnapToQibla,
                        onResumeSensor = onResumeSensor
                    )
                }
            }
        }
    } else {
        // Mobile Portrait Layout: Full Left-to-Right Edge-to-Edge Daylight Kaaba Hero blending seamlessly into the bright page
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Full-Bleed Left-to-Right Bright Blue-Sky Kaaba Hero with Seamless Bottom Gradient
            FullWidthKaabaDaylightHero(
                uiState = uiState,
                onOpenCityPicker = onOpenCityPicker,
                onRequestGpsClick = onRequestGpsClick,
                onToggleHaptic = onToggleHaptic
            )

            // 2. Centerpiece Luminous Kaaba Compass Dial
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QiblaCompassCanvas(
                    deviceHeadingDeg = uiState.effectiveDeviceHeadingDeg,
                    qiblaBearingDeg = uiState.qiblaBearingTrueNorthDeg,
                    pitchDeg = uiState.pitchDeg,
                    rollDeg = uiState.rollDeg,
                    isAligned = uiState.isAlignedWithKaaba,
                    isNearAlignment = uiState.isNearAlignmentWithKaaba,
                    isDarkTheme = uiState.darkThemeEnabled,
                    onManualRotate = onManualRotate,
                    modifier = Modifier.widthIn(max = 365.dp)
                )

                // 3. Clean Geodesic Summary Strip
                CleanTelemetryStrip(
                    uiState = uiState,
                    modifier = Modifier.widthIn(max = 560.dp)
                )

                // 4. Minimalist Quick Alignment Chip Row
                QuickSimulationActionsRow(
                    uiState = uiState,
                    onSnapToQibla = onSnapToQibla,
                    onResumeSensor = onResumeSensor,
                    modifier = Modifier.widthIn(max = 560.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Full-width (edge-to-edge left-to-right) bright blue-sky Kaaba hero header
 * that smoothly gradients at the bottom into `MaterialTheme.colorScheme.background`
 * for an airy, luminous, seamless look.
 */
@Composable
private fun FullWidthKaabaDaylightHero(
    uiState: QiblaUiState,
    onOpenCityPicker: () -> Unit,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit
) {
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(255.dp)
            .testTag("kaaba_atmosphere_banner")
    ) {
        // Full-bleed Left-to-Right Bright Blue Sky Kaaba Illustration
        Image(
            painter = painterResource(id = R.drawable.img_kaaba_daylight_1791628182842),
            contentDescription = stringResource(id = R.string.cd_kaaba_hero),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle top sky scrim so top header text/icons remain crisp against the bright blue sky
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF062338).copy(alpha = 0.48f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Smooth bottom gradient fade blending the Kaaba courtyard directly into the page background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            pageBackgroundColor.copy(alpha = 0.72f),
                            pageBackgroundColor
                        )
                    )
                )
        )

        // Foreground Content inside the Hero Header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar overlaid on the bright blue sky
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.88f))
                        .clickable { onOpenCityPicker() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("header_location_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(id = R.string.cd_location_button),
                        tint = DeepEmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.locationLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = CharcoalText,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = onRequestGpsClick,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("refresh_gps_icon_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = stringResource(id = R.string.btn_refresh_gps),
                                tint = if (uiState.isLiveGpsActive) AlignedEmerald else DeepEmeraldPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onToggleHaptic,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("toggle_haptic_icon_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = stringResource(id = R.string.cd_haptic_button),
                                tint = if (uiState.hapticFeedbackEnabled) {
                                    DeepEmeraldPrimary
                                } else {
                                    CharcoalText.copy(alpha = 0.38f)
                                },
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Info Row floating on the soft gradient transition
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.isAlignedWithKaaba) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = AlignedEmeraldGlow
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tepat Kiblat",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Sudut Kiblat ${"%.1f°".format(uiState.qiblaBearingTrueNorthDeg)} (${
                            QiblaCalculator.getIndonesianCardinalDirection(uiState.qiblaBearingTrueNorthDeg)
                        }) • ${"%,.0f".format(uiState.distanceToKaabaKm)} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = DeepGoldAccent.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(14.dp)
                    )
                ) {
                    Text(
                        text = "%.0f°".format(uiState.effectiveDeviceHeadingDeg),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanTelemetryStrip(
    uiState: QiblaUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CompactMetricCard(
            label = stringResource(id = R.string.label_qibla_bearing),
            primaryValue = "%.1f°".format(uiState.qiblaBearingTrueNorthDeg),
            subtitle = "Utara Sejati",
            isHighlighted = true,
            modifier = Modifier
                .weight(1f)
                .testTag("metric_qibla_bearing")
        )
        CompactMetricCard(
            label = stringResource(id = R.string.label_current_heading),
            primaryValue = "%.1f°".format(uiState.effectiveDeviceHeadingDeg),
            subtitle = QiblaCalculator.getIndonesianCardinalDirection(uiState.effectiveDeviceHeadingDeg)
                .substringBefore(" "),
            isHighlighted = false,
            modifier = Modifier
                .weight(1f)
                .testTag("metric_device_heading")
        )
        CompactMetricCard(
            label = uiState.nextPrayerName,
            primaryValue = uiState.nextPrayerTimeFormatted,
            subtitle = uiState.nextPrayerCountdownLabel.ifBlank { "Waktu Sholat" },
            isHighlighted = false,
            modifier = Modifier
                .weight(1f)
                .testTag("metric_next_prayer")
        )
    }
}

@Composable
private fun CompactMetricCard(
    label: String,
    primaryValue: String,
    subtitle: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.border(
            width = 1.dp,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            },
            shape = RoundedCornerShape(18.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = primaryValue,
                style = MaterialTheme.typography.titleLarge,
                color = if (isHighlighted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickSimulationActionsRow(
    uiState: QiblaUiState,
    onSnapToQibla: () -> Unit,
    onResumeSensor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = uiState.isAlignedWithKaaba,
            onClick = onSnapToQibla,
            label = {
                Text(
                    text = if (uiState.isAlignedWithKaaba) {
                        "Menghadap Ka'bah (${"%.1f°".format(uiState.qiblaBearingTrueNorthDeg)})"
                    } else {
                        "Simulasi Tepat Kiblat"
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AlignedEmerald.copy(alpha = 0.16f),
                selectedLabelColor = AlignedEmerald,
                selectedLeadingIconColor = AlignedEmerald
            ),
            modifier = Modifier.testTag("snap_to_qibla_chip")
        )

        if (uiState.isManualRotationOverride && uiState.isHardwareSensorAvailable) {
            Spacer(modifier = Modifier.width(10.dp))
            FilterChip(
                selected = false,
                onClick = onResumeSensor,
                label = { Text("Sensor Otomatis", style = MaterialTheme.typography.labelMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("resume_sensor_chip")
            )
        }
    }
}

@Composable
private fun QiblaBottomNavigationBar(
    selectedTab: QiblaNavigationTab,
    onSelectTab: (QiblaNavigationTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == QiblaNavigationTab.COMPASS,
            onClick = { onSelectTab(QiblaNavigationTab.COMPASS) },
            icon = { Icon(Icons.Default.Explore, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_compass)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_compass")
        )
        NavigationBarItem(
            selected = selectedTab == QiblaNavigationTab.PRAYER_TIMES,
            onClick = { onSelectTab(QiblaNavigationTab.PRAYER_TIMES) },
            icon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_prayer)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_prayer")
        )
        NavigationBarItem(
            selected = selectedTab == QiblaNavigationTab.SENSOR_DIAGNOSTICS,
            onClick = { onSelectTab(QiblaNavigationTab.SENSOR_DIAGNOSTICS) },
            icon = { Icon(Icons.Default.CompassCalibration, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_sensor)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_sensor")
        )
    }
}

@Composable
private fun QiblaSideNavigationRail(
    selectedTab: QiblaNavigationTab,
    onSelectTab: (QiblaNavigationTab) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Spacer(modifier = Modifier.weight(1f))
        NavigationRailItem(
            selected = selectedTab == QiblaNavigationTab.COMPASS,
            onClick = { onSelectTab(QiblaNavigationTab.COMPASS) },
            icon = { Icon(Icons.Default.Explore, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_compass)) },
            modifier = Modifier.testTag("rail_tab_compass")
        )
        Spacer(modifier = Modifier.height(12.dp))
        NavigationRailItem(
            selected = selectedTab == QiblaNavigationTab.PRAYER_TIMES,
            onClick = { onSelectTab(QiblaNavigationTab.PRAYER_TIMES) },
            icon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_prayer)) },
            modifier = Modifier.testTag("rail_tab_prayer")
        )
        Spacer(modifier = Modifier.height(12.dp))
        NavigationRailItem(
            selected = selectedTab == QiblaNavigationTab.SENSOR_DIAGNOSTICS,
            onClick = { onSelectTab(QiblaNavigationTab.SENSOR_DIAGNOSTICS) },
            icon = { Icon(Icons.Default.CompassCalibration, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_sensor)) },
            modifier = Modifier.testTag("rail_tab_sensor")
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

private fun triggerGentleHapticPulse(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(45L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(45L)
        }
    } catch (e: Exception) {
        // Ignore if vibrator hardware is absent
    }
}
