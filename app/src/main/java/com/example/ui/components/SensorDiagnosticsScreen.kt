package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SensorAccuracyLevel
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.MeccaGold
import com.example.ui.theme.MeccaGoldBright
import com.example.ui.theme.WarningAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SensorDiagnosticsScreen(
    uiState: QiblaUiState,
    onToggleTrueNorth: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
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
        // 1. Animated Figure-8 Calibration Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = null,
                        tint = MeccaGold
                    )
                    Text(
                        text = "Kalibrasi Magnetometer (Angka 8)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ayunkan dan putar pergelangan tangan Anda membentuk pola angka 8 (∞) di udara jauh dari benda logam atau casing bermagnet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Lemniscate (Figure-8) Canvas
                FigureEightCalibrationAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
            }
        }

        // 2. Real-Time Sensor Telemetry Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            modifier = Modifier.fillMaxWidth()
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Telemetri Sensor Real-Time",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    val (accLabel, accColor) = when (uiState.sensorAccuracy) {
                        SensorAccuracyLevel.HIGH -> "Akurasi Tinggi" to AlignedEmerald
                        SensorAccuracyLevel.MEDIUM -> "Akurasi Sedang" to MeccaGold
                        SensorAccuracyLevel.LOW -> "Perlu Kalibrasi" to WarningAmber
                        SensorAccuracyLevel.UNRELIABLE -> "Interferensi" to DangerCoral
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = accColor.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = accLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = accColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                TelemetryRow(
                    label = "Sumber Sensor Aktif",
                    value = uiState.sensorSourceName
                )
                TelemetryRow(
                    label = "Kuat Medan Magnet Bumi",
                    value = "%.1f µT (Normal: 25–65 µT)".format(uiState.magneticFieldStrengthUt),
                    isWarning = uiState.hasMagneticInterference
                )
                TelemetryRow(
                    label = "Deklinasi Magnetik (WMM)",
                    value = "%+.2f° terhadap Utara Magnetik".format(uiState.magneticDeclinationDeg)
                )
                TelemetryRow(
                    label = "Kemiringan Ponsel (Pitch / Roll)",
                    value = "Pitch %.1f° • Roll %.1f°".format(uiState.pitchDeg, uiState.rollDeg),
                    isWarning = uiState.isDeviceTilted
                )
                TelemetryRow(
                    label = "Koordinat Ka'bah (Makkah)",
                    value = "21.4225° LU, 39.8262° BT"
                )
            }
        }

        // 3. Precision & UX Preferences Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Pengaturan Presisi & Navigasi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                PreferenceSwitchRow(
                    icon = Icons.Default.Explore,
                    title = "Koreksi Kutub Utara Sejati (True North)",
                    subtitle = "Mengoreksi kompas magnetik dengan deklinasi geomagnetik GPS (${"%+.2f°".format(uiState.magneticDeclinationDeg)})",
                    checked = uiState.useTrueNorthCorrection,
                    onCheckedChange = onToggleTrueNorth,
                    testTag = "switch_true_north"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                PreferenceSwitchRow(
                    icon = Icons.Default.Vibration,
                    title = "Getar Haptik Saat Tepat Kiblat",
                    subtitle = "Memberikan getaran lembut ketika tanda panah tepat mengarah ke Ka'bah (±3°)",
                    checked = uiState.hapticFeedbackEnabled,
                    onCheckedChange = onToggleHaptic,
                    testTag = "switch_haptic_feedback"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                PreferenceSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Mode Malam (Tema Gelap)",
                    subtitle = "Beralih antara tampilan terang mutiara (default) dan tampilan malam zamrud gelap",
                    checked = uiState.darkThemeEnabled,
                    onCheckedChange = onToggleDarkTheme,
                    testTag = "switch_dark_theme"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FigureEightCalibrationAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "figure_eight")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lemniscate_phase"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val scaleX = size.width * 0.34f
        val scaleY = size.height * 0.38f

        val path = Path()
        val steps = 120
        for (i in 0..steps) {
            val t = (i.toFloat() / steps) * (2.0 * PI)
            val x = centerX + scaleX * sin(t).toFloat()
            val y = centerY + scaleY * sin(t).toFloat() * cos(t).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // Draw infinity track
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MeccaGold.copy(alpha = 0.3f),
                    AlignedEmerald.copy(alpha = 0.65f),
                    MeccaGold.copy(alpha = 0.3f)
                )
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw animated glowing puck tracing the figure-8
        val puckX = centerX + scaleX * sin(phase.toDouble()).toFloat()
        val puckY = centerY + scaleY * sin(phase.toDouble()).toFloat() * cos(phase.toDouble()).toFloat()
        val puckCenter = Offset(puckX, puckY)

        drawCircle(
            color = AlignedEmerald.copy(alpha = 0.35f),
            radius = 18.dp.toPx(),
            center = puckCenter
        )
        drawCircle(
            color = MeccaGoldBright,
            radius = 8.dp.toPx(),
            center = puckCenter
        )
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.45f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(0.55f)
        ) {
            if (isWarning) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = if (isWarning) WarningAmber else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
