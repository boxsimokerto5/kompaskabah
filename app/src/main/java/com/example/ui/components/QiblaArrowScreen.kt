package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.QiblaCalculator
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.roundToInt

private val SkyAzurePrimary = Color(0xFF1D8FE1)
private val SkyAzureDark = Color(0xFF0E6EB8)
private val SkyAzureSoftPill = Color(0xFFEEF6FC)
private val SkyAzureBorder = Color(0xFFD5E9FA)
private val ArrowAmberTop = Color(0xFFFF9800)
private val ArrowGoldBottom = Color(0xFFE6D200)

/**
 * Dedicated "Anak Panah" (kompas kiblat) screen featuring a full-bleed left-to-right
 * Grand Golden Mosque at Sunset gradient hero banner at the top, followed by the
 * rotating 3D split-faceted golden Kaaba arrow, DMS coordinates, and automatic GPS bar.
 */
@Composable
fun QiblaArrowScreen(
    uiState: QiblaUiState,
    onBackToCompass: () -> Unit,
    onOpenPrayerTimes: () -> Unit,
    onRefreshGps: () -> Unit,
    onOpenCalibration: () -> Unit,
    onToggleHaptic: () -> Unit,
    onManualRotate: (Float) -> Unit,
    onSnapToQibla: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    val qiblaInt = uiState.qiblaBearingTrueNorthDeg.roundToInt()
    val cardinalShort = QiblaCalculator.getShortCardinalCode(uiState.qiblaBearingTrueNorthDeg)
    val latDms = QiblaCalculator.formatToDms(uiState.latitude, isLatitude = true)
    val lonDms = QiblaCalculator.formatToDms(uiState.longitude, isLatitude = false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Full-Bleed Left-to-Right Grand Golden Mosque at Sunset Hero with Seamless Bottom Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
                .testTag("arrow_golden_mosque_hero_banner")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_golden_mosque_sunset_1791637680885),
                contentDescription = "Ilustrasi Masjid Megah Bernuansa Emas Saat Senja",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            // Top dusk scrim for crisp readability of the "<- kompas kiblat" bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF141824).copy(alpha = 0.58f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Smooth bottom gradient fade blending the golden mosque courtyard into the bright page background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
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

            // Top Bar & Calibration Button overlaid inside the Golden Mosque Sunset Hero
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackToCompass,
                            modifier = Modifier.testTag("arrow_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali ke Kompas",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "kompas kiblat",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onOpenPrayerTimes,
                            modifier = Modifier.testTag("arrow_top_prayer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Jadwal Waktu Sholat",
                                tint = Color(0xFFFFE082)
                            )
                        }
                        IconButton(
                            onClick = onOpenCalibration,
                            modifier = Modifier.testTag("arrow_top_info_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Informasi & Kalibrasi Sensor",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Bottom of Hero: Subtle Reference Triangle in Center & Figure-8 (∞) Calibration Button on Right
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2f, size.height * 0.78f)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = if (uiState.isAlignedWithKaaba) {
                                AlignedEmerald.copy(alpha = 0.65f)
                            } else {
                                SkyAzurePrimary.copy(alpha = 0.28f)
                            }
                        )
                    }

                    Surface(
                        onClick = onOpenCalibration,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterEnd)
                            .border(1.dp, SkyAzureBorder, CircleShape)
                            .testTag("arrow_screen_calibration_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AllInclusive,
                                contentDescription = "Kalibrasi Kompas Angka 8",
                                tint = SkyAzurePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Centerpiece Kaaba Medallion + Rotating 3D Split-Faceted Qibla Arrow
        InteractiveKaabaArrowPointer(
            deviceHeadingDeg = uiState.effectiveDeviceHeadingDeg,
            qiblaBearingDeg = uiState.qiblaBearingTrueNorthDeg,
            isAligned = uiState.isAlignedWithKaaba,
            onManualRotate = onManualRotate,
            onDoubleTapOrClick = onSnapToQibla,
            modifier = Modifier
                .widthIn(max = 295.dp)
                .padding(horizontal = 20.dp)
        )

        // 3. Bottom Readout & Automatic GPS Location Display Section
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isAlignedWithKaaba) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AlignedEmerald.copy(alpha = 0.14f),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AlignedEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tepat Menghadap Kiblat",
                            style = MaterialTheme.typography.labelMedium,
                            color = AlignedEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Arah Kiblat dari Utara",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = String.format(Locale.US, "%d° %s", qiblaInt, cardinalShort),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 44.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (uiState.isAlignedWithKaaba) AlignedEmerald else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("arrow_qibla_degree_text")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Two Rounded Soft-Blue GPS Coordinate Pills: Garis Lintang & Garis Bujur
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CoordinateDmsPill(
                    title = "Garis Lintang :",
                    dmsValue = latDms,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pill_latitude_dms")
                )
                CoordinateDmsPill(
                    title = "Garis bujur :",
                    dmsValue = lonDms,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pill_longitude_dms")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Automatic GPS Location Pill + Circular Haptic Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onRefreshGps,
                    shape = RoundedCornerShape(50),
                    color = SkyAzurePrimary,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("arrow_location_pill_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi GPS Otomatis",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .width(1.2.dp)
                                .height(22.dp)
                                .background(Color.White.copy(alpha = 0.55f))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = uiState.locationLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Surface(
                    onClick = onToggleHaptic,
                    shape = CircleShape,
                    color = SkyAzureSoftPill,
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.dp, SkyAzureBorder, CircleShape)
                        .testTag("arrow_haptic_circle_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Getar saat menghadap Kiblat",
                            tint = if (uiState.hapticFeedbackEnabled) {
                                SkyAzurePrimary
                            } else {
                                SkyAzurePrimary.copy(alpha = 0.35f)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinateDmsPill(
    title: String,
    dmsValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = SkyAzureSoftPill,
        modifier = modifier.border(
            width = 1.dp,
            color = SkyAzureBorder.copy(alpha = 0.7f),
            shape = RoundedCornerShape(50)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = SkyAzurePrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dmsValue,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InteractiveKaabaArrowPointer(
    deviceHeadingDeg: Float,
    qiblaBearingDeg: Float,
    isAligned: Boolean,
    onManualRotate: (Float) -> Unit,
    onDoubleTapOrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var continuousHeading by remember { mutableFloatStateOf(deviceHeadingDeg) }
    val headingDiff = QiblaCalculator.shortestAngleDelta(
        ((continuousHeading % 360f) + 360f) % 360f,
        deviceHeadingDeg
    )
    continuousHeading += headingDiff

    val animatedHeading by animateFloatAsState(
        targetValue = continuousHeading,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "arrow_pointer_heading"
    )

    val ringBorderColor by animateColorAsState(
        targetValue = if (isAligned) AlignedEmerald else SkyAzureDark,
        animationSpec = tween(300),
        label = "medallion_ring_color"
    )

    val ringFillColor by animateColorAsState(
        targetValue = if (isAligned) AlignedEmeraldGlow else SkyAzurePrimary,
        animationSpec = tween(300),
        label = "medallion_fill_color"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .testTag("qibla_arrow_canvas")
            .semantics {
                contentDescription = "Visualisasi Penunjuk Arah Kiblat Presisi Real-time berbasis Sensor Hardware Magnetometer."
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = min(size.width, size.height) / 2f
            val medallionRadius = maxR * 0.34f

            val pointerRotationDeg = qiblaBearingDeg - animatedHeading

            rotate(degrees = pointerRotationDeg, pivot = center) {
                drawSplitFacetedGoldenArrow(
                    center = center,
                    medallionRadius = medallionRadius,
                    maxRadius = maxR * 0.94f,
                    isAligned = isAligned
                )

                drawCircle(
                    color = ringBorderColor,
                    radius = medallionRadius,
                    center = center
                )
                drawCircle(
                    color = ringFillColor,
                    radius = medallionRadius * 0.88f,
                    center = center
                )

                drawStylizedKaabaInMedallion(
                    center = center,
                    cubeRadius = medallionRadius * 0.56f
                )
            }
        }
    }
}

private fun DrawScope.drawSplitFacetedGoldenArrow(
    center: Offset,
    medallionRadius: Float,
    maxRadius: Float,
    isAligned: Boolean
) {
    val tipY = center.y - maxRadius
    val baseBottomY = center.y - medallionRadius * 1.12f
    val wingTipY = center.y - medallionRadius * 1.38f
    val neckY = center.y - medallionRadius * 1.44f

    val halfNeckWidth = medallionRadius * 0.54f
    val halfWingWidth = medallionRadius * 1.05f

    val leftColor = if (isAligned) AlignedEmerald else ArrowAmberTop
    val rightColor = if (isAligned) AlignedEmeraldGlow else ArrowGoldBottom

    val leftHalfPath = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x - halfWingWidth, wingTipY)
        lineTo(center.x - halfNeckWidth, neckY)
        lineTo(center.x - halfNeckWidth * 0.90f, baseBottomY)
        lineTo(center.x, baseBottomY - 4.dp.toPx())
        close()
    }

    val rightHalfPath = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x + halfWingWidth, wingTipY)
        lineTo(center.x + halfNeckWidth, neckY)
        lineTo(center.x + halfNeckWidth * 0.90f, baseBottomY)
        lineTo(center.x, baseBottomY - 4.dp.toPx())
        close()
    }

    drawPath(path = leftHalfPath, color = leftColor)
    drawPath(path = rightHalfPath, color = rightColor)

    val fullArrowContour = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x - halfWingWidth, wingTipY)
        lineTo(center.x - halfNeckWidth, neckY)
        lineTo(center.x - halfNeckWidth * 0.90f, baseBottomY)
        lineTo(center.x + halfNeckWidth * 0.90f, baseBottomY)
        lineTo(center.x + halfNeckWidth, neckY)
        lineTo(center.x + halfWingWidth, wingTipY)
        close()
    }

    drawPath(
        path = fullArrowContour,
        color = leftColor.copy(alpha = 0.35f),
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawStylizedKaabaInMedallion(
    center: Offset,
    cubeRadius: Float
) {
    val w = cubeRadius * 1.32f
    val h = cubeRadius * 1.24f
    val left = center.x - w / 2f
    val top = center.y - h / 2f

    drawRoundRect(
        color = Color(0xFFD7CCC8),
        topLeft = Offset(left - 2.dp.toPx(), top + h * 0.72f),
        size = Size(w + 4.dp.toPx(), h * 0.28f),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    drawRoundRect(
        color = Color(0xFF14181B),
        topLeft = Offset(left, top),
        size = Size(w, h * 0.80f),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
    )

    drawRoundRect(
        color = Color(0xFFF5F7FA),
        topLeft = Offset(left + w * 0.06f, top + h * 0.56f),
        size = Size(w * 0.88f, h * 0.18f),
        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
    )

    val bandY = top + h * 0.24f
    drawLine(
        color = Color(0xFFFFD54F),
        start = Offset(left + 3.dp.toPx(), bandY),
        end = Offset(left + w - 3.dp.toPx(), bandY),
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round
    )

    drawRoundRect(
        color = Color(0xFFFFCA28),
        topLeft = Offset(left + w * 0.60f, top + h * 0.34f),
        size = Size(w * 0.18f, h * 0.24f),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
}
