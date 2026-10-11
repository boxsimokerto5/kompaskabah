package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.IronSourceAdManager
import com.example.data.CalendarNoteItem
import com.example.data.CalendarNotesRepository
import com.example.data.SoftBrushPalette
import com.example.domain.calendar.FastingType
import com.example.domain.calendar.IslamicCalendarDay
import com.example.domain.calendar.IslamicCalendarHelper
import com.example.ui.QiblaUiState
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.AlignedEmeraldGlow
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.DeepEmeraldPrimary
import com.example.ui.theme.DeepGoldAccent
import com.example.ui.theme.MeccaGold
import java.util.Calendar

/**
 * Dedicated Islamic Calendar (Kalender Hijriyah & Masehi) screen.
 * Features:
 * 1. Full-bleed daylight Mecca / Kaaba under vibrant blue sky with smooth bottom vertical gradient.
 * 2. Today's live Gregorian & Hijri date card with fast action pill buttons.
 * 3. Month & Year navigation card with dynamic Hijri month span.
 * 4. 7-column day headers: MIN (Red), SEN, SEL, RAB, KAM, JUM (Green Sayyidul Ayyam), SAB (Blue).
 * 5. Dual-date grid (Gregorian large + Hijri date small below).
 * 6. Transparent Watercolor Brush Stroke effect with different soft pastel colors for dates with notes.
 * 7. Interactive Notes Panel below the calendar with Add/Edit note and soft brush color palette selector.
 * 8. Day detail modal sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicCalendarScreen(
    uiState: QiblaUiState,
    onToggleHaptic: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val pageBackgroundColor = MaterialTheme.colorScheme.background

    // Notes repository
    val notesRepo = remember { CalendarNotesRepository.getInstance(context) }
    val notesMap by notesRepo.notesMap.collectAsState()

    // Get current live system date
    val liveCal = remember { Calendar.getInstance() }
    val todayYear = remember { liveCal.get(Calendar.YEAR) }
    val todayMonth = remember { liveCal.get(Calendar.MONTH) + 1 } // 1 - 12
    val todayDay = remember { liveCal.get(Calendar.DAY_OF_MONTH) }
    val todayDow = remember { liveCal.get(Calendar.DAY_OF_WEEK) }

    // Selected Month & Year for calendar navigation
    var currentYear by rememberSaveable { mutableStateOf(todayYear) }
    var currentMonth by rememberSaveable { mutableStateOf(todayMonth) } // 1 - 12

    // Compute month calendar grid
    val daysInGrid = remember(currentYear, currentMonth, todayYear, todayMonth, todayDay) {
        IslamicCalendarHelper.getMonthCalendarGrid(
            year = currentYear,
            month = currentMonth,
            todayYear = todayYear,
            todayMonth = todayMonth,
            todayDay = todayDay
        )
    }

    // Selected Day for Notes Panel & Interaction
    var selectedDay by remember(daysInGrid) {
        mutableStateOf(daysInGrid.find { it.isToday } ?: daysInGrid.firstOrNull { it.isCurrentMonth } ?: daysInGrid[0])
    }

    // Modal Bottom Sheet for Day Details
    var showDayDetailSheet by remember { mutableStateOf(false) }

    // Dialog state for Adding / Editing Note
    var noteToEdit by remember { mutableStateOf<CalendarNoteItem?>(null) }
    var showAddEditNoteDialog by remember { mutableStateOf(false) }

    // Hijri Month Range string
    val hijriRangeString = remember(daysInGrid) {
        IslamicCalendarHelper.getHijriMonthRangeString(daysInGrid)
    }

    // Today's Hijri Date
    val todayHijri = remember(todayYear, todayMonth, todayDay) {
        IslamicCalendarHelper.gregorianToHijri(todayYear, todayMonth, todayDay)
    }

    val todayGregorianFormatted = remember(todayDow, todayDay, todayMonth, todayYear) {
        val dayName = IslamicCalendarHelper.DAY_NAMES_ID[todayDow - 1]
        val monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[todayMonth - 1]
        "$dayName, $todayDay $monthName $todayYear"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Full-Bleed Daylight Mecca Kaaba with Blue Sky Hero Banner & Seamless Bottom Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(265.dp)
                .testTag("islamic_calendar_hero_banner")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_kaaba_daylight_1791628182842),
                contentDescription = "Ka'bah Masjidil Haram dengan Langit Biru",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            )

            // Top Sky Scrim for readability of status bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF041E32).copy(alpha = 0.50f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Smooth bottom gradient fade blending Mecca photo seamlessly into page background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                pageBackgroundColor.copy(alpha = 0.80f),
                                pageBackgroundColor
                            )
                        )
                    )
            )

            // Foreground Content inside Hero Banner
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row: Title & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location / Today pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.90f))
                            .clickable {
                                IronSourceAdManager.recordClick(context)
                                currentYear = todayYear
                                currentMonth = todayMonth
                                daysInGrid.find { it.isToday }?.let { selectedDay = it }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("calendar_today_chip")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Hari Ini",
                            tint = DeepEmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hari Ini",
                            style = MaterialTheme.typography.titleSmall,
                            color = CharcoalText,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                                    tint = if (uiState.hapticFeedbackEnabled) DeepEmeraldPrimary else CharcoalText.copy(alpha = 0.38f),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = onOpenSettings,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.88f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Kalender",
                                    tint = DeepEmeraldPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Header Info inside Hero
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "PENANGGALAN ISLAM & HIJRIYAH",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepGoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayGregorianFormatted,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✨ ${todayHijri.formattedString}",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AlignedEmeraldGlow.copy(alpha = 0.18f),
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = AlignedEmerald.copy(alpha = 0.40f),
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Text(
                                text = todayHijri.monthNameArabic,
                                style = MaterialTheme.typography.labelMedium,
                                color = AlignedEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Action Buttons like screenshot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = DeepEmeraldPrimary.copy(alpha = 0.88f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .clickable {
                                    IronSourceAdManager.recordClick(context)
                                    val ayyamulBidhDay = daysInGrid.find { it.isCurrentMonth && it.hijriDate.day == 13 }
                                    if (ayyamulBidhDay != null) {
                                        selectedDay = ayyamulBidhDay
                                        showDayDetailSheet = true
                                    }
                                }
                                .shadow(2.dp, RoundedCornerShape(50))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nightlight,
                                    contentDescription = null,
                                    tint = MeccaGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Jadwal Puasa Sunnah",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.92f),
                            contentColor = CharcoalText,
                            modifier = Modifier
                                .border(1.dp, DeepGoldAccent.copy(alpha = 0.45f), RoundedCornerShape(50))
                                .clickable {
                                    IronSourceAdManager.recordClick(context)
                                    val eventDay = daysInGrid.find { it.isCurrentMonth && it.islamicEvent != null }
                                    if (eventDay != null) {
                                        selectedDay = eventDay
                                        showDayDetailSheet = true
                                    }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = DeepGoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hari Besar Islam",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Calendar Body (Month Selector + Day Header Pills + 7-Column Grid)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Month & Year Selector Card
            MonthYearNavigatorCard(
                year = currentYear,
                month = currentMonth,
                hijriRange = hijriRangeString,
                onPrevYear = {
                    IronSourceAdManager.recordClick(context)
                    currentYear--
                },
                onPrevMonth = {
                    IronSourceAdManager.recordClick(context)
                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear--
                    } else {
                        currentMonth--
                    }
                },
                onNextMonth = {
                    IronSourceAdManager.recordClick(context)
                    if (currentMonth == 12) {
                        currentMonth = 1
                        currentYear++
                    } else {
                        currentMonth++
                    }
                },
                onNextYear = {
                    IronSourceAdManager.recordClick(context)
                    currentYear++
                }
            )

            // Day Header Pills (7 columns: MIN, SEN, SEL, RAB, KAM, JUM, SAB)
            DayHeaderPillsRow()

            // 7-Column Calendar Grid with Transparent Watercolor Brush Stroke effect for days with notes
            CalendarGridWithBrushStrokes(
                days = daysInGrid,
                notesMap = notesMap,
                selectedDay = selectedDay,
                onDayClick = { day ->
                    IronSourceAdManager.recordClick(context)
                    selectedDay = day
                },
                onDayLongClick = { day ->
                    IronSourceAdManager.recordClick(context)
                    selectedDay = day
                    showDayDetailSheet = true
                }
            )

            // Dynamic Notes Panel Below Calendar
            NotesPanelSection(
                selectedDay = selectedDay,
                notes = notesMap[selectedDay.dateKey] ?: emptyList(),
                onAddNoteClick = {
                    IronSourceAdManager.recordClick(context)
                    noteToEdit = null
                    showAddEditNoteDialog = true
                },
                onEditNoteClick = { note ->
                    IronSourceAdManager.recordClick(context)
                    noteToEdit = note
                    showAddEditNoteDialog = true
                },
                onDeleteNoteClick = { note ->
                    IronSourceAdManager.recordClick(context)
                    notesRepo.deleteNote(note.id, note.dateKey)
                },
                onViewDayDetail = {
                    IronSourceAdManager.recordClick(context)
                    showDayDetailSheet = true
                }
            )

            // Legend / Information Card
            CalendarLegendCard()

            // Important Events and Sunnah Fasting Summary for this Month
            IslamicEventsSummaryCard(
                monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[currentMonth - 1],
                year = currentYear,
                days = daysInGrid,
                onDayClick = { day ->
                    IronSourceAdManager.recordClick(context)
                    selectedDay = day
                    showDayDetailSheet = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Bottom Sheet for Day Detail Info
    if (showDayDetailSheet) {
        DayDetailBottomSheet(
            day = selectedDay,
            notes = notesMap[selectedDay.dateKey] ?: emptyList(),
            onAddNote = {
                showDayDetailSheet = false
                noteToEdit = null
                showAddEditNoteDialog = true
            },
            onDismiss = { showDayDetailSheet = false }
        )
    }

    // Add / Edit Note Dialog with Watercolor Brush Color Palette Picker
    if (showAddEditNoteDialog) {
        AddEditNoteDialog(
            selectedDay = selectedDay,
            existingNote = noteToEdit,
            onDismiss = { showAddEditNoteDialog = false },
            onSave = { text, colorIndex ->
                IronSourceAdManager.recordClick(context)
                notesRepo.addOrUpdateNote(
                    dateKey = selectedDay.dateKey,
                    text = text,
                    colorIndex = colorIndex,
                    noteId = noteToEdit?.id
                )
                showAddEditNoteDialog = false
            },
            onDelete = {
                noteToEdit?.let {
                    IronSourceAdManager.recordClick(context)
                    notesRepo.deleteNote(it.id, it.dateKey)
                }
                showAddEditNoteDialog = false
            }
        )
    }
}

/**
 * Beautiful artistic watercolor brush stroke background drawn with Canvas.
 * Creates an organic, soft transparent watercolor sweep with wavy bristle wash.
 */
@Composable
fun WatercolorBrushStrokeBackground(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Layer 1: Organic outer watercolor wash with soft curved edges
        val brushWashPath = Path().apply {
            moveTo(w * 0.06f, h * 0.22f)
            cubicTo(w * 0.32f, h * 0.12f, w * 0.68f, h * 0.26f, w * 0.94f, h * 0.18f)
            cubicTo(w * 0.99f, h * 0.44f, w * 0.96f, h * 0.72f, w * 0.92f, h * 0.86f)
            cubicTo(w * 0.66f, h * 0.94f, w * 0.34f, h * 0.82f, w * 0.06f, h * 0.85f)
            cubicTo(w * 0.02f, h * 0.60f, w * 0.03f, h * 0.38f, w * 0.06f, h * 0.22f)
            close()
        }

        drawPath(
            path = brushWashPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    color.copy(alpha = 0.30f),
                    color.copy(alpha = 0.52f),
                    color.copy(alpha = 0.34f)
                )
            )
        )

        // Layer 2: Watercolor dense pool in the middle for authentic painter look
        drawRoundRect(
            color = color.copy(alpha = 0.26f),
            topLeft = Offset(w * 0.10f, h * 0.28f),
            size = Size(w * 0.80f, h * 0.44f),
            cornerRadius = CornerRadius(h * 0.22f, h * 0.22f)
        )

        // Layer 3: Dynamic bristle streak accents
        drawLine(
            color = color.copy(alpha = 0.45f),
            start = Offset(w * 0.14f, h * 0.25f),
            end = Offset(w * 0.86f, h * 0.22f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color.copy(alpha = 0.35f),
            start = Offset(w * 0.18f, h * 0.76f),
            end = Offset(w * 0.82f, h * 0.74f),
            strokeWidth = 2.0f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * 7-column Calendar Grid displaying days with transparent watercolor brush stroke support
 */
@Composable
private fun CalendarGridWithBrushStrokes(
    days: List<IslamicCalendarDay>,
    notesMap: Map<String, List<CalendarNoteItem>>,
    selectedDay: IslamicCalendarDay,
    onDayClick: (IslamicCalendarDay) -> Unit,
    onDayLongClick: (IslamicCalendarDay) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("islamic_calendar_grid")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val rows = days.chunked(7)
            rows.forEach { rowDays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowDays.forEach { day ->
                        val dayNotes = notesMap[day.dateKey] ?: emptyList()
                        val isSelected = (day.gregorianYear == selectedDay.gregorianYear &&
                                day.gregorianMonth == selectedDay.gregorianMonth &&
                                day.gregorianDay == selectedDay.gregorianDay)

                        Box(modifier = Modifier.weight(1f)) {
                            CalendarDayCellWithBrush(
                                day = day,
                                notes = dayNotes,
                                isSelected = isSelected,
                                onClick = { onDayClick(day) },
                                onLongClick = { onDayLongClick(day) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Day cell with:
 * - Transparent Watercolor Brush Stroke background if it has notes
 * - Selection ring when tapped
 * - Gregorian number (large, bold)
 * - Hijri date + short month (small, below)
 * - Special accents for Ayyamul Bidh (purple), Jumat (green), Ahad (red), Sabtu (blue)
 */
@Composable
private fun CalendarDayCellWithBrush(
    day: IslamicCalendarDay,
    notes: List<CalendarNoteItem>,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val gregorianTextColor = when {
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
        day.isSunday -> Color(0xFFDC2626) // Red Sunday
        day.isFriday -> Color(0xFF16A34A) // Green Friday
        day.isSaturday -> Color(0xFF2563EB) // Blue Saturday
        day.fastingType == FastingType.AYYAMUL_BIDH -> Color(0xFF7E22CE) // Purple Ayyamul Bidh
        else -> MaterialTheme.colorScheme.onSurface
    }

    val hijriTextColor = when {
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.22f)
        day.fastingType == FastingType.AYYAMUL_BIDH -> Color(0xFF9333EA)
        day.isFriday -> Color(0xFF15803D)
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    }

    val cellBorderModifier = when {
        isSelected -> Modifier.border(
            width = 2.dp,
            color = DeepEmeraldPrimary,
            shape = RoundedCornerShape(10.dp)
        )
        day.isToday -> Modifier.border(
            width = 2.dp,
            color = MeccaGold,
            shape = RoundedCornerShape(10.dp)
        )
        else -> Modifier
    }

    val cellBackground = when {
        isSelected -> DeepEmeraldPrimary.copy(alpha = 0.08f)
        day.isToday -> MeccaGold.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    // Determine brush stroke color from note if present
    val brushColorOption = remember(notes) {
        if (notes.isNotEmpty()) {
            SoftBrushPalette.getColor(notes.last().colorIndex)
        } else null
    }

    Surface(
        color = cellBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .then(cellBorderModifier)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag("calendar_day_${day.gregorianDay}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Watercolor Brush Stroke Layer behind the numbers
            if (brushColorOption != null && day.isCurrentMonth) {
                WatercolorBrushStrokeBackground(
                    color = Color(brushColorOption.primaryColorHex),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Gregorian Day Number (Large & Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day.gregorianDay.toString(),
                        fontSize = 16.sp,
                        fontWeight = if (day.isToday || isSelected || day.isSunday || day.isFriday) FontWeight.ExtraBold else FontWeight.Bold,
                        color = gregorianTextColor
                    )

                    // Dot indicators (Notes dot or Holiday dot)
                    if (day.isCurrentMonth) {
                        if (notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        Color(brushColorOption?.accentColorHex ?: 0xFF047857),
                                        CircleShape
                                    )
                            )
                        } else if (day.islamicEvent != null) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color(0xFFE11D48), CircleShape)
                            )
                        }
                    }
                }

                // Hijri Date Label below Gregorian (Small)
                Text(
                    text = "${day.hijriDate.day} ${day.hijriDate.monthNameShort}",
                    fontSize = 9.sp,
                    fontWeight = if (day.fastingType == FastingType.AYYAMUL_BIDH || notes.isNotEmpty()) FontWeight.Bold else FontWeight.Medium,
                    color = hijriTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                // Fasting / Note micro pill indicator
                if (day.isCurrentMonth) {
                    if (notes.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(brushColorOption!!.accentColorHex).copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "Catatan",
                                fontSize = 7.2.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(brushColorOption.accentColorHex),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    } else if (day.fastingType != null) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(day.fastingType.badgeColorHex).copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = when (day.fastingType) {
                                    FastingType.AYYAMUL_BIDH -> "Bidh"
                                    FastingType.SENIN_KAMIS -> if (day.isMonday) "Sen" else "Kam"
                                    FastingType.RAMADHAN -> "Ram"
                                    FastingType.ASYURA -> "Asy"
                                    FastingType.ARAFAH -> "Arf"
                                    FastingType.HARAM_PUASA -> "Haram"
                                },
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(day.fastingType.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

/**
 * Dedicated Notes Panel Section directly below the calendar.
 * Shows selected date title, notes list with soft brush accents, and action button to add new note.
 */
@Composable
private fun NotesPanelSection(
    selectedDay: IslamicCalendarDay,
    notes: List<CalendarNoteItem>,
    onAddNoteClick: () -> Unit,
    onEditNoteClick: (CalendarNoteItem) -> Unit,
    onDeleteNoteClick: (CalendarNoteItem) -> Unit,
    onViewDayDetail: () -> Unit
) {
    val dayName = IslamicCalendarHelper.DAY_NAMES_ID[selectedDay.dayOfWeek - 1]
    val monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[selectedDay.gregorianMonth - 1]
    val dateTitle = "$dayName, ${selectedDay.gregorianDay} $monthName ${selectedDay.gregorianYear}"

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DeepGoldAccent.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("calendar_notes_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Date Info + Add Note Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = DeepEmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "CATATAN & AGENDA HARIAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DeepGoldAccent,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "✨ ${selectedDay.hijriDate.formattedString}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Add Note Button
                Button(
                    onClick = onAddNoteClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEmeraldPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Notes List or Empty State
            if (notes.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    notes.forEach { note ->
                        val brushOption = SoftBrushPalette.getColor(note.colorIndex)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(brushOption.primaryColorHex).copy(alpha = 0.18f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Color(brushOption.accentColorHex).copy(alpha = 0.35f),
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                // Watercolor Pill on the left edge
                                Box(
                                    modifier = Modifier
                                        .width(5.dp)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(brushOption.accentColorHex))
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    // Palette Label Tag
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(brushOption.accentColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = brushOption.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(brushOption.accentColorHex),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = note.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = { onEditNoteClick(note) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Catatan",
                                            tint = Color(brushOption.accentColorHex),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteNoteClick(note) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus Catatan",
                                            tint = DangerCoral,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty state card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(MeccaGold.copy(alpha = 0.20f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brush,
                                contentDescription = null,
                                tint = DeepGoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Belum ada catatan pada tanggal ini",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tekan 'Tambah' untuk menandai tanggal ini dengan sapuan kuas warna lembut.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick button to view day detail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onViewDayDetail,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Lihat Keutamaan Hari Ini →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepEmeraldPrimary
                    )
                }
            }
        }
    }
}

/**
 * Dialog to Add or Edit Note with soft watercolor brush color selection.
 */
@Composable
private fun AddEditNoteDialog(
    selectedDay: IslamicCalendarDay,
    existingNote: CalendarNoteItem?,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit,
    onDelete: () -> Unit
) {
    var noteText by remember { mutableStateOf(existingNote?.text ?: "") }
    var selectedColorIndex by remember { mutableIntStateOf(existingNote?.colorIndex ?: 0) }

    val dayName = IslamicCalendarHelper.DAY_NAMES_ID[selectedDay.dayOfWeek - 1]
    val monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[selectedDay.gregorianMonth - 1]
    val dateStr = "$dayName, ${selectedDay.gregorianDay} $monthName ${selectedDay.gregorianYear}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (existingNote != null) "Edit Catatan Tanggal" else "Tambah Catatan Kalender",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$dateStr • ${selectedDay.hijriDate.formattedString}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepGoldAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Text Input
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text("Tulis catatan, agenda ibadah, puasa, atau jadwal...", fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .testTag("note_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepEmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.40f)
                    )
                )

                // Soft Brush Color Palette Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Pilih Warna Efek Kuas Kalender:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 6 Soft Pastel Watercolor Swatches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SoftBrushPalette.options.forEachIndexed { index, option ->
                            val isSelected = selectedColorIndex == index

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(option.primaryColorHex))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color(option.accentColorHex) else Color.Black.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(option.accentColorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Selected Brush Palette Label with live watercolor preview
                    val currentOption = SoftBrushPalette.getColor(selectedColorIndex)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(currentOption.primaryColorHex).copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(currentOption.accentColorHex).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color(currentOption.accentColorHex), CircleShape)
                            )
                            Text(
                                text = "Warna: ${currentOption.label}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(currentOption.accentColorHex)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        onSave(noteText.trim(), selectedColorIndex)
                    }
                },
                enabled = noteText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmeraldPrimary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("save_note_button")
            ) {
                Text("Simpan Catatan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (existingNote != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = DangerCoral)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
            }
        }
    )
}

/**
 * Month & Year Navigator Header Card styled similarly to the screenshot card.
 */
@Composable
private fun MonthYearNavigatorCard(
    year: Int,
    month: Int,
    hijriRange: String,
    onPrevYear: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNextYear: () -> Unit
) {
    val monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[month - 1].uppercase()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DeepGoldAccent.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("month_year_navigator_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPrevYear,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                        contentDescription = "Tahun Sebelumnya",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onPrevMonth,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Bulan Sebelumnya",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$monthName $year",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }

                if (hijriRange.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hijriRange,
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepGoldAccent,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Bulan Berikutnya",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onNextYear,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowRight,
                        contentDescription = "Tahun Berikutnya",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * 7 Day Header Pills row matching screenshot:
 * MIN (Red), SEN, SEL, RAB, KAM, JUM (Green Sayyidul Ayyam), SAB (Blue)
 */
@Composable
private fun DayHeaderPillsRow() {
    val headers = listOf(
        DayHeaderInfo("MIN", Color(0xFFB91C1C)), // Red
        DayHeaderInfo("SEN", Color(0xFF374151)), // Dark Charcoal
        DayHeaderInfo("SEL", Color(0xFF374151)),
        DayHeaderInfo("RAB", Color(0xFF374151)),
        DayHeaderInfo("KAM", Color(0xFF374151)),
        DayHeaderInfo("JUM", Color(0xFF15803D)), // Islamic Green (Sayyidul Ayyam)
        DayHeaderInfo("SAB", Color(0xFF1D4ED8))  // Blue
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        headers.forEach { header ->
            Surface(
                color = header.bgColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = header.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private data class DayHeaderInfo(val label: String, val bgColor: Color)

/**
 * Legend badge explanation card including soft brush stroke note indicator
 */
@Composable
private fun CalendarLegendCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(label = "Hari Ini", color = MeccaGold)
            LegendItem(label = "Catatan Kuas", color = Color(0xFF047857))
            LegendItem(label = "Jumat", color = Color(0xFF16A34A))
            LegendItem(label = "Ayyamul Bidh", color = Color(0xFF9333EA))
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Summary card listing events and Sunnah fasting days for the selected month
 */
@Composable
private fun IslamicEventsSummaryCard(
    monthName: String,
    year: Int,
    days: List<IslamicCalendarDay>,
    onDayClick: (IslamicCalendarDay) -> Unit
) {
    val currentMonthDays = days.filter { it.isCurrentMonth }
    val eventsInMonth = currentMonthDays.filter { it.islamicEvent != null }
    val ayyamulBidhInMonth = currentMonthDays.filter { it.fastingType == FastingType.AYYAMUL_BIDH }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Events Card
        if (eventsInMonth.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        DeepGoldAccent.copy(alpha = 0.35f),
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = DeepGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Peringatan & Hari Besar Islam ($monthName $year)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    eventsInMonth.forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onDayClick(day) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepGoldAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${day.gregorianDay} $monthName",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepGoldAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = day.islamicEvent ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = day.hijriDate.formattedShort,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Sunnah Fasting Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    AlignedEmerald.copy(alpha = 0.35f),
                    RoundedCornerShape(18.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = null,
                        tint = AlignedEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Jadwal Puasa Ayyamul Bidh (13, 14, 15 Hijriyah)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                if (ayyamulBidhInMonth.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ayyamulBidhInMonth.forEach { day ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF9333EA).copy(alpha = 0.10f),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        1.dp,
                                        Color(0xFF9333EA).copy(alpha = 0.30f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onDayClick(day) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${day.gregorianDay} $monthName",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E22CE)
                                    )
                                    Text(
                                        text = "${day.hijriDate.day} ${day.hijriDate.monthNameShort}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Puasa Ayyamul Bidh jatuh pada pertengahan bulan Hijriyah (tanggal 13, 14, dan 15).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Modal Bottom Sheet showing rich details and notes for a tapped calendar day
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailBottomSheet(
    day: IslamicCalendarDay,
    notes: List<CalendarNoteItem>,
    onAddNote: () -> Unit,
    onDismiss: () -> Unit
) {
    val dayName = IslamicCalendarHelper.DAY_NAMES_ID[day.dayOfWeek - 1]
    val monthName = IslamicCalendarHelper.GREGORIAN_MONTH_NAMES_ID[day.gregorianMonth - 1]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$dayName, ${day.gregorianDay} $monthName ${day.gregorianYear}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "✨ ${day.hijriDate.formattedString} (${day.hijriDate.monthNameArabic})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepGoldAccent
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            HorizontalDivider()

            // Islamic Event / Holiday Info
            if (day.islamicEvent != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF1F2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Hari Peringatan Islam",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE11D48),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = day.islamicEvent,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF881337)
                            )
                        }
                    }
                }
            }

            // Fasting Recommendation
            if (day.fastingType != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(day.fastingType.badgeColorHex).copy(alpha = 0.10f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color(day.fastingType.badgeColorHex).copy(alpha = 0.30f),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nightlight,
                            contentDescription = null,
                            tint = Color(day.fastingType.badgeColorHex),
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Tuntunan Puasa",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(day.fastingType.badgeColorHex),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (day.fastingType) {
                                    FastingType.AYYAMUL_BIDH -> "Dianjurkan Puasa Sunnah Ayyamul Bidh (13, 14, 15 Hijriyah)"
                                    FastingType.SENIN_KAMIS -> if (day.isMonday) "Dianjurkan Puasa Sunnah Hari Senin" else "Dianjurkan Puasa Sunnah Hari Kamis"
                                    FastingType.RAMADHAN -> "Puasa Wajib Bulan Suci Ramadhan"
                                    FastingType.ASYURA -> "Puasa Sunnah Asyura & Tasu'a"
                                    FastingType.ARAFAH -> "Puasa Sunnah Hari Arafah"
                                    FastingType.HARAM_PUASA -> "Diharamkan berpuasa pada hari ini (Hari Raya / Tasyrik)"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Notes for this day if any
            if (notes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Catatan Kuas Anda pada Tanggal Ini:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    notes.forEach { note ->
                        val brushOption = SoftBrushPalette.getColor(note.colorIndex)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(brushOption.primaryColorHex).copy(alpha = 0.22f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(brushOption.accentColorHex).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = note.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Keutamaan Hari
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Keutamaan Hari:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        day.isFriday -> "Hari Jum'at adalah Sayyidul Ayyam (penghulu segala hari), hari yang paling mulia dalam sepekan. Disunnahkan memperbanyak sholawat, membaca Surat Al-Kahfi, bersedekah, dan melaksanakan Sholat Jum'at."
                        day.fastingType == FastingType.AYYAMUL_BIDH -> "Puasa tiga hari di pertengahan bulan (Ayyamul Bidh) pahalanya seperti berpuasa sepanjang tahun (HR. Bukhari & Muslim)."
                        day.isMonday -> "Hari Senin adalah hari di mana Rasulullah SAW dilahirkan dan hari diturunkannya wahyu Al-Qur'an (HR. Muslim)."
                        day.isThursday -> "Hari Kamis adalah hari di mana amal-amal perbuatan manusia diperlihatkan kepada Allah SWT (HR. Tirmidzi)."
                        else -> "Setiap hari seorang mukmin adalah kesempatan untuk memperbanyak dzikir, istighfar, membaca Al-Qur'an, dan menjaga sholat lima waktu tepat waktu."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAddNote,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tulis Catatan", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
