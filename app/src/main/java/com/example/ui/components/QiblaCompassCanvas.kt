package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.QiblaCalculator
import com.example.ui.theme.AlabasterBg
import com.example.ui.theme.AlabasterCard
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MeccaGold
import com.example.ui.theme.MeccaGoldBright
import com.example.ui.theme.MeccaGoldMuted
import com.example.ui.theme.SageTextSecondary
import com.example.ui.theme.SanctuaryCardSurface
import com.example.ui.theme.SanctuaryDeepBg
import com.example.ui.theme.SanctuaryEmeraldSurface
import com.example.ui.theme.SlateSageSecondary
import com.example.ui.theme.WarningAmber
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * High-precision interactive Qibla Astrolabe & Kaaba Pointer Dial.
 * Supports both Bright Alabaster Pearl theme (default) and Sanctuary Dark theme.
 */
@Composable
fun QiblaCompassCanvas(
    deviceHeadingDeg: Float,
    qiblaBearingDeg: Float,
    pitchDeg: Float,
    rollDeg: Float,
    isAligned: Boolean,
    isNearAlignment: Boolean,
    isDarkTheme: Boolean = false,
    onManualRotate: (Float) -> Unit,
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
        label = "compass_heading_spring"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "qibla_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryAccentColor by animateColorAsState(
        targetValue = if (isAligned) AlignedEmerald else if (isDarkTheme) MeccaGold else DeepGoldAccent,
        animationSpec = tween(350),
        label = "accent_color"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = when {
            isAligned -> 0.38f
            isNearAlignment -> 0.22f
            else -> 0.10f
        },
        animationSpec = tween(400),
        label = "glow_alpha"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(6.dp)
            .testTag("qibla_compass_dial")
            .semantics {
                contentDescription = "Visualisasi Kompas Kiblat Presisi Real-time berbasis Sensor Hardware Magnetometer."
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = min(size.width, size.height) / 2f

            // 1. Outer Ambient Halo Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryAccentColor.copy(alpha = glowAlpha),
                        primaryAccentColor.copy(alpha = glowAlpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * pulseScale
                ),
                radius = maxRadius,
                center = center
            )

            val dialRadius = maxRadius * 0.88f

            // 2. Luminous Pearl / Alabaster (or Dark Sanctuary) Compass Dial Base
            val dialGradientColors = if (isDarkTheme) {
                listOf(SanctuaryCardSurface, SanctuaryEmeraldSurface, SanctuaryDeepBg)
            } else {
                listOf(AlabasterSurface, AlabasterBg, AlabasterCard)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = dialGradientColors,
                    center = center,
                    radius = dialRadius
                ),
                radius = dialRadius,
                center = center
            )

            // 3. Outer Metallic Gold & Emerald Bezel Rings
            drawCircle(
                color = primaryAccentColor.copy(alpha = 0.75f),
                radius = dialRadius,
                center = center,
                style = Stroke(width = 3.2.dp.toPx())
            )
            drawCircle(
                color = if (isDarkTheme) MeccaGoldMuted.copy(alpha = 0.35f) else DeepEmeraldPrimary.copy(alpha = 0.18f),
                radius = dialRadius * 0.94f,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // 4. Fixed Top Lubber Line (Phone Forward Direction Marker at 12 o'clock)
            drawLubberLineAtTop(
                center = center,
                dialRadius = dialRadius,
                isAligned = isAligned,
                accentColor = primaryAccentColor
            )

            // 5. Shortest Turning Arc on Outer Bezel
            val qiblaRelativeAngle = QiblaCalculator.shortestAngleDelta(animatedHeading, qiblaBearingDeg)
            if (kotlin.math.abs(qiblaRelativeAngle) > 1.5f) {
                val arcRadius = dialRadius * 0.97f
                drawArc(
                    color = primaryAccentColor.copy(alpha = 0.80f),
                    startAngle = -90f,
                    sweepAngle = qiblaRelativeAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2f, arcRadius * 2f),
                    style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 6. Rotating Compass Rose (-animatedHeading)
            rotate(degrees = -animatedHeading, pivot = center) {
                // Subtle Islamic 8-pointed Star (Rub el Hizb) geometric watermark
                drawIslamicOctagramWatermark(
                    center = center,
                    radius = dialRadius * 0.62f,
                    color = if (isDarkTheme) {
                        MeccaGold.copy(alpha = 0.08f)
                    } else {
                        DeepGoldAccent.copy(alpha = 0.14f)
                    }
                )

                // Degree Ticks
                for (deg in 0 until 360 step 2) {
                    val angleRad = Math.toRadians((deg - 90).toDouble())
                    val isCardinal = deg % 90 == 0
                    val isMajor30 = deg % 30 == 0
                    val isMedium10 = deg % 10 == 0

                    val outerTickR = dialRadius * 0.91f
                    val innerTickR = when {
                        isCardinal -> dialRadius * 0.79f
                        isMajor30 -> dialRadius * 0.82f
                        isMedium10 -> dialRadius * 0.85f
                        else -> dialRadius * 0.88f
                    }

                    val tickColor = if (isDarkTheme) {
                        when {
                            deg == 0 -> Color(0xFFEF4444)
                            isCardinal -> MeccaGoldBright
                            isMajor30 -> IvoryTextPrimary.copy(alpha = 0.85f)
                            isMedium10 -> SageTextSecondary.copy(alpha = 0.55f)
                            else -> SageTextSecondary.copy(alpha = 0.25f)
                        }
                    } else {
                        when {
                            deg == 0 -> Color(0xFFDC2626)
                            isCardinal -> DeepEmeraldPrimary
                            isMajor30 -> CharcoalText.copy(alpha = 0.80f)
                            isMedium10 -> SlateSageSecondary.copy(alpha = 0.55f)
                            else -> SlateSageSecondary.copy(alpha = 0.26f)
                        }
                    }

                    val strokeW = when {
                        isCardinal -> 2.8.dp.toPx()
                        isMajor30 -> 1.8.dp.toPx()
                        isMedium10 -> 1.2.dp.toPx()
                        else -> 0.8.dp.toPx()
                    }

                    val startPt = Offset(
                        x = center.x + (outerTickR * cos(angleRad)).toFloat(),
                        y = center.y + (outerTickR * sin(angleRad)).toFloat()
                    )
                    val endPt = Offset(
                        x = center.x + (innerTickR * cos(angleRad)).toFloat(),
                        y = center.y + (innerTickR * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = tickColor,
                        start = startPt,
                        end = endPt,
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )

                    // Draw degree numbers at 30° intervals
                    if (isMajor30 && !isCardinal) {
                        val numberRadius = dialRadius * 0.72f
                        val numPos = Offset(
                            x = center.x + (numberRadius * cos(angleRad)).toFloat(),
                            y = center.y + (numberRadius * sin(angleRad)).toFloat()
                        )
                        val textLayout = textMeasurer.measure(
                            text = deg.toString(),
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) {
                                    SageTextSecondary.copy(alpha = 0.75f)
                                } else {
                                    SlateSageSecondary.copy(alpha = 0.85f)
                                }
                            )
                        )
                        rotate(degrees = deg.toFloat(), pivot = numPos) {
                            drawText(
                                textLayoutResult = textLayout,
                                topLeft = Offset(
                                    numPos.x - textLayout.size.width / 2f,
                                    numPos.y - textLayout.size.height / 2f
                                )
                            )
                        }
                    }
                }

                // Draw Cardinal Labels (U, T, S, B)
                val cardinalMainColor = if (isDarkTheme) IvoryTextPrimary else CharcoalText
                val northColor = if (isDarkTheme) MeccaGoldBright else DeepEmeraldPrimary
                val cardinals = listOf(
                    Triple(0f, "U", northColor),
                    Triple(90f, "T", cardinalMainColor),
                    Triple(180f, "S", cardinalMainColor),
                    Triple(270f, "B", cardinalMainColor)
                )
                for ((deg, label, color) in cardinals) {
                    val angleRad = Math.toRadians((deg - 90f).toDouble())
                    val labelRadius = dialRadius * 0.69f
                    val pos = Offset(
                        x = center.x + (labelRadius * cos(angleRad)).toFloat(),
                        y = center.y + (labelRadius * sin(angleRad)).toFloat()
                    )
                    val measured = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            fontFamily = CinzelFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    )
                    rotate(degrees = deg, pivot = pos) {
                        drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(
                                pos.x - measured.size.width / 2f,
                                pos.y - measured.size.height / 2f
                            )
                        )
                    }
                }

                // 7. Kaaba Emblem Marker anchored on the Compass Rose at exact Qibla Bearing
                rotate(degrees = qiblaBearingDeg, pivot = center) {
                    val kaabaMarkerCenter = Offset(center.x, center.y - dialRadius * 0.83f)
                    drawKaabaEmblemOnPerimeter(
                        markerCenter = kaabaMarkerCenter,
                        sizePx = 25.dp.toPx(),
                        isAligned = isAligned
                    )
                }
            }

            // 8. Golden Qibla Pointer Arrow (always points directly toward the Holy Kaaba)
            val qiblaPointerRotation = qiblaBearingDeg - animatedHeading
            rotate(degrees = qiblaPointerRotation, pivot = center) {
                drawSculptedQiblaArrow(
                    center = center,
                    arrowLength = dialRadius * 0.68f,
                    tailLength = dialRadius * 0.34f,
                    isAligned = isAligned,
                    primaryColor = primaryAccentColor
                )
            }

            // 9. Central 3D Spirit Level (Bubble Clinometer) & Pivot Hub
            drawCenterSpiritLevelHub(
                center = center,
                hubRadius = dialRadius * 0.18f,
                pitchDeg = pitchDeg,
                rollDeg = rollDeg,
                isAligned = isAligned,
                isDarkTheme = isDarkTheme,
                accentColor = primaryAccentColor
            )
        }
    }
}

private fun DrawScope.drawLubberLineAtTop(
    center: Offset,
    dialRadius: Float,
    isAligned: Boolean,
    accentColor: Color
) {
    val topY = center.y - dialRadius - 10.dp.toPx()
    val triangleHeight = 14.dp.toPx()
    val triangleHalfWidth = 10.dp.toPx()

    val path = Path().apply {
        moveTo(center.x, topY + triangleHeight)
        lineTo(center.x - triangleHalfWidth, topY)
        lineTo(center.x + triangleHalfWidth, topY)
        close()
    }

    drawPath(
        path = path,
        color = if (isAligned) AlignedEmerald else DeepGoldAccent
    )

    drawLine(
        color = accentColor,
        start = Offset(center.x, center.y - dialRadius * 0.98f),
        end = Offset(center.x, center.y - dialRadius * 0.89f),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawIslamicOctagramWatermark(
    center: Offset,
    radius: Float,
    color: Color
) {
    val stroke = Stroke(width = 1.2.dp.toPx())
    val squareSize = radius * 1.35f
    val topLeft = Offset(center.x - squareSize / 2f, center.y - squareSize / 2f)

    drawRect(
        color = color,
        topLeft = topLeft,
        size = Size(squareSize, squareSize),
        style = stroke
    )
    rotate(degrees = 45f, pivot = center) {
        drawRect(
            color = color,
            topLeft = topLeft,
            size = Size(squareSize, squareSize),
            style = stroke
        )
    }
    drawCircle(
        color = color,
        radius = radius * 0.72f,
        center = center,
        style = stroke
    )
}

private fun DrawScope.drawKaabaEmblemOnPerimeter(
    markerCenter: Offset,
    sizePx: Float,
    isAligned: Boolean
) {
    val haloColor = if (isAligned) AlignedEmerald else DeepGoldAccent
    drawCircle(
        color = haloColor.copy(alpha = 0.25f),
        radius = sizePx * 0.85f,
        center = markerCenter
    )
    drawCircle(
        color = Color(0xFF0C1F18),
        radius = sizePx * 0.65f,
        center = markerCenter
    )
    drawCircle(
        color = haloColor,
        radius = sizePx * 0.65f,
        center = markerCenter,
        style = Stroke(width = 1.8.dp.toPx())
    )

    val cubeW = sizePx * 0.66f
    val cubeH = sizePx * 0.62f
    val cubeTopLeft = Offset(markerCenter.x - cubeW / 2f, markerCenter.y - cubeH / 2f)

    drawRoundRect(
        color = Color(0xFF0A1410),
        topLeft = cubeTopLeft,
        size = Size(cubeW, cubeH),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
    drawRoundRect(
        color = MeccaGoldBright,
        topLeft = cubeTopLeft,
        size = Size(cubeW, cubeH),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        style = Stroke(width = 1.2.dp.toPx())
    )

    val bandY = cubeTopLeft.y + cubeH * 0.28f
    drawLine(
        color = MeccaGoldBright,
        start = Offset(cubeTopLeft.x + 1.5.dp.toPx(), bandY),
        end = Offset(cubeTopLeft.x + cubeW - 1.5.dp.toPx(), bandY),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Butt
    )
}

private fun DrawScope.drawSculptedQiblaArrow(
    center: Offset,
    arrowLength: Float,
    tailLength: Float,
    isAligned: Boolean,
    primaryColor: Color
) {
    val tipY = center.y - arrowLength
    val tailY = center.y + tailLength
    val halfWingWidth = 17.dp.toPx()
    val waistHalfWidth = 5.5.dp.toPx()

    if (isAligned) {
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    AlignedEmerald.copy(alpha = 0.85f),
                    AlignedEmeraldGlow.copy(alpha = 0.1f)
                ),
                startY = tipY - 22.dp.toPx(),
                endY = center.y
            ),
            start = Offset(center.x, tipY - 18.dp.toPx()),
            end = Offset(center.x, center.y),
            strokeWidth = 8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    val leftNeedlePath = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x - halfWingWidth, center.y - arrowLength * 0.26f)
        lineTo(center.x - waistHalfWidth, center.y - arrowLength * 0.32f)
        lineTo(center.x - waistHalfWidth * 0.8f, tailY)
        lineTo(center.x, tailY - 10.dp.toPx())
        close()
    }

    val rightNeedlePath = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x + halfWingWidth, center.y - arrowLength * 0.26f)
        lineTo(center.x + waistHalfWidth, center.y - arrowLength * 0.32f)
        lineTo(center.x + waistHalfWidth * 0.8f, tailY)
        lineTo(center.x, tailY - 10.dp.toPx())
        close()
    }

    val leftFacetColor = if (isAligned) AlignedEmeraldGlow else MeccaGoldBright
    val rightFacetColor = if (isAligned) AlignedEmerald else DeepGoldAccent

    drawPath(path = leftNeedlePath, color = leftFacetColor)
    drawPath(path = rightNeedlePath, color = rightFacetColor)

    val fullOutlinePath = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x - halfWingWidth, center.y - arrowLength * 0.26f)
        lineTo(center.x - waistHalfWidth, center.y - arrowLength * 0.32f)
        lineTo(center.x - waistHalfWidth * 0.8f, tailY)
        lineTo(center.x, tailY - 10.dp.toPx())
        lineTo(center.x + waistHalfWidth * 0.8f, tailY)
        lineTo(center.x + waistHalfWidth, center.y - arrowLength * 0.32f)
        lineTo(center.x + halfWingWidth, center.y - arrowLength * 0.26f)
        close()
    }
    drawPath(
        path = fullOutlinePath,
        color = primaryColor,
        style = Stroke(width = 1.4.dp.toPx(), join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawCenterSpiritLevelHub(
    center: Offset,
    hubRadius: Float,
    pitchDeg: Float,
    rollDeg: Float,
    isAligned: Boolean,
    isDarkTheme: Boolean,
    accentColor: Color
) {
    drawCircle(
        color = if (isDarkTheme) SanctuaryDeepBg else AlabasterSurface,
        radius = hubRadius,
        center = center
    )
    drawCircle(
        color = accentColor.copy(alpha = 0.8f),
        radius = hubRadius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    val crosshairColor = if (isDarkTheme) {
        SageTextSecondary.copy(alpha = 0.25f)
    } else {
        SlateSageSecondary.copy(alpha = 0.25f)
    }

    drawLine(
        color = crosshairColor,
        start = Offset(center.x - hubRadius * 0.75f, center.y),
        end = Offset(center.x + hubRadius * 0.75f, center.y),
        strokeWidth = 1.dp.toPx()
    )
    drawLine(
        color = crosshairColor,
        start = Offset(center.x, center.y - hubRadius * 0.75f),
        end = Offset(center.x, center.y + hubRadius * 0.75f),
        strokeWidth = 1.dp.toPx()
    )
    drawCircle(
        color = crosshairColor,
        radius = hubRadius * 0.38f,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    val maxOffset = hubRadius * 0.62f
    val normX = (rollDeg / 45f).coerceIn(-1f, 1f)
    val normY = (pitchDeg / 45f).coerceIn(-1f, 1f)
    val bubbleCenter = Offset(
        x = center.x + normX * maxOffset,
        y = center.y + normY * maxOffset
    )

    val isFlat = kotlin.math.abs(pitchDeg) < 15f && kotlin.math.abs(rollDeg) < 15f
    val bubbleColor = when {
        isAligned && isFlat -> AlignedEmerald
        isFlat -> DeepGoldAccent
        else -> WarningAmber
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                bubbleColor,
                bubbleColor.copy(alpha = 0.45f)
            ),
            center = bubbleCenter,
            radius = 7.dp.toPx()
        ),
        radius = 6.dp.toPx(),
        center = bubbleCenter
    )
}
