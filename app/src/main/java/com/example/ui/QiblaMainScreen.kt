package com.example.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.MuezzinVoiceOption
import com.example.domain.QiblaCalculator
import com.example.service.AdhanPlaybackState
import com.example.ui.components.AmalanScreen
import com.example.ui.components.NearbyMosquesScreen
import com.example.ui.components.PrayerTimesScreen
import com.example.ui.components.SettingsInfoScreen
import com.example.ui.components.QiblaArrowScreen
import com.example.ui.components.QiblaCompassCanvas
import com.example.ui.components.SensorDiagnosticsScreen
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent

private val MenuActiveBlue = Color(0xFF1D8FE1)
private val MenuInactiveSoftBlue = Color(0xFFE9F3FB)
private val MenuInactiveIconTint = Color(0xFF6B8A9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaMainScreen(
    uiState: QiblaUiState,
    onSelectTab: (QiblaNavigationTab) -> Unit,
    onPermissionResult: (Boolean) -> Unit,
    onRefreshGps: () -> Unit,
    onManualRotate: (Float) -> Unit,
    onSnapToQibla: () -> Unit,
    onResumeSensor: () -> Unit,
    onToggleTrueNorth: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleAdhanAuto: (Boolean) -> Unit,
    onSelectMuezzin: (MuezzinVoiceOption) -> Unit,
    onTogglePrayerAdhan: (String) -> Unit,
    onTestOrStopAdhan: (String) -> Unit,
    onStopAdhan: () -> Unit,
    onRefreshMosques: () -> Unit = {}
) {
    val context = LocalContext.current
    var showSettingsScreen by remember { mutableStateOf(false) }

    if (showSettingsScreen) {
        SettingsInfoScreen(
            onBack = { showSettingsScreen = false }
        )
        return
    }

    if (uiState.selectedTab != QiblaNavigationTab.COMPASS) {
        BackHandler {
            onSelectTab(QiblaNavigationTab.COMPASS)
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onPermissionResult(locGranted)
    }

    val triggerGpsRefreshOrRequest: () -> Unit = {
        if (uiState.hasLocationPermission) {
            onRefreshGps()
        } else {
            val perms = buildList {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }
            }.toTypedArray()
            permissionsLauncher.launch(perms)
        }
    }

    // Automatically request location + notification permissions on first launch
    var hasAutoRequestedPermissions by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasAutoRequestedPermissions) {
            hasAutoRequestedPermissions = true
            val needsNotifPerm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

            if (!uiState.hasLocationPermission || needsNotifPerm) {
                val perms = buildList {
                    add(Manifest.permission.ACCESS_FINE_LOCATION)
                    add(Manifest.permission.ACCESS_COARSE_LOCATION)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }.toTypedArray()
                permissionsLauncher.launch(perms)
            } else {
                onRefreshGps()
            }
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isWideScreen) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Floating Active Adhan Foreground Player Bar (visible across all tabs when Adhan is playing)
                        AnimatedVisibility(
                            visible = uiState.adhanPlaybackState.isPlaying,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            ActiveAdhanBannerBar(
                                playbackState = uiState.adhanPlaybackState,
                                onStopAdhan = onStopAdhan
                            )
                        }

                        ElegantQiblaBottomMenuBar(
                            selectedTab = uiState.selectedTab,
                            onSelectTab = onSelectTab
                        )
                    }
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
                    if (isWideScreen && uiState.adhanPlaybackState.isPlaying) {
                        ActiveAdhanBannerBar(
                            playbackState = uiState.adhanPlaybackState,
                            onStopAdhan = onStopAdhan
                        )
                    }

                    when (uiState.selectedTab) {
                        QiblaNavigationTab.COMPASS -> {
                            CompassDashboardContent(
                                uiState = uiState,
                                isWideScreen = isWideScreen,
                                onRequestGpsClick = triggerGpsRefreshOrRequest,
                                onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) },
                                onOpenSettings = { showSettingsScreen = true },
                                onManualRotate = onManualRotate,
                                onSnapToQibla = onSnapToQibla,
                                onResumeSensor = onResumeSensor,
                                onSwitchToArrowMode = {
                                    onSelectTab(QiblaNavigationTab.ARROW_POINTER)
                                }
                            )
                        }

                        QiblaNavigationTab.ARROW_POINTER -> {
                            QiblaArrowScreen(
                                uiState = uiState,
                                onBackToCompass = { onSelectTab(QiblaNavigationTab.COMPASS) },
                                onOpenPrayerTimes = { onSelectTab(QiblaNavigationTab.PRAYER_TIMES) },
                                onRefreshGps = triggerGpsRefreshOrRequest,
                                onOpenCalibration = { onSelectTab(QiblaNavigationTab.SENSOR_DIAGNOSTICS) },
                                onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) },
                                onManualRotate = onManualRotate,
                                onSnapToQibla = onSnapToQibla
                            )
                        }

                        QiblaNavigationTab.PRAYER_TIMES -> {
                            PrayerTimesScreen(
                                uiState = uiState,
                                onRefreshGps = triggerGpsRefreshOrRequest,
                                onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) },
                                onToggleAdhanAuto = onToggleAdhanAuto,
                                onSelectMuezzin = onSelectMuezzin,
                                onTogglePrayerAdhan = onTogglePrayerAdhan,
                                onTestOrStopAdhan = onTestOrStopAdhan,
                                onOpenSettings = { showSettingsScreen = true }
                            )
                        }

                        QiblaNavigationTab.AMALAN -> {
                            AmalanScreen(
                                onNavigateBackToCompass = { onSelectTab(QiblaNavigationTab.COMPASS) },
                                onOpenSettings = { showSettingsScreen = true }
                            )
                        }

                        QiblaNavigationTab.NEARBY_MOSQUES -> {
                            NearbyMosquesScreen(
                                uiState = uiState,
                                onRefreshGpsAndMosques = {
                                    onRefreshMosques()
                                    triggerGpsRefreshOrRequest()
                                },
                                onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) }
                            )
                        }

                        QiblaNavigationTab.SENSOR_DIAGNOSTICS -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            ) {
                                TopQiblaHeaderBar(
                                    uiState = uiState,
                                    onRequestGpsClick = triggerGpsRefreshOrRequest,
                                    onToggleHaptic = { onToggleHaptic(!uiState.hapticFeedbackEnabled) },
                                    onOpenSettings = { showSettingsScreen = true }
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

/**
 * Prominent Foreground Adhan Player bar that appears whenever Adhan is actively playing
 * so the user can see which prayer & Muezzin is sounding and stop it with one tap.
 */
@Composable
private fun ActiveAdhanBannerBar(
    playbackState: AdhanPlaybackState,
    onStopAdhan: () -> Unit
) {
    Surface(
        color = DeepEmeraldPrimary,
        contentColor = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_adhan_foreground_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.16f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFFFFE082),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (playbackState.isPreview) {
                            "Kumandang Adzan (${playbackState.prayerName})"
                        } else {
                            "Waktunya Sholat ${playbackState.prayerName}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playbackState.muezzinTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onStopAdhan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerCoral,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("banner_stop_adhan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Berhenti",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TopQiblaHeaderBar(
    uiState: QiblaUiState,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit,
    onOpenSettings: () -> Unit
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
                    .clickable { onRequestGpsClick() }
                    .padding(vertical = 2.dp)
                    .testTag("header_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Lokasi GPS Otomatis",
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

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("settings_header_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Pengaturan & Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CompassDashboardContent(
    uiState: QiblaUiState,
    isWideScreen: Boolean,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit,
    onOpenSettings: () -> Unit,
    onManualRotate: (Float) -> Unit,
    onSnapToQibla: () -> Unit,
    onResumeSensor: () -> Unit,
    onSwitchToArrowMode: () -> Unit
) {
    val scrollState = rememberScrollState()

    if (isWideScreen) {
        Column(modifier = Modifier.fillMaxSize()) {
            FullWidthKaabaDaylightHero(
                uiState = uiState,
                onRequestGpsClick = onRequestGpsClick,
                onToggleHaptic = onToggleHaptic,
                onOpenSettings = onOpenSettings
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
                        onResumeSensor = onResumeSensor,
                        onSwitchToArrowMode = onSwitchToArrowMode
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FullWidthKaabaDaylightHero(
                uiState = uiState,
                onRequestGpsClick = onRequestGpsClick,
                onToggleHaptic = onToggleHaptic,
                onOpenSettings = onOpenSettings
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    modifier = Modifier.widthIn(max = 355.dp)
                )

                CleanTelemetryStrip(
                    uiState = uiState,
                    modifier = Modifier.widthIn(max = 560.dp)
                )

                QuickSimulationActionsRow(
                    uiState = uiState,
                    onSnapToQibla = onSnapToQibla,
                    onResumeSensor = onResumeSensor,
                    onSwitchToArrowMode = onSwitchToArrowMode,
                    modifier = Modifier.widthIn(max = 560.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FullWidthKaabaDaylightHero(
    uiState: QiblaUiState,
    onRequestGpsClick: () -> Unit,
    onToggleHaptic: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .testTag("kaaba_atmosphere_banner")
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_kaaba_daylight_1791628182842),
            contentDescription = stringResource(id = R.string.cd_kaaba_hero),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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
                        .clickable { onRequestGpsClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("header_location_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Lokasi GPS Otomatis",
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

                    Surface(
                        onClick = onOpenSettings,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("settings_hero_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan & Info",
                                tint = DeepEmeraldPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

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
    onSwitchToArrowMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = false,
            onClick = onSwitchToArrowMode,
            label = {
                Text(
                    text = "Mode Anak Panah",
                    style = MaterialTheme.typography.labelMedium,
                    color = MenuActiveBlue
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = MenuActiveBlue,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MenuInactiveSoftBlue
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MenuActiveBlue.copy(alpha = 0.35f)
            ),
            modifier = Modifier.testTag("switch_to_arrow_mode_chip")
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterChip(
            selected = uiState.isAlignedWithKaaba,
            onClick = onSnapToQibla,
            label = {
                Text(
                    text = if (uiState.isAlignedWithKaaba) {
                        "Tepat Kiblat"
                    } else {
                        "Simulasi Kiblat"
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
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = false,
                onClick = onResumeSensor,
                label = { Text("Reset", style = MaterialTheme.typography.labelMedium) },
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
private fun ElegantQiblaBottomMenuBar(
    selectedTab: QiblaNavigationTab,
    onSelectTab: (QiblaNavigationTab) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                thickness = 0.8.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_compass),
                    icon = Icons.Default.Explore,
                    isSelected = selectedTab == QiblaNavigationTab.COMPASS,
                    onClick = { onSelectTab(QiblaNavigationTab.COMPASS) },
                    testTag = "nav_tab_compass"
                )

                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_arrow),
                    icon = Icons.Default.NorthEast,
                    isSelected = selectedTab == QiblaNavigationTab.ARROW_POINTER,
                    onClick = { onSelectTab(QiblaNavigationTab.ARROW_POINTER) },
                    testTag = "nav_tab_arrow"
                )

                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_prayer),
                    icon = Icons.Default.AccessTime,
                    isSelected = selectedTab == QiblaNavigationTab.PRAYER_TIMES,
                    onClick = { onSelectTab(QiblaNavigationTab.PRAYER_TIMES) },
                    testTag = "nav_tab_prayer"
                )

                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_amalan),
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    isSelected = selectedTab == QiblaNavigationTab.AMALAN,
                    onClick = { onSelectTab(QiblaNavigationTab.AMALAN) },
                    testTag = "nav_tab_amalan"
                )

                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_mosques),
                    icon = Icons.Default.Mosque,
                    isSelected = selectedTab == QiblaNavigationTab.NEARBY_MOSQUES,
                    onClick = { onSelectTab(QiblaNavigationTab.NEARBY_MOSQUES) },
                    testTag = "nav_tab_mosques"
                )

                CircularNavMenuItem(
                    label = stringResource(id = R.string.tab_sensor),
                    icon = Icons.Default.CompassCalibration,
                    isSelected = selectedTab == QiblaNavigationTab.SENSOR_DIAGNOSTICS,
                    onClick = { onSelectTab(QiblaNavigationTab.SENSOR_DIAGNOSTICS) },
                    testTag = "nav_tab_sensor"
                )
            }
        }
    }
}

@Composable
private fun CircularNavMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val circleBgColor by animateColorAsState(
        targetValue = if (isSelected) MenuActiveBlue else MenuInactiveSoftBlue,
        animationSpec = tween(250),
        label = "nav_circle_bg"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) Color.White else MenuInactiveIconTint,
        animationSpec = tween(250),
        label = "nav_icon_tint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) MenuActiveBlue else MenuInactiveIconTint,
        animationSpec = tween(250),
        label = "nav_label_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .then(
                    if (isSelected) {
                        Modifier.shadow(elevation = 4.dp, shape = CircleShape)
                    } else {
                        Modifier
                    }
                )
                .background(color = circleBgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1
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
            selected = selectedTab == QiblaNavigationTab.ARROW_POINTER,
            onClick = { onSelectTab(QiblaNavigationTab.ARROW_POINTER) },
            icon = { Icon(Icons.Default.NorthEast, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_arrow)) },
            modifier = Modifier.testTag("rail_tab_arrow")
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
            selected = selectedTab == QiblaNavigationTab.AMALAN,
            onClick = { onSelectTab(QiblaNavigationTab.AMALAN) },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_amalan)) },
            modifier = Modifier.testTag("rail_tab_amalan")
        )
        Spacer(modifier = Modifier.height(12.dp))
        NavigationRailItem(
            selected = selectedTab == QiblaNavigationTab.NEARBY_MOSQUES,
            onClick = { onSelectTab(QiblaNavigationTab.NEARBY_MOSQUES) },
            icon = { Icon(Icons.Default.Mosque, contentDescription = null) },
            label = { Text(stringResource(id = R.string.tab_mosques)) },
            modifier = Modifier.testTag("rail_tab_mosques")
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
