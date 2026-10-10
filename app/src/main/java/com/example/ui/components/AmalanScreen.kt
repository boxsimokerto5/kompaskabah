package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
                    ttsHelper.stop()
                    selectedCategory = category
                },
                onOpenSettings = onOpenSettings
            )
        } else {
            // Tampilan Detail Sub-Screen
            AmalanDetailSubScreen(
                category = selectedCategory!!,
                ttsHelper = ttsHelper,
                isPlaying = isPlaying,
                currentlyPlayingId = currentlyPlayingId,
                onBack = {
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
    onOpenSettings: () -> Unit = {}
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

        // 3. Grid of Luxury Golden Ornament Cards ("kotak emas ornamen mewah")
        items(filteredCategories, key = { it.name }) { category ->
            LuxuryGoldOrnamentCard(
                category = category,
                onClick = { onSelectCategory(category) }
            )
        }
    }
}

/**
 * Kartu Kotak Emas Ornamen Mewah (Luxury Gold Ornament Box)
 */
@Composable
private fun LuxuryGoldOrnamentCard(
    category: AmalanCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0xFFD4AF37),
                spotColor = Color(0xFF8A5A00)
            )
            .clickable { onClick() }
            .testTag("amalan_card_${category.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.6.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    GoldGradientTop,
                    GoldGradientMid,
                    GoldGradientDark,
                    GoldGradientTop
                )
            )
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF261D0C),
                            Color(0xFF1B1408),
                            Color(0xFF130E05)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Ornate Corner Flourish (Top Right)
            Text(
                text = "۩",
                color = GoldGradientTop.copy(alpha = 0.18f),
                fontSize = 44.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Golden Geometric Ornament Avatar Box
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF6B4E12),
                                    Color(0xFF3D2A07),
                                    Color(0xFF1A1203)
                                )
                            )
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                listOf(GoldGradientTop, GoldGradientMid, GoldGradientDark)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.ornamentSymbol,
                        fontSize = 28.sp,
                        color = GoldGradientTop,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFF7DC),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Golden Pill Badge
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = GoldBadgeBg,
                            border = BorderStroke(1.dp, GoldGradientMid.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = category.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldGradientTop,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = category.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = GoldGradientMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.5.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
