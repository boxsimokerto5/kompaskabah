package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import kotlinx.coroutines.delay

/**
 * Splash Screen visual penuh bertema oasis padang pasir dengan kafilah saudagar dan unta,
 * dilengkapi emblem logo Ka'bah megah bercahaya di bagian tengah.
 */
@Composable
fun AppSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Long = 2600L
) {
    val scaleAnim = remember { Animatable(0.75f) }
    val alphaAnim = remember { Animatable(0.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "kaaba_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(Unit) {
        // Animasi kemunculan logo Ka'bah
        alphaAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(700, easing = LinearEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )

        // Tunggu durasi splash sebelum masuk ke aplikasi utama
        delay(durationMs)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Sentuhan langsung melewati splash screen (skip)
                onSplashFinished()
            }
            .testTag("app_splash_screen")
    ) {
        // 1. Gambar latar pemandangan gurun pasir, oasis air jernih, pohon kurma, saudagar dan unta
        Image(
            painter = painterResource(id = R.drawable.img_splash_oasis_merchants_1791856374406),
            contentDescription = "Pemandangan Gurun Pasir dan Oasis dengan Kafilah Unta",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lapisan gradasi atas (untuk status bar & pencahayaan langit)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99000000),
                            Color(0x33000000),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. Lapisan vignette lembut di bagian tengah agar logo Ka'bah semakin menonjol
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33000000),
                            Color(0x88051A10)
                        ),
                        radius = 1100f
                    )
                )
        )

        // 4. Lapisan gradasi bawah (untuk teks kaki & indikator memuat)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x66082417),
                            Color(0xDD04130C)
                        )
                    )
                )
        )

        // 5. Bagian Tengah: Emblem Logo Ka'bah Megah & Judul Aplikasi
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                // Lingkaran Cahaya Emas Berdenyut di Belakang Ka'bah
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .scale(glowPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.45f),
                                    Color(0xFFFFA000).copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Cincin Emas Lingkaran Utama
                Surface(
                    shape = CircleShape,
                    color = Color(0xDD0B2418),
                    border = BorderStroke(2.5.dp, Brush.linearGradient(
                        listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFFFFE082))
                    )),
                    shadowElevation = 18.dp,
                    modifier = Modifier
                        .size(136.dp)
                        .shadow(16.dp, CircleShape, spotColor = Color(0xFFFFD54F))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Kanvas Vektor Logo Ka'bah Presisi Emas & Hitam
                        KaabaVectorLogo(
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Judul Aplikasi
            Text(
                text = "KOMPAS KIBLAT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.85f),
                        offset = Offset(0f, 3f),
                        blurRadius = 6f
                    )
                ),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color(0xFFFFE599),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-judul / Deskripsi Fitur
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xBB082015),
                border = BorderStroke(0.8.dp, Color(0x88FFD54F)),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "Arah Kiblat • Waktu Sholat • Hadits Nabawi",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }
        }

        // 6. Bagian Bawah: Indikator Memuat & Versi Aplikasi
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFFFFD54F),
                strokeWidth = 2.5.dp,
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bismillah... Memuat Panduan Ibadah",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Versi 1.1",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Vektor Logo Ka'bah detail dengan Kiswah hitam, sabuk kaligrafi emas (Hizam),
 * pintu Ka'bah emas (Bab ar-Rahman), dan Rukun Yamani / Hajar Aswad.
 */
@Composable
private fun KaabaVectorLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dimensi Kubus Ka'bah
        val cubeWidth = w * 0.70f
        val cubeHeight = h * 0.72f
        val left = (w - cubeWidth) / 2f
        val top = (h - cubeHeight) / 2f + (h * 0.04f)

        // 1. Bayangan dasar kubus Ka'bah
        drawRoundRect(
            color = Color(0xAA000000),
            topLeft = Offset(left - 3f, top + 3f),
            size = Size(cubeWidth + 6f, cubeHeight + 6f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // 2. Dinding Utama Ka'bah (Batu Marmer Hitam Kiswah)
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF262626),
                    Color(0xFF141414),
                    Color(0xFF0A0A0A)
                ),
                startY = top,
                endY = top + cubeHeight
            ),
            topLeft = Offset(left, top),
            size = Size(cubeWidth, cubeHeight),
            cornerRadius = CornerRadius(5f, 5f)
        )

        // 3. Garis Atap Ka'bah (Mizab ar-Rahmah)
        drawRect(
            color = Color(0xFF383838),
            topLeft = Offset(left, top),
            size = Size(cubeWidth, cubeHeight * 0.08f)
        )

        // 4. Sabuk Kiswah Emas Utama (Al-Hizam) melingkari bagian atas Ka'bah
        val beltTop = top + (cubeHeight * 0.22f)
        val beltHeight = cubeHeight * 0.16f
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFD54F),
                    Color(0xFFFFF176),
                    Color(0xFFFFB300),
                    Color(0xFFFFD54F)
                )
            ),
            topLeft = Offset(left, beltTop),
            size = Size(cubeWidth, beltHeight)
        )

        // Detail ornamen sabuk emas (garis batas atas dan bawah sabuk)
        drawRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(left, beltTop),
            size = Size(cubeWidth, 1.8f)
        )
        drawRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(left, beltTop + beltHeight - 1.8f),
            size = Size(cubeWidth, 1.8f)
        )

        // 5. Pintu Emas Ka'bah (Bab Al-Ka'bah) di sisi kanan
        val doorWidth = cubeWidth * 0.26f
        val doorHeight = cubeHeight * 0.44f
        val doorLeft = left + (cubeWidth * 0.58f)
        val doorTop = beltTop + beltHeight + (cubeHeight * 0.04f)

        // Kusen pintu emas
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFF59D),
                    Color(0xFFFFD54F),
                    Color(0xFFFF8F00)
                )
            ),
            topLeft = Offset(doorLeft, doorTop),
            size = Size(doorWidth, doorHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Kusen dalam pintu
        drawRoundRect(
            color = Color(0xFFBF8F00),
            topLeft = Offset(doorLeft + 2.5f, doorTop + 2.5f),
            size = Size(doorWidth - 5f, doorHeight - 5f),
            cornerRadius = CornerRadius(3f, 3f),
            style = Stroke(width = 1.5f)
        )

        // Garis belahan tengah pintu
        drawLine(
            color = Color(0xFF6D4C41),
            start = Offset(doorLeft + (doorWidth / 2f), doorTop + 2f),
            end = Offset(doorLeft + (doorWidth / 2f), doorTop + doorHeight - 2f),
            strokeWidth = 1.5f
        )

        // 6. Pondasi Marmer Putih Ka'bah (Syadzarwan)
        val shadurwanHeight = cubeHeight * 0.09f
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFEEEEEE),
                    Color(0xFFBDBDBD)
                )
            ),
            topLeft = Offset(left - 2f, top + cubeHeight - shadurwanHeight),
            size = Size(cubeWidth + 4f, shadurwanHeight),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // 7. Simbol Sudut Hajar Aswad (Perak di pojok kiri bawah)
        val hajarRadius = 3.5f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFB0BEC5), Color(0xFF37474F))
            ),
            radius = hajarRadius,
            center = Offset(left + 5f, top + cubeHeight - shadurwanHeight - 3f)
        )
    }
}
