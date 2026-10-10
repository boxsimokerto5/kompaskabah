package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Widgets
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.MuezzinVoiceOption
import com.example.domain.QiblaCalculator
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import com.example.ui.theme.MeccaGold
import com.example.widget.QiblaPrayerWidgetProvider

@Composable
fun PrayerTimesScreen(
    uiState: QiblaUiState,
    onRefreshGps: () -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleAdhanAuto: (Boolean) -> Unit,
    onSelectMuezzin: (MuezzinVoiceOption) -> Unit,
    onTogglePrayerAdhan: (String) -> Unit,
    onTestOrStopAdhan: (String) -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Full-Bleed Left-to-Right Desert Oasis, Muallim & Camel Hero with Seamless Bottom Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(255.dp)
                .testTag("prayer_desert_hero_banner")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_desert_prayer_hero_1791637232343),
                contentDescription = "Ilustrasi Padang Pasir dan Pohon Kurma",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
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
                                Color(0xFF062338).copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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
                            .clickable { onRefreshGps() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("prayer_location_chip")
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
                            onClick = onRefreshGps,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.88f),
                            modifier = Modifier.size(38.dp)
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
                            modifier = Modifier.size(38.dp)
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
                                .testTag("prayer_settings_icon_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Pengaturan",
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
                        Text(
                            text = "WAKTU SHOLAT BERIKUTNYA",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = uiState.nextPrayerName,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.nextPrayerTimeFormatted,
                                style = MaterialTheme.typography.headlineMedium,
                                color = DeepGoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (uiState.nextPrayerCountdownLabel.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AlignedEmeraldGlow.copy(alpha = 0.16f),
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = AlignedEmerald.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Text(
                                text = uiState.nextPrayerCountdownLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = AlignedEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Content Body: Automatic Adhan Foreground Controls + Daily Prayer Schedule List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Automatic Foreground Adhan Control Card
            AdhanControlCard(
                uiState = uiState,
                onToggleAdhanAuto = onToggleAdhanAuto,
                onSelectMuezzin = onSelectMuezzin,
                onTestOrStopAdhan = { onTestOrStopAdhan(uiState.nextPrayerName) }
            )

            // Home Screen App Widget Live Preview & 1-Tap Pin Card
            HomeScreenWidgetPromoCard(uiState = uiState)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jadwal Waktu Sholat & Alarm Adzan",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Ketuk ikon suara per waktu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.prayerTimes.forEach { item ->
                    val isNext = item.nameId == uiState.nextPrayerName
                    val isAdhanEnabledForPrayer = item.isObligatory &&
                            uiState.adhanAutoEnabled &&
                            uiState.enabledAdhanPrayers.contains(item.nameId)

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNext) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 2.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isNext) 1.5.dp else 1.dp,
                                color = if (isNext) {
                                    MeccaGold
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                                },
                                shape = RoundedCornerShape(18.dp)
                            )
                            .testTag("prayer_item_${item.nameId.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = if (isNext) {
                                                MeccaGold.copy(alpha = 0.25f)
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isNext -> Icons.Default.NotificationsActive
                                            !item.isObligatory -> Icons.Default.WbTwilight
                                            else -> Icons.Default.WbSunny
                                        },
                                        contentDescription = null,
                                        tint = if (isNext) DeepGoldAccent else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.nameId,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isAdhanEnabledForPrayer) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = AlignedEmerald.copy(alpha = 0.14f)
                                            ) {
                                                Text(
                                                    text = "Adzan Aktif",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = AlignedEmerald,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = item.arabicName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.formattedTime,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )

                                if (item.isObligatory) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onTogglePrayerAdhan(item.nameId) },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("toggle_adhan_${item.nameId.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = if (isAdhanEnabledForPrayer) {
                                                Icons.AutoMirrored.Filled.VolumeUp
                                            } else {
                                                Icons.AutoMirrored.Filled.VolumeOff
                                            },
                                            contentDescription = "Aktifkan atau matikan Adzan ${item.nameId}",
                                            tint = if (isAdhanEnabledForPrayer) {
                                                AlignedEmerald
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun AdhanControlCard(
    uiState: QiblaUiState,
    onToggleAdhanAuto: (Boolean) -> Unit,
    onSelectMuezzin: (MuezzinVoiceOption) -> Unit,
    onTestOrStopAdhan: () -> Unit
) {
    val isPlaying = uiState.adhanPlaybackState.isPlaying

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.2.dp,
                color = if (isPlaying) {
                    AlignedEmerald
                } else {
                    DeepGoldAccent.copy(alpha = 0.45f)
                },
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("adhan_settings_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (uiState.adhanAutoEnabled) {
                                    AlignedEmerald.copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.adhanAutoEnabled) {
                                Icons.AutoMirrored.Filled.VolumeUp
                            } else {
                                Icons.Default.NotificationsOff
                            },
                            contentDescription = null,
                            tint = if (uiState.adhanAutoEnabled) {
                                AlignedEmerald
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Notifikasi & Alarm Adzan Otomatis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val nextAlarm = uiState.scheduledAlarms.firstOrNull()
                        Text(
                            text = when {
                                !uiState.adhanAutoEnabled -> "Alarm & notifikasi Adzan otomatis dinonaktifkan"
                                nextAlarm != null -> {
                                    val dayLabel = if (nextAlarm.isToday) "Hari ini" else "Besok"
                                    "AlarmManager aktif • Berikutnya: ${nextAlarm.prayerName} (${nextAlarm.formattedTime} $dayLabel)"
                                }
                                else -> "Notifikasi push & suara Adzan otomatis saat waktu sholat"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = uiState.adhanAutoEnabled,
                    onCheckedChange = onToggleAdhanAuto,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AlignedEmerald
                    ),
                    modifier = Modifier.testTag("switch_auto_adhan")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Text(
                text = "Pilih Suara Muadzin Terkenal:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MuezzinVoiceOption.entries.forEach { option ->
                    val isSelected = uiState.selectedMuezzin == option
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectMuezzin(option) }
                            .border(
                                width = 1.dp,
                                color = if (isSelected) {
                                    DeepGoldAccent.copy(alpha = 0.6f)
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("muezzin_option_${option.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectMuezzin(option) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = option.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Terpilih",
                                    tint = AlignedEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Test / Stop Foreground Adhan Button
            Button(
                onClick = onTestOrStopAdhan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) DangerCoral else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_or_stop_adhan_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPlaying) {
                        "Hentikan Notifikasi & Kumandang Adzan"
                    } else {
                        "Uji Notifikasi Push & Suara Adzan"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeScreenWidgetPromoCard(uiState: QiblaUiState) {
    val context = LocalContext.current
    val cardinalShort = QiblaCalculator.getShortCardinalCode(uiState.qiblaBearingTrueNorthDeg)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DeepGoldAccent.copy(alpha = 0.45f),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("home_screen_widget_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = DeepGoldAccent.copy(alpha = 0.16f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = DeepGoldAccent,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Widget Layar Utama Android",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pantau waktu sholat & arah Kiblat tanpa membuka aplikasi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Miniature Preview of the Android Home Screen Widget
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.2.dp,
                        color = Color(0xFFD4AF37).copy(alpha = 0.65f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("widget_live_preview_box")
            ) {
                // Background: Grand Mosque seen from afar with vibrant blue sky
                Image(
                    painter = painterResource(id = R.drawable.img_grand_mosque_blue_sky_1791642671931),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Atmospheric Scrim Gradient for high contrast & elegance
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color(0x59071B38),
                                0.45f to Color(0x33041226),
                                1.0f to Color(0xD9020A17)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFDF73),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.locationLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (uiState.nextPrayerCountdownLabel.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF064E3B).copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.7f))
                            ) {
                                Text(
                                    text = uiState.nextPrayerCountdownLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6EE7B7),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.1f)) {
                            Text(
                                text = "SHOLAT BERIKUTNYA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFE082),
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = uiState.nextPrayerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.nextPrayerTimeFormatted,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFFFDF73),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0A1C36).copy(alpha = 0.5f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFD4AF37).copy(alpha = 0.6f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${"%.1f°".format(uiState.qiblaBearingTrueNorthDeg)} $cardinalShort",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${"%,.0f".format(uiState.distanceToKaabaKm)} km ke Ka'bah",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    QiblaPrayerWidgetProvider.updateAllWidgets(context)
                    val pinned = QiblaPrayerWidgetProvider.requestPinWidgetToHomeScreen(context)
                    if (!pinned) {
                        Toast.makeText(
                            context,
                            "Widget telah diperbarui! Tekan tahan layar utama ponsel Anda lalu pilih menu 'Widget' -> 'Kompas Kiblat'.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("pin_home_widget_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Widgets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pasang Widget di Layar Utama",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

