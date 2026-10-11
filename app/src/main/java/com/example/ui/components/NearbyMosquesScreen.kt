package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.Locale
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ads.IronSourceAdManager
import com.example.data.NearbyMosqueItem
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import com.example.ui.theme.MeccaGold

enum class MosqueFilter(val label: String) {
    ALL("Semua"),
    ONLY_MASJID("Masjid"),
    ONLY_MUSHOLLA("Musholla"),
    NAMED_ONLY("Nama Terdaftar")
}

/**
 * Dedicated "Masjid Terdekat" screen featuring:
 * 1. Full-bleed left-to-right gradient hero banner of Santri walking on a garden pathway in front of a Mosque under a bright blue sky.
 * 2. Automatic GPS-based list of closest Mosques & Mushollas sorted by distance (meters/km).
 * 3. Direct 1-tap Google Maps navigation (route directions or search on Google Maps).
 */
@Composable
fun NearbyMosquesScreen(
    uiState: QiblaUiState,
    onRefreshGpsAndMosques: () -> Unit,
    onToggleHaptic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    var currentFilter by rememberSaveable { mutableStateOf(MosqueFilter.ALL) }

    val filteredMosques = remember(uiState.nearbyMosques, currentFilter) {
        when (currentFilter) {
            MosqueFilter.ALL -> uiState.nearbyMosques
            MosqueFilter.ONLY_MASJID -> uiState.nearbyMosques.filter { it.categoryLabel == "Masjid" }
            MosqueFilter.ONLY_MUSHOLLA -> uiState.nearbyMosques.filter { it.categoryLabel == "Musholla" }
            MosqueFilter.NAMED_ONLY -> uiState.nearbyMosques.filter { it.hasCustomName }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Full-Bleed Left-to-Right Santri, Garden Pathway & Mosque Hero Banner with Seamless Bottom Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(255.dp)
                .testTag("mosques_santri_hero_banner")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_santri_mosque_park_1791639333443),
                contentDescription = "Ilustrasi Santri di Jalan Taman Depan Masjid",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            // Subtle top sky scrim for crisp readability of top status pills
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

            // Smooth bottom gradient fade blending the garden pathway seamlessly into the page background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                pageBackgroundColor.copy(alpha = 0.76f),
                                pageBackgroundColor
                            )
                        )
                    )
            )

            // Foreground Header & Title inside the Full-Bleed Hero
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Automatic GPS Location Pill & Action Buttons
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
                            .clickable { onRefreshGpsAndMosques() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("mosques_location_chip")
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
                            onClick = onRefreshGpsAndMosques,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.88f),
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("mosques_refresh_button")
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
                    }
                }

                // Bottom Row of Hero: Title & Mosque Count Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text(
                            text = "PENCARIAN OTOMATIS GPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Masjid Terdekat",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daftar Masjid & Musholla di sekitar posisi Anda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (uiState.nearbyMosques.isNotEmpty()) {
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
                                text = "${uiState.nearbyMosques.size} Lokasi",
                                style = MaterialTheme.typography.labelMedium,
                                color = AlignedEmerald,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Content Body: Quick Google Maps Radar Action + Filter Chips + Sorted Mosques List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Action Row: 3D Raised Pulsing "Buka Google Maps" Button (Top-Left) & Refresh Button
            val mapsInfiniteTransition = rememberInfiniteTransition(label = "maps_btn_pulse")
            val mapsBtnPulseScale by mapsInfiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.035f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "maps_btn_scale"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game-like 3D Pulsing Raised Action Button for Open Google Maps (Top-Left)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .scale(mapsBtnPulseScale)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = DeepEmeraldPrimary.copy(alpha = 0.35f),
                            spotColor = DeepEmeraldPrimary
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF22C55E),
                                    Color(0xFF15803D),
                                    Color(0xFF0F5B2D)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF86EFAC),
                                    Color(0xFF15803D)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White)
                        ) {
                            IronSourceAdManager.showRewardedVideoForMosque(context) {
                                openGoogleMapsSearchNearby(
                                    context = context,
                                    lat = uiState.latitude,
                                    lon = uiState.longitude
                                )
                            }
                        }
                        .padding(horizontal = 14.dp)
                        .testTag("open_google_maps_mosques_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Buka Google Maps",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        IronSourceAdManager.recordClick(context)
                        onRefreshGpsAndMosques()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("refresh_mosques_list_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Perbarui daftar masjid",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Segarkan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Kotak Informasi Navigasi Langsung dengan Tanda Panah Animasi Berdenyut 3D ke Kiri Atas
            MosqueRadarInfoBox(
                uiState = uiState,
                onOpenGoogleMaps = {
                    IronSourceAdManager.showRewardedVideoForMosque(context) {
                        openGoogleMapsSearchNearby(
                            context = context,
                            lat = uiState.latitude,
                            lon = uiState.longitude
                        )
                    }
                }
            )

            // Interactive Filter Chips Row
            if (uiState.nearbyMosques.isNotEmpty()) {
                val chipScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(chipScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MosqueFilter.values().forEach { filter ->
                        val isSelected = currentFilter == filter
                        val count = when (filter) {
                            MosqueFilter.ALL -> uiState.nearbyMosques.size
                            MosqueFilter.ONLY_MASJID -> uiState.nearbyMosques.count { it.categoryLabel == "Masjid" }
                            MosqueFilter.ONLY_MUSHOLLA -> uiState.nearbyMosques.count { it.categoryLabel == "Musholla" }
                            MosqueFilter.NAMED_ONLY -> uiState.nearbyMosques.count { it.hasCustomName }
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                IronSourceAdManager.recordClick(context)
                                currentFilter = filter
                            },
                            label = {
                                Text(
                                    text = "${filter.label} ($count)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Loading / Empty / Filtered List States
            when {
                uiState.isLoadingMosques && uiState.nearbyMosques.isEmpty() -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Mencari Masjid & Musholla terdekat dari titik GPS Anda…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                uiState.nearbyMosques.isEmpty() -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = DeepGoldAccent,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = uiState.mosquesErrorMessage
                                    ?: "Belum ada data masjid dalam radius terdekat.",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Pastikan koneksi internet & GPS aktif, atau buka langsung peta masjid terdekat melalui Google Maps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                filteredMosques.isEmpty() -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = DeepGoldAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Tidak ada tempat ibadah yang cocok dengan filter saat ini.",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Silakan pilih filter 'Semua' untuk melihat seluruh tempat ibadah terdekat, atau gunakan tombol Buka Google Maps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    filteredMosques.forEachIndexed { index, mosque ->
                        NearbyMosqueCard(
                            mosque = mosque,
                            isClosest = index == 0,
                            onNavigateClick = {
                                IronSourceAdManager.showRewardedVideoForMosque(context) {
                                    openGoogleMapsDirectionsToMosque(context, mosque)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun NearbyMosqueCard(
    mosque: NearbyMosqueItem,
    isClosest: Boolean,
    onNavigateClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClosest) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isClosest) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onNavigateClick() }
            .border(
                width = if (isClosest) 1.5.dp else 1.dp,
                color = if (isClosest) {
                    MeccaGold
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                },
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("mosque_card_${mosque.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = if (isClosest) {
                                MeccaGold.copy(alpha = 0.25f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = if (isClosest) DeepGoldAccent else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isClosest) {
                                AlignedEmerald.copy(alpha = 0.16f)
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                            }
                        ) {
                            Text(
                                text = if (isClosest) "Terdekat • ${mosque.categoryLabel}" else mosque.categoryLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isClosest) AlignedEmerald else MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        if (mosque.hasCustomName) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = DeepGoldAccent.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "Terdaftar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepGoldAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = mosque.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${mosque.addressOrArea} • Arah ${mosque.cardinalDirection}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = mosque.formattedDistance,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Rute ke ${mosque.name}",
                        tint = AlignedEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rute Maps",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlignedEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun openGoogleMapsDirectionsToMosque(context: Context, mosque: NearbyMosqueItem) {
    try {
        val encodedLabel = Uri.encode(mosque.name)
        val gmmIntentUri = Uri.parse(
            "geo:${mosque.latitude},${mosque.longitude}?q=${mosque.latitude},${mosque.longitude}($encodedLabel)"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${mosque.latitude},${mosque.longitude}"
            )
            context.startActivity(
                Intent(Intent.ACTION_VIEW, browserUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    } catch (e: Exception) {
        // Fallback to browser URL
        try {
            val browserUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${mosque.latitude},${mosque.longitude}"
            )
            context.startActivity(
                Intent(Intent.ACTION_VIEW, browserUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: Exception) {
        }
    }
}

private fun openGoogleMapsSearchNearby(context: Context, lat: Double, lon: Double) {
    try {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=masjid+terdekat")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserUri = Uri.parse(
                "https://www.google.com/maps/search/masjid+terdekat/@$lat,$lon,15z"
            )
            context.startActivity(
                Intent(Intent.ACTION_VIEW, browserUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    } catch (e: Exception) {
        // Ignore if no browser/maps handler is installed
    }
}

/**
 * 3D pulsating animated arrow pointing diagonally towards the top-left (↖),
 * specifically guiding the user towards the "Buka Google Maps" button.
 * Incorporates specular 3D bevels, depth extrusion shadows, radial glowing aura,
 * and dynamic directional pulsing towards the Northwest quadrant.
 */
@Composable
fun Pulsing3DTopLeftArrow(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arrow_3d_pulse_transition")

    // Pulsing scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Directional bounce translation towards top-left (negative X and negative Y)
    val bounceShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_shift"
    )

    // Outer glow aura alpha oscillation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .scale(pulseScale)
    ) {
        Canvas(
            modifier = Modifier
                .size(56.dp)
                .offset(x = bounceShift.dp, y = bounceShift.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension * 0.44f

            // 1. Pulsing Outer Radial Halo / Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF22C55E).copy(alpha = glowAlpha * 0.60f),
                        Color(0xFFEAB308).copy(alpha = glowAlpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.35f
                ),
                radius = outerRadius * 1.35f,
                center = center
            )

            // 2. 3D Badge Base Plate (Plaque) with Beveled Edge
            // Base shadow offset down-right (+2.5, +3.5)
            drawCircle(
                color = Color(0xFF031A0F).copy(alpha = 0.55f),
                radius = outerRadius,
                center = Offset(center.x + 2.5f, center.y + 3.5f)
            )

            // Base plate gradient (deep rich emerald tone)
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF166534),
                        Color(0xFF0F4424),
                        Color(0xFF062B16)
                    ),
                    start = Offset(center.x - outerRadius, center.y - outerRadius),
                    end = Offset(center.x + outerRadius, center.y + outerRadius)
                ),
                radius = outerRadius,
                center = center
            )

            // Specular border ring for 3D metallic bevel look
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF86EFAC), // Highlight top-left
                        Color(0xFF22C55E),
                        Color(0xFF052E16)  // Shadow bottom-right
                    ),
                    start = Offset(center.x - outerRadius, center.y - outerRadius),
                    end = Offset(center.x + outerRadius, center.y + outerRadius)
                ),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // 3. Arrow pointing Top-Left:
            // Drawn pointing straight UP, then rotated by -45 degrees around center
            rotate(degrees = -45f, pivot = center) {
                val arrowH = outerRadius * 1.25f
                val arrowW = outerRadius * 0.85f
                val stemW = arrowW * 0.42f
                val headH = arrowH * 0.52f

                val topY = center.y - arrowH * 0.52f
                val headBottomY = topY + headH
                val stemBottomY = center.y + arrowH * 0.48f

                // Construct arrow polygon
                val arrowPath = Path().apply {
                    moveTo(center.x, topY) // Tip
                    lineTo(center.x + arrowW * 0.55f, headBottomY) // Right arrowhead wing
                    lineTo(center.x + stemW * 0.5f, headBottomY) // Right shoulder
                    lineTo(center.x + stemW * 0.5f, stemBottomY) // Right bottom
                    lineTo(center.x - stemW * 0.5f, stemBottomY) // Left bottom
                    lineTo(center.x - stemW * 0.5f, headBottomY) // Left shoulder
                    lineTo(center.x - arrowW * 0.55f, headBottomY) // Left arrowhead wing
                    close()
                }

                // A) 3D Depth Extrusion (Shadow layer offset down-right)
                val shadowPath = Path().apply {
                    moveTo(center.x + 2.5f, topY + 3f)
                    lineTo(center.x + arrowW * 0.55f + 2.5f, headBottomY + 3f)
                    lineTo(center.x + stemW * 0.5f + 2.5f, headBottomY + 3f)
                    lineTo(center.x + stemW * 0.5f + 2.5f, stemBottomY + 3f)
                    lineTo(center.x - stemW * 0.5f + 2.5f, stemBottomY + 3f)
                    lineTo(center.x - stemW * 0.5f + 2.5f, headBottomY + 3f)
                    lineTo(center.x - arrowW * 0.55f + 2.5f, headBottomY + 3f)
                    close()
                }
                drawPath(path = shadowPath, color = Color(0xFF03220F).copy(alpha = 0.75f))

                // B) Vibrant 3D Arrow Gradient (Lime-Emerald to Gold/Cyan)
                drawPath(
                    path = arrowPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFDE047), // Gold highlight at tip
                            Color(0xFF4ADE80), // Vibrant emerald body
                            Color(0xFF16A34A)  // Deep emerald bottom
                        ),
                        startY = topY,
                        endY = stemBottomY
                    )
                )

                // C) 3D Highlight Ridge down the center line & left edge
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(center.x, topY + 2f),
                    end = Offset(center.x, stemBottomY - 2f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Left bevel highlight
                drawLine(
                    color = Color.White.copy(alpha = 0.75f),
                    start = Offset(center.x, topY + 2f),
                    end = Offset(center.x - arrowW * 0.55f, headBottomY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Kotak Informasi Navigasi Google Maps:
 * Prominent information box immediately visible on the nearby mosque page.
 * Contains:
 * - 3D pulsating animated arrow pointing to the top-left (open Google Maps button)
 * - Navigation and satellite radar guidance
 * - Real-time GPS status, current coordinates, search radius, and direct tap affordance.
 */
@Composable
private fun MosqueRadarInfoBox(
    uiState: QiblaUiState,
    onOpenGoogleMaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AlignedEmerald.copy(alpha = 0.70f),
                        MeccaGold.copy(alpha = 0.50f),
                        DeepEmeraldPrimary.copy(alpha = 0.30f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onOpenGoogleMaps() }
            .testTag("mosque_radar_info_box")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Section: 3D Pulsating Arrow Pointing Top-Left + Header Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 3D Animated Pulsating Arrow pointing to Top-Left (↖)
                Pulsing3DTopLeftArrow()

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = AlignedEmerald.copy(alpha = 0.14f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            AlignedEmerald.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "PETUNJUK NAVIGASI ↖",
                                style = MaterialTheme.typography.labelSmall,
                                color = AlignedEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Peta & Rute Google Maps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ketuk tombol hijau di atas kiri untuk melihat peta interaktif satelit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Info Description
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = DeepEmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Google Maps menyediakan panduan navigasi belokan demi belokan (turn-by-turn), info macet real-time, foto aula sholat & tempat wudhu dari jamaah sekitar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                    )
                }
            }

            // 3. Telemetry & Live GPS Status Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Posisi GPS
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "LOKASI GPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.locationLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Koordinat & Radius
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "KOORDINAT",
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%.4f, %.4f", uiState.latitude, uiState.longitude),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 4. Tap action callout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = AlignedEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Langsung buka Google Maps",
                        style = MaterialTheme.typography.labelMedium,
                        color = AlignedEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Buka Peta ➔",
                    style = MaterialTheme.typography.labelMedium,
                    color = DeepEmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
