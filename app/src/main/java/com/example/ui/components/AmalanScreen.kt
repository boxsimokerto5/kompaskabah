package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.amalan.AmalanCategory
import com.example.domain.amalan.AsmaulHusnaData
import com.example.domain.amalan.DoaSholatData
import com.example.domain.amalan.SholatData
import com.example.domain.amalan.SholawatData
import com.example.domain.amalan.TahlilData
import com.example.domain.amalan.WudhuData
import com.example.domain.amalan.YasinData
import com.example.service.IslamicTtsHelper
import com.example.ads.IronSourceAdManager
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent

// Palette Warna Emas Mewah Islami (Royal Islamic Gold & Emerald)
val GoldGradientTop = Color(0xFFFFE89C)
val GoldGradientMid = Color(0xFFD4AF37)
val GoldGradientDark = Color(0xFF8A5A00)
val GoldDarkBackground = Color(0xFF1E170A)
val GoldCardBorder = Color(0xFFE5C07B)
val GoldBadgeBg = Color(0xFF38290D)
val EmeraldAccent = Color(0xFF0F382B)

@Composable
fun AmalanScreen(
    onNavigateBackToCompass: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenTasbih: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ttsHelper = remember { IslamicTtsHelper(context) }
    val isPlaying by ttsHelper.isPlayingFlow.collectAsState()
    val currentlyPlayingId by ttsHelper.currentlyPlayingId.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    var selectedCategory by remember { mutableStateOf<AmalanCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    if (selectedCategory != null) {
        BackHandler {
            ttsHelper.stop()
            selectedCategory = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedCategory == null) {
            // Tampilan Menu Utama Amalan: Kotak Emas Ornamen Mewah
            AmalanMainMenu(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onSelectCategory = { category ->
                    IronSourceAdManager.recordClick(context)
                    ttsHelper.stop()
                    selectedCategory = category
                },
                onOpenSettings = onOpenSettings,
                onOpenTasbih = onOpenTasbih
            )
        } else {
            // Tampilan Detail Sub-Screen
            AmalanDetailSubScreen(
                category = selectedCategory!!,
                ttsHelper = ttsHelper,
                isPlaying = isPlaying,
                currentlyPlayingId = currentlyPlayingId,
                onBack = {
                    IronSourceAdManager.recordClick(context)
                    ttsHelper.stop()
                    selectedCategory = null
                }
            )
        }
    }
}

@Composable
private fun AmalanMainMenu(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (AmalanCategory) -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenTasbih: () -> Unit = {}
) {
    val categories = remember { AmalanCategory.values().toList() }
    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            categories
        } else {
            categories.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("amalan_main_menu_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Banner Ornamen Emas Mewah
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .testTag("amalan_hero_banner")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_gold_islamic_ornament_1791643531933),
                    contentDescription = "Ornamen Emas Mewah Amalan Islami",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Atmospheric Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color(0x660B1D3A),
                                0.55f to Color(0x990A1726),
                                1.0f to MaterialTheme.colorScheme.background
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = GoldBadgeBg,
                            border = BorderStroke(1.dp, GoldGradientMid),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = "۞", color = GoldGradientTop, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "KUMPULAN AMALAN ISLAMI",
                                    color = GoldGradientTop,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Surface(
                            onClick = onOpenSettings,
                            shape = CircleShape,
                            color = GoldBadgeBg,
                            border = BorderStroke(1.dp, GoldGradientMid),
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_amalan_top_settings")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Pengaturan",
                                    tint = GoldGradientTop,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Amalan & Ibadah Harian",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Pedoman ibadah, dzikir, doa sholat, yasin & sholawat dengan audio",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }
        }

        // 2. Search Field
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amalan_search_input"),
                    placeholder = {
                        Text(
                            text = "Cari amalan, doa, sholat, sholawat...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GoldGradientMid
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldGradientMid,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // 3. Featured Interactive Digital Tasbih Card with Moving Beads & Sujud Hero
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                border = BorderStroke(1.5.dp, DeepGoldAccent.copy(alpha = 0.55f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenTasbih() }
                    .testTag("amalan_tasbih_digital_featured_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_sujud_grand_mosque_1791732157493),
                            contentDescription = "Tasbih Digital Hero",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "📿 Tasbih Digital",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepEmeraldPrimary
                            ) {
                                Text(
                                    text = "Butiran Bergerak",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Hitung dzikir dengan animasi butiran fisik & visual sujud sholat di masjid megah",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 4. Dynamic Colorful Game-Banner Style Amalan Cards (Sesuai Desain Gambar 2)
        items(filteredCategories, key = { it.name }) { category ->
            ModernAmalanBannerCard(
                category = category,
                onClick = { onSelectCategory(category) }
            )
        }
    }
}

/**
 * Konfigurasi tema visual kartu amalan bergaya banner interaktif dengan latar ilustrasi lanskap
 */
private data class AmalanBannerTheme(
    val imageRes: Int,
    val bgGradient: List<Color>,
    val tagIcon: String,
    val tagLabel: String,
    val subHeadline: String,
    val mainHeadline: String,
    val featureHighlight: String,
    val ctaLabel: String,
    val ctaGradient: List<Color>,
    val ctaTextColor: Color,
    val watermarkEmblem: String
)

private fun getAmalanBannerTheme(category: AmalanCategory): AmalanBannerTheme {
    return when (category) {
        AmalanCategory.ASMAUL_HUSNA -> AmalanBannerTheme(
            imageRes = R.drawable.img_asmaul_husna_stars_1791679969150,
            bgGradient = listOf(Color(0xFF072B22), Color(0xFF0F5A47), Color(0xFF1B7F64)),
            tagIcon = "💎",
            tagLabel = "99 Sifat Keagungan",
            subHeadline = "Kenali & Hafalkan Nama Allah",
            mainHeadline = "99 ASMAUL HUSNA",
            featureHighlight = "Teks Arab, Latin, Makna & Audio Murottal",
            ctaLabel = "Dengarkan",
            ctaGradient = listOf(Color(0xFF00E5FF), Color(0xFF00B0FF)),
            ctaTextColor = Color(0xFF003644),
            watermarkEmblem = "ﷲ"
        )
        AmalanCategory.TATA_CARA_WUDHU -> AmalanBannerTheme(
            imageRes = R.drawable.img_oasis_wudhu_spring_1791679946849,
            bgGradient = listOf(Color(0xFF06333D), Color(0xFF0B586B), Color(0xFF0E7D91)),
            tagIcon = "💧",
            tagLabel = "Syarat Sah Sholat",
            subHeadline = "Bersuci Sesuai Tuntunan Sunnah",
            mainHeadline = "PANDUAN WUDHU",
            featureHighlight = "Urutan Rukun, Doa & Adab Bersuci",
            ctaLabel = "Pelajari",
            ctaGradient = listOf(Color(0xFF69F0AE), Color(0xFF00E676)),
            ctaTextColor = Color(0xFF003D19),
            watermarkEmblem = "۩"
        )
        AmalanCategory.TATA_CARA_SHOLAT -> AmalanBannerTheme(
            imageRes = R.drawable.img_grand_mosque_blue_sky_1791642671931,
            bgGradient = listOf(Color(0xFF0C1F3B), Color(0xFF162E56), Color(0xFF1F467D)),
            tagIcon = "🕌",
            tagLabel = "Tiang Utama Agama",
            subHeadline = "Panduan Sholat Fardhu & Sunnah",
            mainHeadline = "PANDUAN SHOLAT",
            featureHighlight = "Gerakan Rukun, Bacaan Arab & Audio Suara",
            ctaLabel = "Pelajari",
            ctaGradient = listOf(Color(0xFFFFD54F), Color(0xFFFFB300)),
            ctaTextColor = Color(0xFF3E2723),
            watermarkEmblem = "۞"
        )
        AmalanCategory.YASIN -> AmalanBannerTheme(
            imageRes = R.drawable.img_quran_yasin_rehal_1791679991654,
            bgGradient = listOf(Color(0xFF3D1905), Color(0xFF6E320A), Color(0xFF9E4B0E)),
            tagIcon = "📖",
            tagLabel = "Jantung Al-Qur'an (83 Ayat)",
            subHeadline = "Pemberi Syafaat & Ketenangan Jiwa",
            mainHeadline = "SURAT YASIN",
            featureHighlight = "Ayat demi Ayat, Latin & Terjemah Lengkap",
            ctaLabel = "Mulai Baca",
            ctaGradient = listOf(Color(0xFFFFF176), Color(0xFFFFEE58)),
            ctaTextColor = Color(0xFF422B00),
            watermarkEmblem = "📖"
        )
        AmalanCategory.TAHLIL -> AmalanBannerTheme(
            imageRes = R.drawable.img_tasbih_lantern_tahlil_1791680016525,
            bgGradient = listOf(Color(0xFF281814), Color(0xFF442B24), Color(0xFF634137)),
            tagIcon = "📿",
            tagLabel = "Dzikir & Doa Arwah",
            subHeadline = "Susunan Majelis Tahlil Lengkap",
            mainHeadline = "BACAAN TAHLIL",
            featureHighlight = "Istighfar, Tasbih, Tahlil & Doa Arwah",
            ctaLabel = "Buka Doa",
            ctaGradient = listOf(Color(0xFFFFCC80), Color(0xFFFFA726)),
            ctaTextColor = Color(0xFF3E2723),
            watermarkEmblem = "📿"
        )
        AmalanCategory.DOA_SETELAH_SHOLAT -> AmalanBannerTheme(
            imageRes = R.drawable.img_desert_prayer_hero_1791637232343,
            bgGradient = listOf(Color(0xFF27094F), Color(0xFF42127E), Color(0xFF611A93)),
            tagIcon = "🤲",
            tagLabel = "Doa Mustajab Pilihan",
            subHeadline = "Senjata Utama Orang Beriman",
            mainHeadline = "DOA & WIRID SHOLAT",
            featureHighlight = "Dzikir Rutin Ba'da Sholat & Doa Pilihan",
            ctaLabel = "Amalkan",
            ctaGradient = listOf(Color(0xFFEA80FC), Color(0xFFB388FF)),
            ctaTextColor = Color(0xFF28064D),
            watermarkEmblem = "🤲"
        )
        AmalanCategory.SHOLAWAT_LENGKAP -> AmalanBannerTheme(
            imageRes = R.drawable.img_nabawi_green_dome_1791680082165,
            bgGradient = listOf(Color(0xFF4A0629), Color(0xFF7B0D47), Color(0xFF9E1258)),
            tagIcon = "✨",
            tagLabel = "Raih Syafaat Rasulullah ﷺ",
            subHeadline = "8 Sholawat Agung & Fadhilah",
            mainHeadline = "SHOLAWAT NABI",
            featureHighlight = "Nariyah, Tibbil Qulub, Jibril & Badar",
            ctaLabel = "Lantunkan",
            ctaGradient = listOf(Color(0xFFFF80AB), Color(0xFFFF4081)),
            ctaTextColor = Color(0xFF4A0020),
            watermarkEmblem = "✨"
        )
    }
}

/**
 * Kartu Amalan Bergaya Banner Horizontal Interaktif dengan Latar Ilustrasi Gambar (Gambar 2 Style)
 */
@Composable
private fun ModernAmalanBannerCard(
    category: AmalanCategory,
    onClick: () -> Unit
) {
    val theme = remember(category) { getAmalanBannerTheme(category) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = theme.bgGradient.first().copy(alpha = 0.5f),
                spotColor = theme.bgGradient.last().copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("amalan_card_${category.name.lowercase()}"),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.30f)
                )
            )
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // 1. Gambar Ilustrasi Lanskap Tematik sebagai Background
            Image(
                painter = painterResource(id = theme.imageRes),
                contentDescription = theme.mainHeadline,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Lapisan Gradien Warna Protektif (Tebal di kiri untuk keterbacaan teks, transparan di kanan)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0.0f to theme.bgGradient[0].copy(alpha = 0.94f),
                            0.55f to theme.bgGradient[1].copy(alpha = 0.82f),
                            1.0f to theme.bgGradient[2].copy(alpha = 0.40f)
                        )
                    )
            )

            // 3. Watermark Ornamen Kaligrafi Islami Halus
            Text(
                text = theme.watermarkEmblem,
                color = Color.White.copy(alpha = 0.16f),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 85.dp)
            )

            // 4. Konten Utama: Teks di Kiri & Tombol Aksi Kapsul di Kanan
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Kolom Kiri: Badge Tag + Sub-Headline + Judul Utama Bold Besar + Deskripsi Fitur
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Small Pill Tag
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.28f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(text = theme.tagIcon, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = theme.tagLabel,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = theme.subHeadline,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.92f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = theme.mainHeadline,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = theme.featureHighlight,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Kolom Kanan: Tombol Timbul 3D Berdenyut Khas Game (Pulsing 3D Game Capsule Button)
                GamePulsing3DButton(
                    label = theme.ctaLabel,
                    textColor = theme.ctaTextColor,
                    gradientColors = theme.ctaGradient,
                    onClick = onClick,
                    testTag = "btn_action_${category.name.lowercase()}"
                )
            }
        }
    }
}

/**
 * Tombol Timbul 3D Berdenyut (Game-Style Pulsing 3D Capsule Button)
 * Menampilkan efek timbul dengan bevel gelap di bawah, kilau specular highlight di atas,
 * dan animasi denyut lembut (breathing/pulse) yang hidup.
 */
@Composable
private fun GamePulsing3DButton(
    label: String,
    textColor: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val bottomBevelColor = remember(gradientColors) {
        val base = gradientColors.firstOrNull() ?: Color(0xFFD4AF37)
        Color(
            red = (base.red * 0.42f).coerceIn(0f, 1f),
            green = (base.green * 0.42f).coerceIn(0f, 1f),
            blue = (base.blue * 0.42f).coerceIn(0f, 1f),
            alpha = 1.0f
        )
    }

    Box(
        modifier = modifier
            .scale(pulseScale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(50),
                ambientColor = gradientColors.last().copy(alpha = glowAlpha),
                spotColor = gradientColors.first().copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Lapisan 1: Dasar Tebal 3D (Bevel Gelap Bawah)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(bottomBevelColor)
        )

        // Lapisan 2: Tutup Muka Timbul 3D (Offset ke atas menyisakan bevel bawah 3.5dp)
        Box(
            modifier = Modifier
                .padding(bottom = 3.5.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
                .background(Brush.horizontalGradient(gradientColors))
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.30f)
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 18.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            // Lapisan 3: Kilau Kaca Mengkilap (Specular Gel Sheen di Bagian Atas)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Teks Label Timbul Kontras Tinggi
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    shadow = Shadow(
                        color = Color.White.copy(alpha = 0.50f),
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                ),
                fontWeight = FontWeight.Black,
                maxLines = 1,
                letterSpacing = 0.4.sp
            )
        }
    }
}

/**
 * Tampilan Layar Detail untuk setiap Kategori Amalan
 */
@Composable
private fun AmalanDetailSubScreen(
    category: AmalanCategory,
    ttsHelper: IslamicTtsHelper,
    isPlaying: Boolean,
    currentlyPlayingId: String?,
    onBack: () -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val copyToClipboard: (String, String) -> Unit = { label, text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label disalin ke papan klip", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("amalan_subscreen_${category.name.lowercase()}")
    ) {
        // Top Bar dengan Tombol Kembali & Judul Emas
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_amalan_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Menu Amalan",
                        tint = GoldGradientMid
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = category.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldGradientMid
                    )
                }

                // Global Stop Audio jika sedang memutar
                if (isPlaying) {
                    IconButton(
                        onClick = { ttsHelper.stop() },
                        modifier = Modifier.testTag("btn_stop_audio_global")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Hentikan Suara",
                            tint = Color(0xFFFF5252)
                        )
                    }
                }
            }
        }

        // Content Area berdasarkan kategori
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (category) {
                AmalanCategory.ASMAUL_HUSNA -> {
                    item {
                        DetailHeaderCard(
                            title = "99 Asmaul Husna",
                            subtitle = "Barangsiapa menghafal dan mengamalkannya, niscaya masuk surga (HR. Bukhari)",
                            iconSymbol = "ﷲ"
                        )
                    }
                    items(AsmaulHusnaData.items, key = { it.number }) { item ->
                        val isItemPlaying = isPlaying && currentlyPlayingId == "asma_${item.number}"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldGradientMid.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldBadgeBg,
                                        border = BorderStroke(1.dp, GoldGradientMid)
                                    ) {
                                        Text(
                                            text = "#${item.number}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldGradientTop,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = item.arabic,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldGradientMid,
                                        textAlign = TextAlign.End
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.latin,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = item.meaningId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            copyToClipboard(item.latin, "${item.arabic}\n${item.latin} (${item.meaningId})")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Salin",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            ttsHelper.speakRecitation(
                                                id = "asma_${item.number}",
                                                arabicText = item.arabic,
                                                fallbackLatinText = item.latin
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isItemPlaying) Color(0xFFFF5252) else GoldBadgeBg
                                        ),
                                        border = BorderStroke(1.dp, GoldGradientMid),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isItemPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = if (isItemPlaying) Color.White else GoldGradientTop,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isItemPlaying) "Stop" else "Dengarkan",
                                            fontSize = 11.sp,
                                            color = if (isItemPlaying) Color.White else GoldGradientTop,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AmalanCategory.TATA_CARA_WUDHU -> {
                    item {
                        DetailHeaderCard(
                            title = "Panduan Wudhu Sempurna",
                            subtitle = "Panduan bersuci lengkap mulai dari niat, rukun, sunnah, hingga doa setelah wudhu.",
                            iconSymbol = "۩"
                        )
                    }
                    items(WudhuData.steps, key = { it.stepNumber }) { step ->
                        val isStepPlaying = isPlaying && currentlyPlayingId == "wudhu_${step.stepNumber}"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldGradientMid.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (step.isRukun) EmeraldAccent else GoldBadgeBg,
                                        border = BorderStroke(1.dp, if (step.isRukun) Color(0xFF34D399) else GoldGradientMid)
                                    ) {
                                        Text(
                                            text = if (step.isRukun) "Langkah ${step.stepNumber} • RUKUN" else "Langkah ${step.stepNumber} • SUNNAH",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (step.isRukun) Color(0xFF6EE7B7) else GoldGradientTop,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (step.arabicText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = step.arabicText,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldGradientMid,
                                                textAlign = TextAlign.End,
                                                lineHeight = 32.sp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = step.latinText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = step.translation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { copyToClipboard(step.title, "${step.arabicText}\n${step.latinText}\n${step.translation}") }) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Salin", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        }
                                        Button(
                                            onClick = {
                                                ttsHelper.speakRecitation("wudhu_${step.stepNumber}", step.arabicText, step.latinText)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isStepPlaying) Color(0xFFFF5252) else GoldBadgeBg),
                                            border = BorderStroke(1.dp, GoldGradientMid),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(imageVector = if (isStepPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = if (isStepPlaying) Color.White else GoldGradientTop, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = if (isStepPlaying) "Stop" else "Dengarkan Doa", fontSize = 11.sp, color = if (isStepPlaying) Color.White else GoldGradientTop, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AmalanCategory.TATA_CARA_SHOLAT -> {
                    item {
                        DetailHeaderCard(
                            title = "Tata Cara Sholat Lengkap",
                            subtitle = "Lengkap gerakan, bacaan Arab, Latin, arti, serta suara membaca audio per gerakan.",
                            iconSymbol = "۞"
                        )
                    }

                    // Section 1: Niat Sholat 5 Waktu
                    item {
                        Text(
                            text = "Niat Sholat Fardhu 5 Waktu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldGradientMid,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(SholatData.prayerNiyyahList, key = { it.id }) { niat ->
                        val isNiatPlaying = isPlaying && currentlyPlayingId == niat.id
                        RecitationCard(
                            title = niat.title,
                            badge = "Niat",
                            arabicText = niat.arabicText,
                            latinText = niat.latinText,
                            translation = niat.translation,
                            sourceNote = niat.sourceOrFadhilah,
                            isPlaying = isNiatPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation(niat.id, niat.arabicText, niat.latinText) },
                            onCopy = { copyToClipboard(niat.title, "${niat.arabicText}\n${niat.latinText}\n${niat.translation}") }
                        )
                    }

                    // Section 2: Urutan Gerakan & Bacaan Sholat
                    item {
                        Text(
                            text = "Rangkaian Gerakan & Bacaan Sholat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldGradientMid,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }

                    items(SholatData.steps, key = { it.stepNumber }) { step ->
                        val isStepPlaying = isPlaying && currentlyPlayingId == "sholat_${step.stepNumber}"
                        RecitationCard(
                            title = "${step.stepNumber}. ${step.movementName}",
                            badge = "Gerakan ${step.stepNumber}",
                            arabicText = step.arabicRecitation,
                            latinText = step.latinRecitation,
                            translation = step.indonesianMeaning,
                            sourceNote = step.explanation,
                            isPlaying = isStepPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation("sholat_${step.stepNumber}", step.arabicRecitation, step.latinRecitation) },
                            onCopy = { copyToClipboard(step.movementName, "${step.arabicRecitation}\n${step.latinRecitation}\n${step.indonesianMeaning}") }
                        )
                    }
                }

                AmalanCategory.YASIN -> {
                    item {
                        DetailHeaderCard(
                            title = "Surat Yasin (83 Ayat)",
                            subtitle = "Jantung Al-Qur'an (Qalb Al-Qur'an), teks Arab lengkap, Latin, terjemah, dan audio.",
                            iconSymbol = "📖"
                        )
                    }
                    items(YasinData.verses, key = { it.ayatNumber }) { verse ->
                        val isVersePlaying = isPlaying && currentlyPlayingId == "yasin_${verse.ayatNumber}"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldGradientMid.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldBadgeBg,
                                        border = BorderStroke(1.dp, GoldGradientMid)
                                    ) {
                                        Text(
                                            text = "Ayat ${verse.ayatNumber}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldGradientTop,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = { copyToClipboard("Ayat ${verse.ayatNumber}", "${verse.arabicText}\n${verse.latinText}\n${verse.indonesianTranslation}") }) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Salin", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(onClick = { ttsHelper.speakRecitation("yasin_${verse.ayatNumber}", verse.arabicText, verse.latinText) }) {
                                            Icon(
                                                imageVector = if (isVersePlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Audio Ayat",
                                                tint = if (isVersePlaying) Color(0xFFFF5252) else GoldGradientMid
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = verse.arabicText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldGradientMid,
                                    textAlign = TextAlign.End,
                                    lineHeight = 34.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = verse.latinText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = verse.indonesianTranslation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                AmalanCategory.TAHLIL -> {
                    item {
                        DetailHeaderCard(
                            title = "Tahlil & Doa Arwah Lengkap",
                            subtitle = "Susunan bacaan tahlil dari Hadhrat Al-Fatihah, surat pendek, inti tahlil, hingga doa arwah.",
                            iconSymbol = "📿"
                        )
                    }
                    items(TahlilData.items, key = { it.orderNumber }) { tahlil ->
                        val isTahlilPlaying = isPlaying && currentlyPlayingId == "tahlil_${tahlil.orderNumber}"
                        RecitationCard(
                            title = tahlil.sectionTitle,
                            badge = tahlil.repetitionNote,
                            arabicText = tahlil.arabicText,
                            latinText = tahlil.latinText,
                            translation = tahlil.translation,
                            sourceNote = "Dibaca sebanyak ${tahlil.repetitionNote}",
                            isPlaying = isTahlilPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation("tahlil_${tahlil.orderNumber}", tahlil.arabicText, tahlil.latinText) },
                            onCopy = { copyToClipboard(tahlil.sectionTitle, "${tahlil.arabicText}\n${tahlil.latinText}\n${tahlil.translation}") }
                        )
                    }
                }

                AmalanCategory.DOA_SETELAH_SHOLAT -> {
                    item {
                        DetailHeaderCard(
                            title = "Dzikir & Doa Setelah Sholat",
                            subtitle = "Wirid ma'tsur sesudah sholat fardhu dan kumpulan doa-doa mustajab para ulama.",
                            iconSymbol = "🤲"
                        )
                    }

                    item {
                        Text(
                            text = "Wirid Dzikir Ba'da Sholat Fardhu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldGradientMid,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(DoaSholatData.dzikirList, key = { it.id }) { dzikir ->
                        val isDzikirPlaying = isPlaying && currentlyPlayingId == dzikir.id
                        RecitationCard(
                            title = dzikir.title,
                            badge = "Dzikir",
                            arabicText = dzikir.arabicText,
                            latinText = dzikir.latinText,
                            translation = dzikir.translation,
                            sourceNote = dzikir.sourceOrFadhilah,
                            isPlaying = isDzikirPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation(dzikir.id, dzikir.arabicText, dzikir.latinText) },
                            onCopy = { copyToClipboard(dzikir.title, "${dzikir.arabicText}\n${dzikir.latinText}\n${dzikir.translation}") }
                        )
                    }

                    item {
                        Text(
                            text = "Kumpulan Doa Mustajab Pilihan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldGradientMid,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }

                    items(DoaSholatData.kumpulanDoaMustajab, key = { it.id }) { doa ->
                        val isDoaPlaying = isPlaying && currentlyPlayingId == doa.id
                        RecitationCard(
                            title = doa.title,
                            badge = "Doa Mustajab",
                            arabicText = doa.arabicText,
                            latinText = doa.latinText,
                            translation = doa.translation,
                            sourceNote = doa.sourceOrFadhilah,
                            isPlaying = isDoaPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation(doa.id, doa.arabicText, doa.latinText) },
                            onCopy = { copyToClipboard(doa.title, "${doa.arabicText}\n${doa.latinText}\n${doa.translation}") }
                        )
                    }
                }

                AmalanCategory.SHOLAWAT_LENGKAP -> {
                    item {
                        DetailHeaderCard(
                            title = "Kumpulan Sholawat Lengkap",
                            subtitle = "8 Sholawat agung masyhur lengkap dengan teks Arab berharakat, Latin, terjemah & fadhilah.",
                            iconSymbol = "✨"
                        )
                    }
                    items(SholawatData.items, key = { it.id }) { sholawat ->
                        val isSholawatPlaying = isPlaying && currentlyPlayingId == sholawat.id
                        RecitationCard(
                            title = sholawat.title,
                            badge = "Sholawat",
                            arabicText = sholawat.arabicText,
                            latinText = sholawat.latinText,
                            translation = sholawat.translation,
                            sourceNote = "Fadhilah: ${sholawat.fadhilah}",
                            isPlaying = isSholawatPlaying,
                            onPlayAudio = { ttsHelper.speakRecitation(sholawat.id, sholawat.arabicText, sholawat.latinText) },
                            onCopy = { copyToClipboard(sholawat.title, "${sholawat.arabicText}\n${sholawat.latinText}\n${sholawat.translation}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeaderCard(
    title: String,
    subtitle: String,
    iconSymbol: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.4.dp,
                Brush.linearGradient(listOf(GoldGradientTop, GoldGradientMid, GoldGradientDark)),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF281E0C), Color(0xFF1B1408))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GoldBadgeBg)
                        .border(1.dp, GoldGradientMid, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconSymbol, fontSize = 26.sp, color = GoldGradientTop)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecitationCard(
    title: String,
    badge: String,
    arabicText: String,
    latinText: String,
    translation: String,
    sourceNote: String,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldGradientMid.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldBadgeBg,
                    border = BorderStroke(1.dp, GoldGradientMid.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldGradientTop,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Bacaan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = onPlayAudio,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFFF5252) else GoldBadgeBg
                        ),
                        border = BorderStroke(1.dp, GoldGradientMid),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = if (isPlaying) Color.White else GoldGradientTop,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "Stop" else "Suara",
                            fontSize = 11.sp,
                            color = if (isPlaying) Color.White else GoldGradientTop,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Teks Arab Berharakat Jelas
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = arabicText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldGradientMid,
                    textAlign = TextAlign.End,
                    lineHeight = 34.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = latinText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = translation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (sourceNote.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = sourceNote,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
