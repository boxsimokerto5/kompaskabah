package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.IronSourceAdManager
import com.example.ads.IronSourceBannerAd
import com.example.domain.tasbih.TasbihBeadTheme
import com.example.domain.tasbih.TasbihDzikirPreset
import com.example.domain.tasbih.TasbihPresets
import com.example.domain.tasbih.TasbihState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val GoldHighlight = Color(0xFFFFD54F)
private val DarkCardSurface = Color(0xFF141E28)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalTasbihScreen(
    tasbihState: TasbihState,
    onCountIncrement: () -> Unit,
    onCountReset: () -> Unit,
    onSelectDzikirPreset: (Int) -> Unit,
    onSetTarget: (Int) -> Unit,
    onSelectBeadTheme: (TasbihBeadTheme) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showDzikirSheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSujudVirtueDialog by remember { mutableStateOf(false) }

    val currentPreset = remember(tasbihState.selectedDzikirIndex) {
        TasbihPresets.presets.getOrElse(tasbihState.selectedDzikirIndex) { TasbihPresets.presets.first() }
    }

    val progress = remember(tasbihState.currentCount, tasbihState.targetCount) {
        if (tasbihState.targetCount > 0) {
            (tasbihState.currentCount.toFloat() / tasbihState.targetCount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    // Audio & Haptic Feedback helper
    val triggerFeedback: () -> Unit = {
        triggerHapticAndSound(
            context = context,
            soundEnabled = tasbihState.soundEnabled,
            vibrationEnabled = tasbihState.vibrationEnabled
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top App Bar
        TasbihTopAppBar(
            onNavigateBack = onNavigateBack,
            onOpenDzikirList = { showDzikirSheet = true },
            onOpenThemes = { showThemeDialog = true },
            onResetClick = { showResetDialog = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TOP GRAND MOSQUE SUJUD HERO IMAGE (USER EXPLICIT REQUIREMENT)
            GrandMosqueSujudHeroBanner(
                onClickVirtue = { showSujudVirtueDialog = true }
            )

            // 2. ACTIVE DZIKIR BADGE & QUICK SELECTION PILLS
            ActiveDzikirCard(
                preset = currentPreset,
                onOpenDzikirSelector = { showDzikirSheet = true }
            )

            // 3. TARGET COUNT SELECTOR PILLS
            TargetSelectorPillsRow(
                currentTarget = tasbihState.targetCount,
                onSelectTarget = { target ->
                    onSetTarget(target)
                    IronSourceAdManager.recordClick(context)
                }
            )

            // 4. ANIMATED DIGITAL TASBIH WITH MOVING BEADS
            AnimatedTasbihInteractiveCard(
                tasbihState = tasbihState,
                preset = currentPreset,
                progress = progress,
                onCount = {
                    onCountIncrement()
                    triggerFeedback()
                    IronSourceAdManager.recordClick(context)
                }
            )

            // 5. TACTILE CONTROL TOOLBAR (SOUND, VIBRATION, RESET, THEME)
            TasbihActionButtonsBar(
                soundEnabled = tasbihState.soundEnabled,
                vibrationEnabled = tasbihState.vibrationEnabled,
                beadTheme = tasbihState.beadTheme,
                onToggleSound = { onToggleSound(!tasbihState.soundEnabled) },
                onToggleVibration = { onToggleVibration(!tasbihState.vibrationEnabled) },
                onOpenThemes = { showThemeDialog = true },
                onResetClick = { showResetDialog = true }
            )

            // 6. TOTAL STATS & SUMMARY CARD
            TasbihStatsCard(
                totalCount = tasbihState.totalCount,
                lapCount = tasbihState.lapCount,
                preset = currentPreset
            )

            // 7. BANNER AD
            IronSourceBannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Hitungan Tasbih?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Hitungan saat ini (${tasbihState.currentCount}) akan dikembalikan ke angka 0. Total akumulasi dzikir tetap tersimpan aman."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCountReset()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset ke 0")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Sujud & Prayer Virtues Dialog
    if (showSujudVirtueDialog) {
        SujudVirtueDetailsDialog(onDismiss = { showSujudVirtueDialog = false })
    }

    // Dzikir Selection Bottom Sheet
    if (showDzikirSheet) {
        DzikirPresetBottomSheet(
            selectedIndex = tasbihState.selectedDzikirIndex,
            onSelectPreset = { index ->
                onSelectDzikirPreset(index)
                showDzikirSheet = false
            },
            onDismiss = { showDzikirSheet = false }
        )
    }

    // Bead Theme Picker Dialog
    if (showThemeDialog) {
        BeadThemePickerDialog(
            currentTheme = tasbihState.beadTheme,
            onSelectTheme = { theme ->
                onSelectBeadTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

/**
 * Top App Bar with back action, dzikir chooser, theme selector, and reset.
 */
@Composable
private fun TasbihTopAppBar(
    onNavigateBack: () -> Unit,
    onOpenDzikirList: () -> Unit,
    onOpenThemes: () -> Unit,
    onResetClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("tasbih_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Kompas",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = "Tasbih Digital",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Animasi Butiran Fisik • Dzikir & Doa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onOpenThemes,
                    modifier = Modifier.testTag("tasbih_theme_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Ganti Warna Butiran Tasbih",
                        tint = DeepGoldAccent
                    )
                }

                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier.testTag("tasbih_reset_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Hitungan",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Top Grand Mosque Sujud Hero Banner:
 * Displays a magnificent image of someone performing sujud in prayer inside a grand mosque
 * with majestic arches, marble floors, ornate domes, and hadith on the virtue of sujud.
 */
@Composable
private fun GrandMosqueSujudHeroBanner(
    onClickVirtue: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.2.dp, DeepGoldAccent.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClickVirtue)
            .testTag("grand_mosque_sujud_hero_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(215.dp)
        ) {
            // Photo of someone in sujud inside the grand mosque
            Image(
                painter = painterResource(id = R.drawable.img_sujud_grand_mosque_1791732157493),
                contentDescription = "Seseorang sedang sujud sholat di dalam masjid yang megah",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cinematic gradient overlay from top and bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            // Top badge: Sujud di Dalam Masjid yang Megah
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = DeepGoldAccent.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🕌",
                    fontSize = 13.sp
                )
                Text(
                    text = "Sujud Sholat di Dalam Masjid yang Megah",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Info button top right with tooltip
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.55f),
                border = BorderStroke(0.8.dp, DeepGoldAccent.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                IconButton(
                    onClick = onClickVirtue,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Informasi Keutamaan Sujud & Dzikir",
                        tint = DeepGoldAccent
                    )
                }
            }

            // Bottom text: Hadith on Sujud and spiritual reflection
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ فَأَكْثِرُوا الدُّعَاءَ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepGoldAccent,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "\"Sedekat-dekatnya seorang hamba kepada Rabb-nya adalah ketika ia sedang sujud, maka perbanyaklah doa.\" (HR. Muslim No. 482)",
                    fontSize = 11.5.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Sentuh untuk membaca kemuliaan sujud & rahasia tasbih ℹ️",
                    fontSize = 10.sp,
                    color = DeepGoldAccent.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Active Dzikir Card displaying the selected Arabic, Latin, and meaning.
 */
@Composable
private fun ActiveDzikirCard(
    preset: TasbihDzikirPreset,
    onOpenDzikirSelector: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onOpenDzikirSelector)
            .testTag("active_dzikir_info_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LAFADZ DZIKIR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Ganti Dzikir ▾",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Arabic text
            Text(
                text = preset.arabic,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Latin text
            Text(
                text = preset.latin,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            // Translation
            Text(
                text = "\"${preset.translation}\"",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Horizontal row of target chips: 33, 99, 100, 1000, Bebas.
 */
@Composable
private fun TargetSelectorPillsRow(
    currentTarget: Int,
    onSelectTarget: (Int) -> Unit
) {
    val targets = listOf(
        33 to "33x (Sunnah)",
        99 to "99x (Asmaul Husna)",
        100 to "100x (Harian)",
        1000 to "1000x (Istiqomah)",
        0 to "Bebas (∞)"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "TARGET HITUNGAN",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targets.forEach { (targetValue, label) ->
                val isSelected = currentTarget == targetValue
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectTarget(targetValue) },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("target_chip_$targetValue")
                )
            }
        }
    }
}

/**
 * Styles for interactive digital prayer beads animation.
 */
enum class TasbihBeadStyle(val label: String, val icon: String) {
    LOOP_33("Untaian Melingkar (33)", "📿"),
    VERTICAL_FALL("Untaian Geser Vertikal", "🧵")
}

/**
 * Animated Digital Tasbih with real moving beads.
 * When the user taps or drags, beads physically rotate or slide down smoothly along the strand thread.
 */
@Composable
private fun AnimatedTasbihInteractiveCard(
    tasbihState: TasbihState,
    preset: TasbihDzikirPreset,
    progress: Float,
    onCount: () -> Unit
) {
    // Current bead presentation style: Circular loop vs Vertical falling strand
    var beadStyle by remember { mutableStateOf(TasbihBeadStyle.LOOP_33) }

    // Animated float for smooth bead movement with spring physics
    val animatedCount by animateFloatAsState(
        targetValue = tasbihState.currentCount.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.58f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tasbih_bead_movement"
    )

    // Animated scale bounce for central counter number on each click
    val numberScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow),
        label = "tasbih_number_scale"
    )

    // Drag offset accumulator for finger-sliding physical beads
    var dragAccumulatorY by remember { mutableFloatStateOf(0f) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.5.dp, DeepGoldAccent.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("tasbih_interactive_canvas_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Bead theme indicator and lap counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .background(tasbihState.beadTheme.baseColor, CircleShape)
                            .border(1.2.dp, tasbihState.beadTheme.highlightColor, CircleShape)
                    )
                    Text(
                        text = tasbihState.beadTheme.displayName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Putaran ke-${tasbihState.lapCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            // Animation Style Switcher: Loop 33 vs Vertical Fall
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = beadStyle == TasbihBeadStyle.LOOP_33,
                    onClick = { beadStyle = TasbihBeadStyle.LOOP_33 },
                    label = {
                        Text(
                            text = "📿 Untaian Melingkar",
                            fontSize = 11.sp,
                            fontWeight = if (beadStyle == TasbihBeadStyle.LOOP_33) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("chip_bead_loop")
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = beadStyle == TasbihBeadStyle.VERTICAL_FALL,
                    onClick = { beadStyle = TasbihBeadStyle.VERTICAL_FALL },
                    label = {
                        Text(
                            text = "🧵 Butiran Geser Vertikal",
                            fontSize = 11.sp,
                            fontWeight = if (beadStyle == TasbihBeadStyle.VERTICAL_FALL) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("chip_bead_vertical")
                )
            }

            // Interactive Bead Strand Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onCount()
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                dragAccumulatorY += dragAmount
                                if (dragAccumulatorY > 32f || dragAccumulatorY < -32f) {
                                    onCount()
                                    dragAccumulatorY = 0f
                                }
                            },
                            onDragEnd = {
                                dragAccumulatorY = 0f
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val effectiveProgress = animatedCount + (dragAccumulatorY / 32f).coerceIn(-0.85f, 0.85f)

                if (beadStyle == TasbihBeadStyle.LOOP_33) {
                    // 1. Moving Circular 33 Prayer Beads Canvas
                    TasbihBeadsTrackCanvas(
                        animatedBeadProgress = effectiveProgress,
                        theme = tasbihState.beadTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 2. Physical Vertical Falling Strand Beads Canvas
                    TasbihVerticalStrandCanvas(
                        animatedBeadProgress = effectiveProgress,
                        theme = tasbihState.beadTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Central Digital Counter Circle Overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(DeepGoldAccent, DeepEmeraldPrimary)
                            ),
                            shape = CircleShape
                        )
                        .padding(12.dp)
                ) {
                    // Big count number
                    Text(
                        text = "${tasbihState.currentCount}",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    // Target indicator
                    Text(
                        text = if (tasbihState.targetCount > 0) {
                            "/ ${tasbihState.targetCount}"
                        } else {
                            "Bebas (∞)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Progress percentage
                    if (tasbihState.targetCount > 0) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlignedEmerald
                        )
                    }
                }

                // Gesture guidance caption at top/bottom of canvas
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "👇", fontSize = 10.sp)
                    Text(
                        text = "Geser ke bawah atau sentuh butiran",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Target Progress Bar
            if (tasbihState.targetCount > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kemajuan Dzikir",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${tasbihState.currentCount} dari ${tasbihState.targetCount}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DeepEmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Big Tap Button: "Geser / Tekan Butiran"
            Button(
                onClick = onCount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepEmeraldPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("tasbih_count_main_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = DeepGoldAccent
                    )
                    Text(
                        text = "Sentuh Layar / Geser Butiran (+1)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Canvas drawing the physical prayer beads track (Tasbih strand in 33-bead loop).
 * The beads are rendered in an oval track with rich 3D sphere gradient lighting.
 * When animatedBeadProgress changes, the entire strand rotates so beads physically slide!
 */
@Composable
private fun TasbihBeadsTrackCanvas(
    animatedBeadProgress: Float,
    theme: TasbihBeadTheme,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Elliptical track dimensions
        val rx = w * 0.40f
        val ry = h * 0.40f

        // 1. Draw the underlying thread/string
        drawOval(
            color = theme.stringColor.copy(alpha = 0.85f),
            topLeft = Offset(cx - rx, cy - ry),
            size = Size(rx * 2, ry * 2),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Total beads rendered along the continuous loop
        val totalBeads = 33
        val angleStep = (2.0 * PI) / totalBeads

        // Offset rotation based on progress (smooth sliding animation)
        val rotationOffset = (animatedBeadProgress * angleStep) % (2.0 * PI)

        // Draw 33 beads with 3D gradient and shadows
        for (i in 0 until totalBeads) {
            // Calculate angle for bead i
            val angle = (i * angleStep - rotationOffset - (PI / 2.0)).toFloat()
            val bx = (cx + rx * cos(angle.toDouble())).toFloat()
            val by = (cy + ry * sin(angle.toDouble())).toFloat()

            // Is this bead a marker/divider bead (Fashilah at 11, 22, 33)?
            val isMarkerBead = (i % 11 == 0)

            // Bead radius: normal beads vs marker beads
            val beadRadius = if (isMarkerBead) 15.dp.toPx() else 12.5.dp.toPx()

            // 1. Draw soft drop shadow beneath bead
            drawCircle(
                color = Color.Black.copy(alpha = 0.28f),
                radius = beadRadius,
                center = Offset(bx + 1.5.dp.toPx(), by + 2.5.dp.toPx())
            )

            // 2. 3D sphere radial gradient colors
            val gradientColors = if (isMarkerBead) {
                listOf(
                    GoldHighlight,
                    DeepGoldAccent,
                    Color(0xFF8D6E08)
                )
            } else {
                listOf(
                    theme.highlightColor,
                    theme.baseColor,
                    theme.shadowColor
                )
            }

            // Light source from top-left: shift radial gradient center slightly
            val lightOffset = Offset(
                bx - beadRadius * 0.35f,
                by - beadRadius * 0.35f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = lightOffset,
                    radius = beadRadius * 1.35f
                ),
                radius = beadRadius,
                center = Offset(bx, by)
            )

            // 3. Highlight specular shine spot
            drawCircle(
                color = Color.White.copy(alpha = if (isMarkerBead) 0.65f else 0.45f),
                radius = beadRadius * 0.28f,
                center = Offset(bx - beadRadius * 0.35f, by - beadRadius * 0.35f)
            )

            // 4. Subtle gold rim on marker beads
            if (isMarkerBead) {
                drawCircle(
                    color = GoldHighlight.copy(alpha = 0.8f),
                    radius = beadRadius,
                    center = Offset(bx, by),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }

        // Top decorative Tasbih Minaret Head (Imam / Yad) and tassel
        val topX = cx
        val topY = cy - ry

        // Golden Minaret bead (Imam)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GoldHighlight, DeepGoldAccent, Color(0xFF6B4C00)),
                center = Offset(topX - 3.dp.toPx(), topY - 3.dp.toPx()),
                radius = 16.dp.toPx()
            ),
            radius = 11.dp.toPx(),
            center = Offset(topX, topY)
        )

        // Silk tassel threads hanging downward from top apex
        val tasselLength = 22.dp.toPx()
        drawLine(
            color = DeepGoldAccent,
            start = Offset(topX - 3.dp.toPx(), topY + 11.dp.toPx()),
            end = Offset(topX - 6.dp.toPx(), topY + 11.dp.toPx() + tasselLength),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = GoldHighlight,
            start = Offset(topX, topY + 11.dp.toPx()),
            end = Offset(topX, topY + 11.dp.toPx() + tasselLength + 3.dp.toPx()),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = DeepGoldAccent,
            start = Offset(topX + 3.dp.toPx(), topY + 11.dp.toPx()),
            end = Offset(topX + 6.dp.toPx(), topY + 11.dp.toPx() + tasselLength),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Bottom focal pointer notch (where beads pass into counted pool)
        val bottomX = cx
        val bottomY = cy + ry
        drawCircle(
            color = AlignedEmerald.copy(alpha = 0.25f),
            radius = 20.dp.toPx(),
            center = Offset(bottomX, bottomY)
        )
        drawCircle(
            color = GoldHighlight,
            radius = 3.5.dp.toPx(),
            center = Offset(bottomX, bottomY + 18.dp.toPx())
        )
    }
}

/**
 * Canvas drawing the physical vertical falling prayer beads strand.
 * The beads visibly slide down one by one through a central golden Mihrab gate!
 */
@Composable
private fun TasbihVerticalStrandCanvas(
    animatedBeadProgress: Float,
    theme: TasbihBeadTheme,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Central braided silk thread
        drawLine(
            color = theme.stringColor.copy(alpha = 0.9f),
            start = Offset(cx, 0f),
            end = Offset(cx, h),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Subtle side silk twist
        drawLine(
            color = theme.highlightColor.copy(alpha = 0.35f),
            start = Offset(cx + 1.2.dp.toPx(), 0f),
            end = Offset(cx + 1.2.dp.toPx(), h),
            strokeWidth = 1.dp.toPx()
        )

        val beadSpacing = 44.dp.toPx()
        val yOffset = (animatedBeadProgress * beadSpacing) % beadSpacing

        // Draw counting gate reticle in the center
        drawCircle(
            color = DeepGoldAccent.copy(alpha = 0.18f),
            radius = 42.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GoldHighlight.copy(alpha = 0.45f),
                    DeepGoldAccent.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = 50.dp.toPx()
            ),
            radius = 50.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = DeepGoldAccent.copy(alpha = 0.7f),
            radius = 30.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Number of visible beads above and below center
        val visibleRange = (h / beadSpacing).toInt() / 2 + 3
        for (i in -visibleRange..visibleRange) {
            val by = cy + (i * beadSpacing) + yOffset
            if (by < -beadSpacing || by > h + beadSpacing) continue

            // Distance to center focal reticle determines scale and brightness
            val distToCenter = kotlin.math.abs(by - cy)
            val focusFactor = (1f - (distToCenter / (beadSpacing * 2.2f))).coerceIn(0.65f, 1.35f)

            // Base bead radius
            val isMarkerBead = ((i + animatedBeadProgress.toInt()) % 11 == 0)
            val baseRadius = if (isMarkerBead) 16.dp.toPx() else 13.5.dp.toPx()
            val beadRadius = baseRadius * focusFactor

            // Drop shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.32f),
                radius = beadRadius,
                center = Offset(cx + 2.dp.toPx(), by + 3.dp.toPx())
            )

            // 3D sphere gradient
            val gradientColors = if (isMarkerBead) {
                listOf(GoldHighlight, DeepGoldAccent, Color(0xFF6B4307))
            } else {
                listOf(theme.highlightColor, theme.baseColor, theme.shadowColor)
            }

            val lightOffset = Offset(
                cx - beadRadius * 0.35f,
                by - beadRadius * 0.35f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = lightOffset,
                    radius = beadRadius * 1.35f
                ),
                radius = beadRadius,
                center = Offset(cx, by)
            )

            // Specular highlight spot
            drawCircle(
                color = Color.White.copy(alpha = if (isMarkerBead) 0.7f else 0.5f),
                radius = beadRadius * 0.28f,
                center = Offset(cx - beadRadius * 0.35f, by - beadRadius * 0.35f)
            )

            // Active bead halo if near center
            if (distToCenter < beadSpacing * 0.45f) {
                drawCircle(
                    color = GoldHighlight.copy(alpha = 0.65f),
                    radius = beadRadius + 3.dp.toPx(),
                    center = Offset(cx, by),
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }
        }
    }
}

/**
 * Action toolbar: Sound toggle, Vibration toggle, Theme chooser, Reset.
 */
@Composable
private fun TasbihActionButtonsBar(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    beadTheme: TasbihBeadTheme,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onOpenThemes: () -> Unit,
    onResetClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sound button
            TasbihToolButton(
                icon = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                label = if (soundEnabled) "Suara Aktif" else "Bisu",
                isActive = soundEnabled,
                onClick = onToggleSound,
                testTag = "tasbih_toggle_sound"
            )

            // Vibration button
            TasbihToolButton(
                icon = Icons.Default.Vibration,
                label = if (vibrationEnabled) "Getar Aktif" else "Getar Mati",
                isActive = vibrationEnabled,
                onClick = onToggleVibration,
                testTag = "tasbih_toggle_vibration"
            )

            // Theme button
            TasbihToolButton(
                icon = Icons.Default.Palette,
                label = "Warna Biji",
                isActive = true,
                onClick = onOpenThemes,
                testTag = "tasbih_open_themes"
            )

            // Reset button
            TasbihToolButton(
                icon = Icons.Default.Refresh,
                label = "Reset",
                isActive = false,
                onClick = onResetClick,
                testTag = "tasbih_reset_action"
            )
        }
    }
}

@Composable
private fun TasbihToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (isActive) {
                        DeepEmeraldPrimary.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) DeepEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) DeepEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Total Statistics card showing aggregate count across sessions.
 */
@Composable
private fun TasbihStatsCard(
    totalCount: Long,
    lapCount: Int,
    preset: TasbihDzikirPreset
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "STATISTIK & FADHILAH DZIKIR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Akumulasi Dzikir",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalCount x",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Putaran",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$lapCount Putaran",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepEmeraldPrimary
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Fadhilah:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepGoldAccent
            )

            Text(
                text = preset.fadhilah,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Bottom sheet displaying all available dzikir presets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DzikirPresetBottomSheet(
    selectedIndex: Int,
    onSelectPreset: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pilih Lafadz Dzikir & Wirid",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Pilih dzikir ma'tsur sesuai amalan harian Anda:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.8.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TasbihPresets.presets.forEachIndexed { index, preset ->
                    val isSelected = selectedIndex == index
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            }
                        ),
                        border = if (isSelected) {
                            BorderStroke(1.5.dp, DeepEmeraldPrimary)
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPreset(index) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = preset.arabic,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = preset.latin,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = preset.translation,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Terpilih",
                                    tint = DeepEmeraldPrimary,
                                    modifier = Modifier.padding(start = 8.dp)
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
 * Dialog to select bead color / material theme.
 */
@Composable
private fun BeadThemePickerDialog(
    currentTheme: TasbihBeadTheme,
    onSelectTheme: (TasbihBeadTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pilih Warna Butiran Tasbih",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TasbihBeadTheme.entries.forEach { theme ->
                    val isSelected = currentTheme == theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTheme(theme) }
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(theme.baseColor, CircleShape)
                                .border(2.dp, theme.highlightColor, CircleShape)
                        )

                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DeepEmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

/**
 * Details Dialog showing explanations of Sujud in Islam and how it elevates Dzikir.
 */
@Composable
private fun SujudVirtueDetailsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🕌", fontSize = 20.sp)
                Text(
                    text = "Keutamaan Sujud & Dzikir",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Posisi Paling Dekat Dengan Sang Khalik",
                    fontWeight = FontWeight.Bold,
                    color = DeepEmeraldPrimary
                )
                Text(
                    text = "Rasulullah ﷺ bersabda:\n\"Sedekat-dekatnya seorang hamba kepada Rabb-nya adalah ketika ia sedang sujud, maka perbanyaklah doa padanya.\" (HR. Muslim No. 482)",
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                HorizontalDivider(thickness = 0.8.dp)

                Text(
                    text = "Mengapa Sujud Begitu Mulia?",
                    fontWeight = FontWeight.Bold,
                    color = DeepGoldAccent
                )
                Text(
                    text = "1. Puncak Ketundukan: Anggota tubuh paling terhormat (wajah & dahi) direbahkan sejajar dengan tanah di hadapan keagungan Allah SWT.\n\n" +
                            "2. Menghapus Dosa & Mengangkat Derajat: Setiap kali seorang muslim bersujud satu kali karena Allah, Allah gugurkan satu kesalahannya dan tinggikan satu derajatnya di surga.\n\n" +
                            "3. Doa Paling Mustajab: Sujud adalah saat terkabulnya hajat, doa istighfar, dan tasbih Subhana Rabbiyal A'la wa Bihamdih.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary)
            ) {
                Text("Alhamdulillah, Paham")
            }
        }
    )
}

/**
 * Helper to trigger snappy system click sound & subtle tactile vibration.
 */
private fun triggerHapticAndSound(
    context: Context,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean
) {
    if (vibrationEnabled) {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(18)
                }
            }
        } catch (_: Exception) {}
    }

    if (soundEnabled) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.75f)
        } catch (_: Exception) {}
    }
}
