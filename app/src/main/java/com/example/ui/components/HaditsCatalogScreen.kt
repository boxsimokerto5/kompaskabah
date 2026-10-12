package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.IronSourceAdManager
import com.example.data.network.hadith.HadithBook
import com.example.data.network.hadith.HadithBookSection
import com.example.data.network.hadith.HadithNetworkClient
import com.example.data.network.hadith.HadithParsedItem
import com.example.data.network.hadith.HadithRepository
import com.example.data.network.hadith.HadithRepositoryImpl
import com.example.domain.hadits.HaditsData
import com.example.domain.hadits.HaditsGrade
import com.example.domain.hadits.HaditsItem
import com.example.domain.hadits.HaditsTopic
import com.example.service.IslamicTtsHelper
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import kotlinx.coroutines.launch

enum class HaditsScreenTab {
    TEMATIK_DERAJAT,
    KUMPULAN_KITAB
}

@Composable
fun HaditsCatalogScreen(
    onNavigateBack: () -> Unit,
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

    HaditsCatalogContent(
        ttsHelper = ttsHelper,
        isPlaying = isPlaying,
        currentlyPlayingId = currentlyPlayingId,
        onBack = onNavigateBack,
        onOpenSettings = onOpenSettings,
        modifier = modifier
    )
}

@Composable
fun HaditsCatalogContent(
    ttsHelper: IslamicTtsHelper,
    isPlaying: Boolean,
    currentlyPlayingId: String?,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hadithRepository: HadithRepository = remember { HadithRepositoryImpl(HadithNetworkClient.apiService) }

    var currentTab by remember { mutableStateOf(HaditsScreenTab.KUMPULAN_KITAB) }
    var selectedBook by remember { mutableStateOf<HadithBook?>(null) }
    var selectedSection by remember { mutableStateOf<HadithBookSection?>(null) }
    var showKnowledgeDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var kumpulanKitabSearchQuery by remember { mutableStateOf("") }
    var kumpulanKitabSearchFilter by remember { mutableStateOf(KumpulanKitabSearchFilter.ALL) }

    // Intercept back button based on nested level
    BackHandler {
        ttsHelper.stop()
        when {
            selectedSection != null -> {
                selectedSection = null
            }
            selectedBook != null -> {
                selectedBook = null
            }
            else -> {
                onBack()
            }
        }
    }

    if (showKnowledgeDialog) {
        HaditsKnowledgeDialog(onDismiss = { showKnowledgeDialog = false })
    }

    // Pop-Up Dialog Pencarian Kumpulan Kitab & Bab
    if (showSearchDialog) {
        KumpulanKitabSearchPopUpDialog(
            initialQuery = kumpulanKitabSearchQuery,
            initialFilter = kumpulanKitabSearchFilter,
            repository = hadithRepository,
            onApplySearch = { query, filter ->
                kumpulanKitabSearchQuery = query
                kumpulanKitabSearchFilter = filter
                currentTab = HaditsScreenTab.KUMPULAN_KITAB
                showSearchDialog = false
            },
            onSelectBook = { book ->
                selectedBook = book
                selectedSection = null
                currentTab = HaditsScreenTab.KUMPULAN_KITAB
                showSearchDialog = false
            },
            onSelectBookAndSection = { book, section ->
                selectedBook = book
                selectedSection = section
                currentTab = HaditsScreenTab.KUMPULAN_KITAB
                showSearchDialog = false
            },
            onDismiss = { showSearchDialog = false }
        )
    }

    val pageBackgroundColor = MaterialTheme.colorScheme.background

    if (selectedBook != null || selectedSection != null) {
        val currentBook = selectedBook
        val currentSection = selectedSection
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(pageBackgroundColor)
                .statusBarsPadding()
        ) {
            when {
                currentBook != null && currentSection != null -> {
                    OnlineHadithDetailList(
                        book = currentBook,
                        section = currentSection,
                        repository = hadithRepository,
                        ttsHelper = ttsHelper,
                        isPlaying = isPlaying,
                        currentlyPlayingId = currentlyPlayingId,
                        onBack = {
                            ttsHelper.stop()
                            selectedSection = null
                        }
                    )
                }
                currentBook != null -> {
                    BookSectionsVerticalList(
                        book = currentBook,
                        onSelectSection = { section -> selectedSection = section },
                        onBack = {
                            ttsHelper.stop()
                            selectedBook = null
                            selectedSection = null
                        }
                    )
                }
            }
        }
    } else {
        // LEVEL 1: Main Catalog Header & Tabs
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(pageBackgroundColor)
        ) {
            // Full-Width Atmospheric Hero Banner (Arab Oasis & Camels)
            // Rata kiri & kanan (fillMaxWidth), menjangkau ke atas notifikasi (WindowInsets.statusBars),
            // serta dilengkapi gradasi atas dan bawah identik dengan halaman kompas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .testTag("hadits_hero_banner")
            ) {
                // Background image pemandangan orang arab, unta, oasis air jernih, pohon kurma, dan langit biru cerah
                Image(
                    painter = painterResource(id = R.drawable.img_arab_oasis_camels_1791805370717),
                    contentDescription = "Pemandangan Oasis Padang Pasir dengan Orang Arab dan Unta",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradasi atas (Scrim) untuk keterbacaan baris status/notifikasi & tombol navigasi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF062338).copy(alpha = 0.50f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Gradasi bawah yang menyatu lembut ke warna latar belakang halaman (seperti halaman kompas)
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

                // Konten di dalam banner: Navigasi atas di bawah baris notifikasi & Deskripsi koleksi hadits
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Baris Navigasi Atas (Kembali, Judul, Pencarian, Info, Pengaturan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Surface(
                                onClick = onBack,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.88f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_hadits_back")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Kembali",
                                        tint = DeepEmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Katalog Hadits",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        shadow = Shadow(
                                            color = Color.Black.copy(alpha = 0.75f),
                                            offset = Offset(0f, 1f),
                                            blurRadius = 3f
                                        )
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (currentTab == HaditsScreenTab.TEMATIK_DERAJAT) {
                                        "Shahih, Hasan, Dha'if & Edukasi Populer"
                                    } else {
                                        "7 Kitab Induk Hadits (Kutubus Sittah)"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        shadow = Shadow(
                                            color = Color.Black.copy(alpha = 0.75f),
                                            offset = Offset(0f, 1f),
                                            blurRadius = 2f
                                        )
                                    ),
                                    color = Color.White.copy(alpha = 0.90f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = { showSearchDialog = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.88f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_hadits_search_icon")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Pencarian Kitab & Hadits",
                                        tint = if (kumpulanKitabSearchQuery.isNotBlank()) DeepGoldAccent else DeepEmeraldPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    if (kumpulanKitabSearchQuery.isNotBlank()) {
                                        Surface(
                                            shape = CircleShape,
                                            color = DeepGoldAccent,
                                            modifier = Modifier
                                                .size(7.dp)
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                        ) {}
                                    }
                                }
                            }

                            Surface(
                                onClick = { showKnowledgeDialog = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.88f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("btn_hadits_knowledge")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Panduan Derajat Hadits",
                                        tint = DeepEmeraldPrimary,
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
                                    .testTag("btn_hadits_settings")
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

                    // Blok Informasi Bawah
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF133F2B).copy(alpha = 0.90f),
                            border = BorderStroke(1.dp, DeepGoldAccent),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (currentTab == HaditsScreenTab.TEMATIK_DERAJAT) "📜" else "☀️",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (currentTab == HaditsScreenTab.TEMATIK_DERAJAT) {
                                        "Hadits Nabawi Terverifikasi"
                                    } else {
                                        "Kutubus Sittah & Al-Muwatta"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFE599)
                                )
                            }
                        }

                        Text(
                            text = if (currentTab == HaditsScreenTab.TEMATIK_DERAJAT) {
                                "Koleksi Hadits & Sanad"
                            } else {
                                "Kumpulan Kitab Induk Hadits"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    offset = Offset(0f, 1f),
                                    blurRadius = 3f
                                )
                            ),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentTab == HaditsScreenTab.TEMATIK_DERAJAT) {
                                "Pelajari sabda Nabi ﷺ dengan klasifikasi derajat sanad yang ilmiah, mudah difahami, dan dilengkapi faedah."
                            } else {
                                "Akses 7 Kitab Induk Hadits terlengkap karya Bukhari, Muslim, Abu Dawud, Tirmidzi, dll."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    offset = Offset(0f, 1f),
                                    blurRadius = 2f
                                )
                            ),
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Main Tab Switcher: [Tematik & Derajat] vs [Kumpulan Kitab]
            TabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DeepEmeraldPrimary
            ) {
                Tab(
                    selected = currentTab == HaditsScreenTab.TEMATIK_DERAJAT,
                    onClick = { currentTab = HaditsScreenTab.TEMATIK_DERAJAT },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Tematik & Derajat", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Tab(
                    selected = currentTab == HaditsScreenTab.KUMPULAN_KITAB,
                    onClick = { currentTab = HaditsScreenTab.KUMPULAN_KITAB },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Kumpulan Kitab", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Tab Body Content
            when (currentTab) {
                HaditsScreenTab.TEMATIK_DERAJAT -> {
                    TematikHaditsView(
                        ttsHelper = ttsHelper,
                        isPlaying = isPlaying,
                        currentlyPlayingId = currentlyPlayingId
                    )
                }
                HaditsScreenTab.KUMPULAN_KITAB -> {
                    KumpulanKitabHierarchicalView(
                        repository = hadithRepository,
                        ttsHelper = ttsHelper,
                        isPlaying = isPlaying,
                        currentlyPlayingId = currentlyPlayingId,
                        selectedBook = null,
                        selectedSection = null,
                        searchQuery = kumpulanKitabSearchQuery,
                        selectedFilter = kumpulanKitabSearchFilter,
                        onOpenSearchDialog = { showSearchDialog = true },
                        onClearSearch = { kumpulanKitabSearchQuery = "" },
                        onSelectBook = { book ->
                            selectedBook = book
                            selectedSection = null
                        },
                        onSelectSection = { section ->
                            selectedSection = section
                        },
                        onSelectBookAndSection = { book, section ->
                            selectedBook = book
                            selectedSection = section
                        },
                        onBackToBooks = {
                            ttsHelper.stop()
                            selectedBook = null
                            selectedSection = null
                        },
                        onBackToSections = {
                            ttsHelper.stop()
                            selectedSection = null
                        }
                    )
                }
            }
        }
    }
}

/**
 * Filter mode for Kumpulan Kitab search bar
 */
enum class KumpulanKitabSearchFilter {
    ALL,
    BOOKS,
    CHAPTERS
}

/**
 * Representasi hasil pencarian bab lintas kitab
 */
data class BookSectionSearchResult(
    val book: HadithBook,
    val section: HadithBookSection
)

/**
 * Pop-up Dialog Pencarian Kitab & Hadits (Kumpulan Kitab)
 * Muncul ketika pengguna menekan ikon kaca pembesar di bagian atas
 */
@Composable
private fun KumpulanKitabSearchPopUpDialog(
    initialQuery: String,
    initialFilter: KumpulanKitabSearchFilter,
    repository: HadithRepository,
    onApplySearch: (String, KumpulanKitabSearchFilter) -> Unit,
    onSelectBook: (HadithBook) -> Unit,
    onSelectBookAndSection: (HadithBook, HadithBookSection) -> Unit,
    onDismiss: () -> Unit
) {
    var queryInput by remember { mutableStateOf(initialQuery) }
    var filterSelection by remember { mutableStateOf(initialFilter) }
    val books = remember { repository.getAvailableBooks() }

    val allSections = remember(books) {
        books.flatMap { bItem -> bItem.sections.map { BookSectionSearchResult(bItem, it) } }
    }

    val q = queryInput.trim().lowercase()
    val matchedBooks = remember(books, q) {
        if (q.isBlank()) emptyList() else books.filter {
            it.title.lowercase().contains(q) ||
                    it.author.lowercase().contains(q) ||
                    it.description.lowercase().contains(q)
        }
    }
    val matchedSections = remember(allSections, q) {
        if (q.isBlank()) emptyList() else allSections.filter { item ->
            item.section.titleId.lowercase().contains(q) ||
                    item.section.titleEn.lowercase().contains(q) ||
                    item.section.description.lowercase().contains(q) ||
                    item.book.title.lowercase().contains(q)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = DeepEmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Pencarian Kitab & Hadits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Kolom Input Pencarian
                OutlinedTextField(
                    value = queryInput,
                    onValueChange = { queryInput = it },
                    placeholder = {
                        Text(
                            text = "Ketik judul kitab, bab, topik...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = DeepEmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (queryInput.isNotEmpty()) {
                            IconButton(onClick = { queryInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_search_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepEmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    )
                )

                // Pilihan Filter Lingkup
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterSelection == KumpulanKitabSearchFilter.ALL,
                        onClick = { filterSelection = KumpulanKitabSearchFilter.ALL },
                        label = { Text("Semua", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = filterSelection == KumpulanKitabSearchFilter.BOOKS,
                        onClick = { filterSelection = KumpulanKitabSearchFilter.BOOKS },
                        label = { Text("📚 Kitab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = filterSelection == KumpulanKitabSearchFilter.CHAPTERS,
                        onClick = { filterSelection = KumpulanKitabSearchFilter.CHAPTERS },
                        label = { Text("📖 Bab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Kata Kunci Populer / Cepat
                Text(
                    text = "Kata Kunci Populer:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickTopics = listOf("Wahyu", "Iman", "Sholat", "Zakat", "Puasa", "Adab", "Bukhari", "Muslim")
                    quickTopics.forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.6.dp, DeepEmeraldPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                queryInput = topic
                                filterSelection = KumpulanKitabSearchFilter.ALL
                            }
                        ) {
                            Text(
                                text = "#$topic",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepEmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (q.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepEmeraldPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ditemukan: ${matchedBooks.size} Kitab • ${matchedSections.size} Bab",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplySearch(queryInput, filterSelection)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (queryInput.isBlank()) "Tampilkan Semua" else "Terapkan Pencarian")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Tutup", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * Tab 2: Kumpulan Kitab Hierarki 3 Tingkat
 * Level 1: Daftar Banner Kitab (Shahih Bukhari, Muslim, dll.) dengan tombol berdenyut tersusun ke bawah
 * Level 2: Daftar Bab / Sub-Kategori dari Kitab terpilih, semua tersusun ke bawah
 * Level 3: Daftar Hadits di dalam Bab tersebut (Teks Arab, Terjemahan, Audio TTS)
 */
@Composable
private fun KumpulanKitabHierarchicalView(
    repository: HadithRepository,
    ttsHelper: IslamicTtsHelper,
    isPlaying: Boolean,
    currentlyPlayingId: String?,
    selectedBook: HadithBook?,
    selectedSection: HadithBookSection?,
    searchQuery: String,
    selectedFilter: KumpulanKitabSearchFilter,
    onOpenSearchDialog: () -> Unit,
    onClearSearch: () -> Unit,
    onSelectBook: (HadithBook) -> Unit,
    onSelectSection: (HadithBookSection) -> Unit,
    onSelectBookAndSection: (HadithBook, HadithBookSection) -> Unit,
    onBackToBooks: () -> Unit,
    onBackToSections: () -> Unit
) {
    when {
        // LEVEL 3: Menampilkan Hadits di dalam Bab Terpilih
        selectedBook != null && selectedSection != null -> {
            OnlineHadithDetailList(
                book = selectedBook,
                section = selectedSection,
                repository = repository,
                ttsHelper = ttsHelper,
                isPlaying = isPlaying,
                currentlyPlayingId = currentlyPlayingId,
                onBack = onBackToSections
            )
        }

        // LEVEL 2: Menampilkan Daftar Bab / Sub-Kategori Tersusun ke Bawah
        selectedBook != null -> {
            BookSectionsVerticalList(
                book = selectedBook,
                onSelectSection = onSelectSection,
                onBack = onBackToBooks
            )
        }

        // LEVEL 1: Menampilkan Daftar Banner Kitab (Hero & Banner Slim Terang)
        else -> {
            BooksBannerVerticalList(
                repository = repository,
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
                onOpenSearchDialog = onOpenSearchDialog,
                onClearSearch = onClearSearch,
                onSelectBook = onSelectBook,
                onSelectBookAndSection = onSelectBookAndSection
            )
        }
    }
}

/**
 * Level 1: Layar Kumpulan Kitab dengan Desain Luas & Terang
 * Kolom pencarian & baris filter permanen dihilangkan agar tempatnya lebih luas,
 * diganti dengan ikon kaca pembesar pop-up di pojok atas.
 */
@Composable
private fun BooksBannerVerticalList(
    repository: HadithRepository,
    searchQuery: String,
    selectedFilter: KumpulanKitabSearchFilter,
    onOpenSearchDialog: () -> Unit,
    onClearSearch: () -> Unit,
    onSelectBook: (HadithBook) -> Unit,
    onSelectBookAndSection: (HadithBook, HadithBookSection) -> Unit
) {
    val context = LocalContext.current
    val books = remember { repository.getAvailableBooks() }

    // Hitung seluruh bab dari semua kitab
    val allSections = remember(books) {
        books.flatMap { bItem -> bItem.sections.map { BookSectionSearchResult(bItem, it) } }
    }

    // Filter books & chapters berdasarkan query dan chip filter dari pop-up
    val (filteredBooks, filteredSections) = remember(books, allSections, searchQuery, selectedFilter) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) {
            val b = if (selectedFilter == KumpulanKitabSearchFilter.CHAPTERS) emptyList() else books
            val s = if (selectedFilter == KumpulanKitabSearchFilter.CHAPTERS) allSections else emptyList()
            Pair(b, s)
        } else {
            val b = if (selectedFilter == KumpulanKitabSearchFilter.CHAPTERS) emptyList() else {
                books.filter {
                    it.title.lowercase().contains(q) ||
                            it.author.lowercase().contains(q) ||
                            it.description.lowercase().contains(q) ||
                            it.gradeStatus.lowercase().contains(q)
                }
            }
            val s = if (selectedFilter == KumpulanKitabSearchFilter.BOOKS) emptyList() else {
                allSections.filter { item ->
                    item.section.titleId.lowercase().contains(q) ||
                            item.section.titleEn.lowercase().contains(q) ||
                            item.section.description.lowercase().contains(q) ||
                            item.section.hadithRange.lowercase().contains(q) ||
                            item.section.keyTopics.any { topic -> topic.lowercase().contains(q) } ||
                            item.book.title.lowercase().contains(q)
                }
            }
            Pair(b, s)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("kumpulan_kitab_container")
    ) {
        // ==========================================
        // SLIM INDIKATOR PENCARIAN AKTIF (JIKA ADA QUERY)
        // Kolom pencarian & baris filter permanen dihilangkan agar layar luas
        // ==========================================
        if (searchQuery.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("kumpulan_kitab_active_search_pill"),
                shape = RoundedCornerShape(12.dp),
                color = DeepEmeraldPrimary.copy(alpha = 0.08f),
                border = BorderStroke(0.8.dp, DeepEmeraldPrimary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = DeepEmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Filter: \"$searchQuery\" (${filteredBooks.size} Kitab • ${filteredSections.size} Bab)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmeraldPrimary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onOpenSearchDialog,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Ubah", fontSize = 11.sp, color = DeepEmeraldPrimary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Hapus Pencarian",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // DAFTAR HASIL / KONTEN KUMPULAN KITAB
        // ==========================================
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("kumpulan_kitab_banner_list"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 95.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (searchQuery.isNotBlank()) {
                // Info Header Hasil Pencarian
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DeepEmeraldPrimary.copy(alpha = 0.08f),
                        border = BorderStroke(0.8.dp, DeepEmeraldPrimary.copy(alpha = 0.25f))
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
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = DeepEmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Hasil: \"$searchQuery\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEmeraldPrimary
                                )
                            }
                            Text(
                                text = "${filteredBooks.size} Kitab • ${filteredSections.size} Bab",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Bagian 1: Kitab yang Cocok
                if (filteredBooks.isNotEmpty()) {
                    item {
                        Text(
                            text = "📚 Kitab Hadits Terkait (${filteredBooks.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                        )
                    }

                    items(filteredBooks, key = { "book_${it.id}" }) { book ->
                        LuxuryBookBannerCard(
                            book = book,
                            onClick = {
                                IronSourceAdManager.recordClick(context)
                                onSelectBook(book)
                            }
                        )
                    }
                }

                // Bagian 2: Bab / Sub-Kategori yang Cocok
                if (filteredSections.isNotEmpty()) {
                    item {
                        Text(
                            text = "📖 Pilihan Bab & Pembahasan Terkait (${filteredSections.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                        )
                    }

                    items(filteredSections, key = { "sec_${it.book.id}_${it.section.sectionNumber}" }) { result ->
                        SearchedSectionCard(
                            result = result,
                            onClick = {
                                IronSourceAdManager.recordClick(context)
                                onSelectBookAndSection(result.book, result.section)
                            }
                        )
                    }
                }

                // Bagian 3: Keadaan Kosong (Tidak Ditemukan Hasil)
                if (filteredBooks.isEmpty() && filteredSections.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                                .testTag("kumpulan_kitab_empty_search"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Tidak Ditemukan Hasil",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tidak ada kitab atau bab yang cocok dengan kata kunci \"$searchQuery\". Coba cari topik lain seperti: Iman, Sholat, Wahyu, Zakat, atau Puasa.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        onClearSearch()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DeepEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("btn_reset_search")
                                ) {
                                    Text("Reset Pencarian")
                                }
                            }
                        }
                    }
                }
            } else {
                // MODE NORMAL (Belum Mengetik Pencarian)
                if (selectedFilter == KumpulanKitabSearchFilter.CHAPTERS) {
                    // Tampilkan seluruh bab dari semua kitab
                    item {
                        Text(
                            text = "📖 Telusuri Seluruh Bab Hadits (${allSections.size} Bab)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                        )
                    }

                    items(allSections, key = { "all_sec_${it.book.id}_${it.section.sectionNumber}" }) { result ->
                        SearchedSectionCard(
                            result = result,
                            onClick = {
                                IronSourceAdManager.recordClick(context)
                                onSelectBookAndSection(result.book, result.section)
                            }
                        )
                    }
                } else {
                    // Judul Bagian Kitab Induk
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Kitab Induk (${filteredBooks.size} Kitab)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pilih untuk buka bab",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // List of Book Banners with Pulsing 3D Buttons
                    items(filteredBooks, key = { it.id }) { book ->
                        LuxuryBookBannerCard(
                            book = book,
                            onClick = {
                                IronSourceAdManager.recordClick(context)
                                onSelectBook(book)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kartu Khusus untuk Menampilkan Hasil Pencarian Bab / Sub-Kategori
 * Dilengkapi informasi kitab sumber, nomor bab, rentang hadits, dan tombol langsung buka
 */
@Composable
private fun SearchedSectionCard(
    result: BookSectionSearchResult,
    onClick: () -> Unit
) {
    val book = result.book
    val section = result.section
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp)
            .clickable(onClick = onClick)
            .testTag("search_section_item_${book.id}_${section.sectionNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Baris Atas: Label Kitab Asal & Rentang Hadits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(book.badgeColorHex).copy(alpha = 0.14f),
                    border = BorderStroke(0.8.dp, Color(book.badgeColorHex).copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "📚 ${book.title}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(book.badgeColorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (section.hadithRange.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = section.hadithRange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Judul Bab Indonesia
            Text(
                text = section.titleId,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (section.titleEn.isNotBlank()) {
                Text(
                    text = section.titleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            if (section.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tombol Langsung Buka Bab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEmeraldPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_open_search_section_${book.id}_${section.sectionNumber}")
                ) {
                    Text("Buka Bab Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Kartu Banner Kitab Slim & Terang dengan Tombol Berdenyut (Game 3D Pulse)
 * Menampilkan nama kitab dengan foto cerah Makkah / Madinah dan kartu yang slim & enak dipandang
 */
@Composable
private fun LuxuryBookBannerCard(
    book: HadithBook,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .clickable { onClick() }
            .testTag("banner_book_${book.id}"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(book.badgeColorHex).copy(alpha = 0.55f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
        ) {
            // Background Image Terang (Makkah, Ka'bah, Nabawi, Masjidil Haram)
            if (book.imageRes != 0) {
                Image(
                    painter = painterResource(id = book.imageRes),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Luminous Scrim Transparan Terang agar pemandangan cerah tetap terpancar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0x08000000),
                            0.35f to Color(0x30000000),
                            1.0f to Color(0xD0061810)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Baris Atas: Badge Status Derajat & Jumlah Bab / Hadits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(book.badgeColorHex).copy(alpha = 0.88f),
                        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = book.gradeStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xCC000000),
                        border = BorderStroke(0.6.dp, DeepGoldAccent.copy(alpha = 0.8f))
                    ) {
                        Text(
                            text = "${book.sections.size} Bab • ${book.totalHadithEstimate}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE082),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                // Baris Tengah: Judul Kitab & Nama Imam Penyusun
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.85f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f
                            )
                        ),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = book.author,
                        fontSize = 11.sp,
                        color = Color(0xFFFFE082),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Baris Bawah: Tombol Berdenyut Slim (Pulsing 3D Capsule Button)
                PulsingGameButton(
                    label = "✨ ${book.title} (Buka Pilihan Bab)",
                    onClick = onClick,
                    testTag = "btn_open_book_${book.id}"
                )
            }
        }
    }
}

/**
 * Tombol 3D Berdenyut Slim (Game-Style Pulsing Capsule Button)
 * Berdenyut ritmis dengan ukuran slim elegan agar nyaman dipandang
 */
@Composable
private fun PulsingGameButton(
    label: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = Color(0xFFFFB300)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFFFB300),
                        Color(0xFFF57F17),
                        Color(0xFFFFB300)
                    )
                )
            )
            .border(
                1.2.dp,
                Color(0xFFFFF9C4).copy(alpha = glowAlpha),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = Color(0xFF2E1700),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF2E1700),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Level 2: Daftar Bab / Sub-Kategori Tersusun ke Bawah (Vertical List)
 */
@Composable
private fun BookSectionsVerticalList(
    book: HadithBook,
    onSelectSection: (HadithBookSection) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchSectionQuery by remember { mutableStateOf("") }

    val filteredSections = remember(book.sections, searchSectionQuery) {
        if (searchSectionQuery.isBlank()) {
            book.sections
        } else {
            val q = searchSectionQuery.trim().lowercase()
            book.sections.filter {
                it.titleId.lowercase().contains(q) ||
                        it.titleEn.lowercase().contains(q) ||
                        it.description.lowercase().contains(q) ||
                        it.hadithRange.lowercase().contains(q) ||
                        it.sectionNumber.toString().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("book_sections_vertical_view")
    ) {
        // Top Navigation Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
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
                    modifier = Modifier.testTag("btn_back_to_books")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Daftar Kitab",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(book.badgeColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = book.gradeStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(book.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Pilih Bab / Pembahasan (${book.sections.size} Bab Pilihan)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 95.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Bar for Sections
            item {
                OutlinedTextField(
                    value = searchSectionQuery,
                    onValueChange = { searchSectionQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "Cari nama bab atau tema di ${book.title}…",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DeepEmeraldPrimary)
                    },
                    trailingIcon = {
                        if (searchSectionQuery.isNotEmpty()) {
                            IconButton(onClick = { searchSectionQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepEmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Book Overview Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "📖", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "${book.author} • ${book.totalHadithEstimate}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = book.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Section Cards Arranged Vertically Downward
            items(filteredSections, key = { it.sectionNumber }) { section ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            IronSourceAdManager.recordClick(context)
                            onSelectSection(section)
                        }
                        .testTag("section_card_${section.sectionNumber}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.2.dp, DeepEmeraldPrimary.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header Bar: Bab Pill & Rentang Hadits
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DeepEmeraldPrimary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, DeepEmeraldPrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "BAB ${section.sectionNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepEmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (section.hadithRange.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepGoldAccent.copy(alpha = 0.18f),
                                    border = BorderStroke(0.8.dp, DeepGoldAccent.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = section.hadithRange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepGoldAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Judul Utama Bab & Tema Inggris/Asli
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = section.titleId,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            if (section.titleEn.isNotEmpty()) {
                                Text(
                                    text = "Tema: ${section.titleEn}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Keterangan / Deskripsi Bab
                        if (section.description.isNotEmpty()) {
                            Text(
                                text = section.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }

                        // Key Topics Tags jika tersedia
                        if (section.keyTopics.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                section.keyTopics.forEach { topic ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = topic,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Tombol Aksi Buka Bab
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = DeepEmeraldPrimary.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, DeepEmeraldPrimary.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Buka Hadits Bab Ini",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEmeraldPrimary
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Buka Bab",
                                    tint = DeepEmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
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
 * Level 3: Daftar Hadits di dalam Bab Terpilih
 */
@Composable
private fun OnlineHadithDetailList(
    book: HadithBook,
    section: HadithBookSection,
    repository: HadithRepository,
    ttsHelper: IslamicTtsHelper,
    isPlaying: Boolean,
    currentlyPlayingId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fetchedHadiths by remember { mutableStateOf<List<HadithParsedItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val loadSectionData: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.fetchSectionHadiths(book.id, section.sectionNumber)
            result.onSuccess { data ->
                fetchedHadiths = data
                isLoading = false
            }.onFailure { err ->
                errorMessage = err.localizedMessage ?: "Gagal mengunduh hadits dari server API. Periksa koneksi internet Anda."
                isLoading = false
            }
        }
    }

    LaunchedEffect(book.id, section.sectionNumber) {
        loadSectionData()
    }

    val displayHadiths = remember(fetchedHadiths, searchQuery) {
        if (searchQuery.isBlank()) {
            fetchedHadiths
        } else {
            val q = searchQuery.trim().lowercase()
            fetchedHadiths.filter {
                it.number.toString().contains(q) ||
                        it.arabicText.contains(q) ||
                        it.indonesianText.lowercase().contains(q)
            }
        }
    }

    val copyOnlineItem: (HadithParsedItem) -> Unit = { item ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            appendLine("📜 ${item.bookName} - Hadits No. ${item.number}")
            appendLine("Bab: ${section.titleId}")
            appendLine()
            appendLine(item.arabicText)
            appendLine()
            appendLine("Terjemahan Indonesia:")
            appendLine("\"${item.indonesianText}\"")
            appendLine()
            append("Sumber: ${book.title}")
        }
        val clip = ClipData.newPlainText("Hadits #${item.number}", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "📋 Hadits #${item.number} berhasil disalin ke papan klip", Toast.LENGTH_SHORT).show()
    }

    val shareOnlineItem: (HadithParsedItem) -> Unit = { item ->
        val text = buildString {
            appendLine("📜 ${item.bookName} - Hadits No. ${item.number}")
            appendLine("Bab: ${section.titleId}")
            appendLine()
            appendLine(item.arabicText)
            appendLine()
            appendLine("Terjemahan:")
            appendLine("\"${item.indonesianText}\"")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Hadits"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("online_hadith_detail_screen")
    ) {
        // Top Navigation Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
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
                    modifier = Modifier.testTag("btn_back_to_sections")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Daftar Bab",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.titleId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${book.title} • ${section.hadithRange}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = loadSectionData,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Muat Ulang Hadits",
                        tint = DeepEmeraldPrimary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 95.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "Cari nomor hadits atau kata kunci terjemahan…",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DeepEmeraldPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepEmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Copy Hint Banner
            if (!isLoading && errorMessage == null && displayHadiths.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = DeepEmeraldPrimary.copy(alpha = 0.08f),
                        border = BorderStroke(0.6.dp, DeepEmeraldPrimary.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💡", fontSize = 13.sp)
                            Text(
                                text = "Sentuh tombol 'Salin' atau tekan lama kartu hadits untuk menyalin teks.",
                                fontSize = 11.sp,
                                color = DeepEmeraldPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Loading State
            if (isLoading) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = DeepEmeraldPrimary, modifier = Modifier.size(40.dp))
                            Text(
                                text = "Mengunduh Hadits dari Server…",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sedang mengambil teks Arab & terjemahan Indonesia untuk ${section.titleId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📡", fontSize = 32.sp)
                            Text(
                                text = "Koneksi Terkendala",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = loadSectionData,
                                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            } else if (displayHadiths.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔍", fontSize = 32.sp)
                            Text(
                                text = "Tidak Ada Hadits Ditemukan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tidak ada hadits yang cocok dengan kata kunci pencarian pada bab ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Render List of Hadiths
                items(displayHadiths, key = { "${it.bookId}-${it.number}" }) { item ->
                    val isPlayingThis = isPlaying && currentlyPlayingId == "online_${item.bookId}_${item.number}"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("online_hadith_item_${item.number}")
                            .pointerInput(item.number) {
                                detectTapGestures(
                                    onLongPress = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        copyOnlineItem(item)
                                    }
                                )
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, DeepEmeraldPrimary.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#${item.number}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${item.bookName} • No. ${item.number}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DeepEmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = item.gradeTag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Arabic Box Slim
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(0.6.dp, DeepEmeraldPrimary.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = item.arabicText,
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Indonesian Translation Slim
                            Text(
                                text = item.indonesianText,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Action Toolbar Row Slim
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        IronSourceAdManager.recordClick(context)
                                        ttsHelper.speakRecitation(
                                            id = "online_${item.bookId}_${item.number}",
                                            arabicText = item.arabicText,
                                            fallbackLatinText = item.indonesianText
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPlayingThis) Color(0xFFD32F2F) else DeepEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPlayingThis) "Hentikan" else "Dengarkan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Dedicated Copy Button Slim
                                    FilledTonalButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            copyOnlineItem(item)
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = DeepEmeraldPrimary.copy(alpha = 0.12f),
                                            contentColor = DeepEmeraldPrimary
                                        ),
                                        shape = RoundedCornerShape(18.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                                        modifier = Modifier.testTag("btn_copy_online_hadith_${item.number}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Salin Hadits",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Salin",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(
                                        onClick = { shareOnlineItem(item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Bagikan Hadits",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Koleksi Tematik Lokal & Derajat Sanad (Offline-First)
 */
@Composable
private fun TematikHaditsView(
    ttsHelper: IslamicTtsHelper,
    isPlaying: Boolean,
    currentlyPlayingId: String?
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedGradeFilter by remember { mutableStateOf<HaditsGrade?>(null) }
    var selectedTopicFilter by remember { mutableStateOf(HaditsTopic.SEMUA) }
    var showBookmarksOnly by remember { mutableStateOf(false) }

    val bookmarkedIds = remember { mutableStateListOf<String>() }

    val allHadits = remember { HaditsData.items }
    val filteredHadits = remember(searchQuery, selectedGradeFilter, selectedTopicFilter, showBookmarksOnly, bookmarkedIds.size) {
        allHadits.filter { item ->
            val matchGrade = selectedGradeFilter == null || item.grade == selectedGradeFilter
            val matchTopic = selectedTopicFilter == HaditsTopic.SEMUA || item.topic == selectedTopicFilter
            val matchBookmark = !showBookmarksOnly || bookmarkedIds.contains(item.id)
            val matchSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                        item.arabic.contains(q) ||
                        item.latin.lowercase().contains(q) ||
                        item.translation.lowercase().contains(q) ||
                        item.narrator.lowercase().contains(q) ||
                        item.takhrijBook.lowercase().contains(q) ||
                        item.faedah.lowercase().contains(q)
            }
            matchGrade && matchTopic && matchBookmark && matchSearch
        }
    }

    val shahihCount = remember { allHadits.count { it.grade == HaditsGrade.SHAHIH } }
    val hasanCount = remember { allHadits.count { it.grade == HaditsGrade.HASAN } }
    val dhaifCount = remember { allHadits.count { it.grade == HaditsGrade.DHAIF } }
    val populerCount = remember { allHadits.count { it.grade == HaditsGrade.POPULER_KLARIFIKASI } }

    val copyToClipboard: (HaditsItem) -> Unit = { hadits ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            appendLine("📜 ${hadits.title} [${hadits.grade.displayName}]")
            appendLine("Perawi: ${hadits.narrator}")
            appendLine()
            appendLine(hadits.arabic)
            appendLine()
            appendLine("Artinya:")
            appendLine("\"${hadits.translation}\"")
            appendLine()
            if (hadits.clarification != null) {
                appendLine("Catatan Penting:")
                appendLine(hadits.clarification)
                appendLine()
            }
            appendLine("Sumber: ${hadits.takhrijBook}")
            append("Dibagikan melalui Kompas Kiblat & Katalog Hadits")
        }
        val clip = ClipData.newPlainText(hadits.title, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "📋 Hadits berhasil disalin ke papan klip", Toast.LENGTH_SHORT).show()
    }

    val shareHadits: (HaditsItem) -> Unit = { hadits ->
        val text = buildString {
            appendLine("📜 ${hadits.title} [${hadits.grade.displayName}]")
            appendLine("Perawi: ${hadits.narrator}")
            appendLine()
            appendLine(hadits.arabic)
            appendLine()
            appendLine("Artinya:")
            appendLine("\"${hadits.translation}\"")
            appendLine()
            if (hadits.clarification != null) {
                appendLine("Klarifikasi / Catatan:")
                appendLine(hadits.clarification)
                appendLine()
            }
            appendLine("Pelajaran: ${hadits.faedah}")
            appendLine()
            append("Sumber: ${hadits.takhrijBook}")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Hadits")
        context.startActivity(shareIntent)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("hadits_catalog_list"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 95.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Bar & Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hadits_search_field"),
                    placeholder = {
                        Text(
                            "Cari judul, kata kunci, perawi, atau terjemahan…",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari Hadits",
                            tint = DeepEmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus Pencarian",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepEmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Filter Status & Derajat:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedGradeFilter == null && !showBookmarksOnly,
                        onClick = {
                            selectedGradeFilter = null
                            showBookmarksOnly = false
                        },
                        label = { Text("Semua (${allHadits.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    FilterChip(
                        selected = selectedGradeFilter == HaditsGrade.SHAHIH && !showBookmarksOnly,
                        onClick = {
                            selectedGradeFilter = if (selectedGradeFilter == HaditsGrade.SHAHIH) null else HaditsGrade.SHAHIH
                            showBookmarksOnly = false
                        },
                        label = { Text("🟢 Shahih ($shahihCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE8F7F0),
                            selectedLabelColor = Color(0xFF1B8A5A)
                        )
                    )

                    FilterChip(
                        selected = selectedGradeFilter == HaditsGrade.HASAN && !showBookmarksOnly,
                        onClick = {
                            selectedGradeFilter = if (selectedGradeFilter == HaditsGrade.HASAN) null else HaditsGrade.HASAN
                            showBookmarksOnly = false
                        },
                        label = { Text("🟡 Hasan ($hasanCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFF7E6),
                            selectedLabelColor = Color(0xFFC07000)
                        )
                    )

                    FilterChip(
                        selected = selectedGradeFilter == HaditsGrade.DHAIF && !showBookmarksOnly,
                        onClick = {
                            selectedGradeFilter = if (selectedGradeFilter == HaditsGrade.DHAIF) null else HaditsGrade.DHAIF
                            showBookmarksOnly = false
                        },
                        label = { Text("🟠 Dha'if ($dhaifCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFEBEE),
                            selectedLabelColor = Color(0xFFD32F2F)
                        )
                    )

                    FilterChip(
                        selected = selectedGradeFilter == HaditsGrade.POPULER_KLARIFIKASI && !showBookmarksOnly,
                        onClick = {
                            selectedGradeFilter = if (selectedGradeFilter == HaditsGrade.POPULER_KLARIFIKASI) null else HaditsGrade.POPULER_KLARIFIKASI
                            showBookmarksOnly = false
                        },
                        label = { Text("🔵 Klarifikasi ($populerCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE3F2FD),
                            selectedLabelColor = Color(0xFF1976D2)
                        )
                    )

                    FilterChip(
                        selected = showBookmarksOnly,
                        onClick = {
                            showBookmarksOnly = !showBookmarksOnly
                            if (showBookmarksOnly) selectedGradeFilter = null
                        },
                        label = { Text("⭐ Tersimpan (${bookmarkedIds.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepGoldAccent.copy(alpha = 0.2f),
                            selectedLabelColor = DeepGoldAccent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tema & Pembahasan:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HaditsTopic.values().forEach { topic ->
                        FilterChip(
                            selected = selectedTopicFilter == topic,
                            onClick = { selectedTopicFilter = topic },
                            leadingIcon = { Text(topic.iconSymbol, fontSize = 12.sp) },
                            label = { Text(topic.title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepEmeraldPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = DeepEmeraldPrimary
                            )
                        )
                    }
                }
            }
        }

        // Grade Info Card
        if (selectedGradeFilter != null) {
            item {
                val grade = selectedGradeFilter!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = grade.containerColor),
                    border = BorderStroke(1.dp, grade.primaryColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "💡", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "Karakteristik Hadits ${grade.displayName}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = grade.primaryColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = grade.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Result Count Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menampilkan ${filteredHadits.size} dari ${allHadits.size} Hadits",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                if (searchQuery.isNotEmpty() || selectedGradeFilter != null || selectedTopicFilter != HaditsTopic.SEMUA || showBookmarksOnly) {
                    Text(
                        text = "Reset Filter",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepEmeraldPrimary,
                        modifier = Modifier
                            .clickable {
                                searchQuery = ""
                                selectedGradeFilter = null
                                selectedTopicFilter = HaditsTopic.SEMUA
                                showBookmarksOnly = false
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        // List Items
        if (filteredHadits.isEmpty()) {
            item {
                EmptyHaditsPlaceholder {
                    searchQuery = ""
                    selectedGradeFilter = null
                    selectedTopicFilter = HaditsTopic.SEMUA
                    showBookmarksOnly = false
                }
            }
        } else {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = DeepEmeraldPrimary.copy(alpha = 0.08f),
                    border = BorderStroke(0.6.dp, DeepEmeraldPrimary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💡", fontSize = 13.sp)
                        Text(
                            text = "Sentuh tombol 'Salin' atau tekan lama kartu hadits untuk menyalin teks lengkap.",
                            fontSize = 11.sp,
                            color = DeepEmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(filteredHadits, key = { it.id }) { hadits ->
                val isPlayingThis = isPlaying && currentlyPlayingId == hadits.id
                val isBookmarked = bookmarkedIds.contains(hadits.id)

                HaditsItemCard(
                    hadits = hadits,
                    isPlaying = isPlayingThis,
                    isBookmarked = isBookmarked,
                    onTogglePlayAudio = {
                        IronSourceAdManager.recordClick(context)
                        ttsHelper.speakRecitation(hadits.id, hadits.arabic, hadits.latin)
                    },
                    onToggleBookmark = {
                        if (isBookmarked) {
                            bookmarkedIds.remove(hadits.id)
                            Toast.makeText(context, "Dihapus dari simpanan", Toast.LENGTH_SHORT).show()
                        } else {
                            bookmarkedIds.add(hadits.id)
                            Toast.makeText(context, "Disimpan ke favorit", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCopy = { copyToClipboard(hadits) },
                    onShare = { shareHadits(hadits) }
                )
            }
        }
    }
}

@Composable
private fun EmptyHaditsPlaceholder(
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔍", fontSize = 36.sp)
            Text(
                text = "Tidak Ada Hadits yang Sesuai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Coba ubah kata kunci pencarian atau bersihkan filter status derajat/tema di atas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary)
            ) {
                Text("Tampilkan Semua Hadits")
            }
        }
    }
}

@Composable
private fun HaditsItemCard(
    hadits: HaditsItem,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onTogglePlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize()
            .testTag("hadits_card_${hadits.id}")
            .pointerInput(hadits.id) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCopy()
                    }
                )
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, hadits.grade.primaryColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: # Number, Topic & Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${hadits.number}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = hadits.grade.containerColor,
                        border = BorderStroke(0.8.dp, hadits.grade.primaryColor)
                    ) {
                        Text(
                            text = hadits.grade.badgeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = hadits.grade.primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = hadits.topic.title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Simpan Hadits",
                        tint = if (isBookmarked) DeepGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = hadits.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Narrator Tag
            Text(
                text = hadits.narrator,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = hadits.grade.containerColor.copy(alpha = 0.5f),
                border = BorderStroke(0.6.dp, hadits.grade.primaryColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = hadits.arabic,
                    fontSize = 20.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Latin Transliteration
            Text(
                text = hadits.latin,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Serif,
                color = DeepEmeraldPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Indonesian Translation
            Text(
                text = "\"${hadits.translation}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Clarification Warning Box for Dha'if or Populer
            if (hadits.clarification != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (hadits.grade == HaditsGrade.DHAIF) Color(0xFFFFF3E0) else Color(0xFFE8EAF6),
                    border = BorderStroke(1.dp, if (hadits.grade == HaditsGrade.DHAIF) Color(0xFFFF9800) else Color(0xFF3F51B5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 14.sp)
                            Text(
                                text = if (hadits.grade == HaditsGrade.DHAIF) "Edukasi & Hadits Shahih Pengganti" else "Klarifikasi Derajat Riwayat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hadits.grade == HaditsGrade.DHAIF) Color(0xFFE65100) else Color(0xFF1A237E)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hadits.clarification,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Expandable section for Sanad Details & Faedah
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Text(
                            text = "🔍 Status Sanad & Takhrij:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = hadits.grade.primaryColor
                        )
                        Text(
                            text = hadits.gradeExplanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = "📚 Kitab Sumber:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = hadits.takhrijBook,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "💎 Pelajaran & Faedah Pengamalan:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AlignedEmerald
                        )
                        Text(
                            text = hadits.faedah,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Toolbar Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onTogglePlayAudio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color(0xFFD32F2F) else DeepEmeraldPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isPlaying) "Hentikan Suara" else "Putar Suara Hadits",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Hentikan" else "Dengarkan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dedicated Copy Button
                    FilledTonalButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCopy()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = DeepEmeraldPrimary.copy(alpha = 0.12f),
                            contentColor = DeepEmeraldPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_copy_hadits_${hadits.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Teks Hadits",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Salin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Hadits",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Tutup" else "Syarah",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmeraldPrimary
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Tutup Rincian" else "Buka Rincian",
                            tint = DeepEmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog Edukatif Penjelasan Lengkap Derajat & Metodologi Hadits
 */
@Composable
private fun HaditsKnowledgeDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📖", fontSize = 20.sp)
                Text(
                    text = "Panduan Memahami Derajat Hadits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Dalam ilmu Musthalah Al-Hadits, keabsahan sebuah riwayat dinilai berdasarkan ketersambungan sanad dan kualitas perawinya:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 1. Shahih
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F7F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "🟢 Hadits Shahih",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B8A5A)
                        )
                        Text(
                            text = "Sanad bersambung tanpa cacat, semua perawi adil dan sangat kuat hafalannya (dhabith). Wajib menjadi rujukan hukum dan akidah.",
                            fontSize = 11.sp,
                            color = Color(0xFF104A30)
                        )
                    }
                }

                // 2. Hasan
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF7E6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "🟡 Hadits Hasan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC07000)
                        )
                        Text(
                            text = "Kriteria mendekati shahih namun ketelitian perawinya sedikit di bawahnya. Sah dijadikan hujjah hukum dan amalan.",
                            fontSize = 11.sp,
                            color = Color(0xFF6B3E00)
                        )
                    }
                }

                // 3. Dha'if
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "🟠 Hadits Dha'if (Lemah)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "Tidak memenuhi syarat shahih/hasan karena hafalan perawi lemah atau sanad terputus. Ulama membolehkan untuk fadhailul a'mal dengan syarat tidak terlalu lemah dan tidak disandarkan mutlak pada Nabi ﷺ.",
                            fontSize = 11.sp,
                            color = Color(0xFF6E1010)
                        )
                    }
                }

                // 4. Populer & Klarifikasi
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "🔵 Hadits Populer & Klarifikasi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                        Text(
                            text = "Ungkapan yang sangat terkenal di masyarakat tetapi ternyata bukan sabda Nabi ﷺ (melainkan atsar sahabat atau ucapan hikmah). Aplikasi ini meluruskan sumber aslinya.",
                            fontSize = 11.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary)
            ) {
                Text("Saya Mengerti")
            }
        }
    )
}
