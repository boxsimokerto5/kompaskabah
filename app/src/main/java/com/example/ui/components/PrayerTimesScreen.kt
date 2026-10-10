package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.MeccaGold
import com.example.ui.theme.MeccaGoldBright
import com.example.ui.theme.SanctuaryDeepBg

@Composable
fun PrayerTimesScreen(
    uiState: QiblaUiState,
    onOpenCityPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Kaaba Banner with Next Prayer Highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = MeccaGold.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_kaaba_daylight_1791628182842),
                contentDescription = "Ilustrasi Ka'bah Masjidil Haram",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SanctuaryDeepBg.copy(alpha = 0.35f),
                                SanctuaryDeepBg.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onOpenCityPicker,
                        shape = RoundedCornerShape(50),
                        color = SanctuaryDeepBg.copy(alpha = 0.75f),
                        modifier = Modifier.testTag("prayer_location_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MeccaGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.locationLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = IvoryTextPrimary
                            )
                        }
                    }

                    if (uiState.nextPrayerCountdownLabel.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AlignedEmerald.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = uiState.nextPrayerCountdownLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = AlignedEmerald,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "WAKTU SHOLAT BERIKUTNYA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MeccaGoldBright
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.nextPrayerName,
                            style = MaterialTheme.typography.displayMedium,
                            color = IvoryTextPrimary
                        )
                        Text(
                            text = uiState.nextPrayerTimeFormatted,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MeccaGoldBright
                        )
                    }
                }
            }
        }

        // 2. Geodesic Summary Strip
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Arah Kiblat Lokasi Ini",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%.2f° dari Utara Sejati".format(uiState.qiblaBearingTrueNorthDeg),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Jarak ke Masjidil Haram",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%,.0f km".format(uiState.distanceToKaabaKm),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 3. Daily Prayer Schedule List
        Text(
            text = "Jadwal Waktu Sholat Hari Ini (Standar Kemenag RI / MWL)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            uiState.prayerTimes.forEach { item ->
                val isNext = item.nameId == uiState.nextPrayerName
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNext) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isNext) {
                                Modifier.border(
                                    width = 1.5.dp,
                                    color = MeccaGold,
                                    shape = RoundedCornerShape(18.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .testTag("prayer_item_${item.nameId.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
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
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
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
                                    tint = if (isNext) MeccaGoldBright else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = item.nameId,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.arabicName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isNext) MeccaGoldBright else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.formattedTime,
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isNext) MeccaGoldBright else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
