package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CalendarNoteItem(
    val id: String = UUID.randomUUID().toString(),
    val dateKey: String, // Format: "yyyy-MM-dd", e.g. "2026-10-11"
    val text: String,
    val colorIndex: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class BrushColorOption(
    val name: String,
    val primaryColorHex: Long,  // Soft pastel watercolor tone
    val accentColorHex: Long,   // Darker accent tone for badges/borders
    val label: String
)

object SoftBrushPalette {
    val options = listOf(
        BrushColorOption(
            name = "Soft Mint",
            primaryColorHex = 0xFFA7F3D0, // Soft pastel emerald green
            accentColorHex = 0xFF047857,
            label = "🌿 Mint (Ibadah & Amal)"
        ),
        BrushColorOption(
            name = "Soft Peach",
            primaryColorHex = 0xFFFECDD3, // Soft peach/rose
            accentColorHex = 0xFFBE123C,
            label = "🍑 Peach (Acara & Silaturahmi)"
        ),
        BrushColorOption(
            name = "Soft Lavender",
            primaryColorHex = 0xFFE9D5FF, // Soft lilac/purple
            accentColorHex = 0xFF7E22CE,
            label = "🌸 Lavender (Penting / Khusus)"
        ),
        BrushColorOption(
            name = "Soft Sky Blue",
            primaryColorHex = 0xFFBAE6FD, // Soft sky blue
            accentColorHex = 0xFF0369A1,
            label = "🌊 Biru Langit (Jadwal & Tugas)"
        ),
        BrushColorOption(
            name = "Soft Honey Gold",
            primaryColorHex = 0xFFFEF08A, // Soft yellow/honey gold
            accentColorHex = 0xFFB45309,
            label = "✨ Emas Madu (Sedekah & Syukur)"
        ),
        BrushColorOption(
            name = "Soft Sunset Orange",
            primaryColorHex = 0xFFFED7AA, // Soft warm orange
            accentColorHex = 0xFFC2410C,
            label = "🌅 Jingga Senja (Catatan Pribadi)"
        )
    )

    fun getColor(index: Int): BrushColorOption {
        return options.getOrElse(index.coerceIn(0, options.lastIndex)) { options[0] }
    }
}

class CalendarNotesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _notesMap = MutableStateFlow<Map<String, List<CalendarNoteItem>>>(emptyMap())
    val notesMap: StateFlow<Map<String, List<CalendarNoteItem>>> = _notesMap.asStateFlow()

    init {
        loadAllNotes()
    }

    private fun loadAllNotes() {
        val allJson = prefs.getString(KEY_ALL_NOTES, "[]") ?: "[]"
        val map = mutableMapOf<String, MutableList<CalendarNoteItem>>()

        try {
            val jsonArray = JSONArray(allJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = CalendarNoteItem(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    dateKey = obj.getString("dateKey"),
                    text = obj.getString("text"),
                    colorIndex = obj.optInt("colorIndex", 0),
                    timestampMillis = obj.optLong("timestampMillis", System.currentTimeMillis())
                )
                map.getOrPut(item.dateKey) { mutableListOf() }.add(item)
            }
        } catch (_: Exception) {
        }

        _notesMap.value = map
    }

    @Synchronized
    fun addOrUpdateNote(dateKey: String, text: String, colorIndex: Int, noteId: String? = null): CalendarNoteItem {
        val current = _notesMap.value.toMutableMap()
        val listForDate = current[dateKey]?.toMutableList() ?: mutableListOf()

        val savedItem: CalendarNoteItem
        if (noteId != null) {
            val idx = listForDate.indexOfFirst { it.id == noteId }
            if (idx >= 0) {
                savedItem = listForDate[idx].copy(
                    text = text,
                    colorIndex = colorIndex,
                    timestampMillis = System.currentTimeMillis()
                )
                listForDate[idx] = savedItem
            } else {
                savedItem = CalendarNoteItem(
                    id = noteId,
                    dateKey = dateKey,
                    text = text,
                    colorIndex = colorIndex,
                    timestampMillis = System.currentTimeMillis()
                )
                listForDate.add(savedItem)
            }
        } else {
            savedItem = CalendarNoteItem(
                dateKey = dateKey,
                text = text,
                colorIndex = colorIndex,
                timestampMillis = System.currentTimeMillis()
            )
            listForDate.add(savedItem)
        }

        current[dateKey] = listForDate
        _notesMap.value = current
        persistAll(current)
        return savedItem
    }

    @Synchronized
    fun deleteNote(noteId: String, dateKey: String) {
        val current = _notesMap.value.toMutableMap()
        val listForDate = current[dateKey]?.toMutableList() ?: return
        listForDate.removeAll { it.id == noteId }

        if (listForDate.isEmpty()) {
            current.remove(dateKey)
        } else {
            current[dateKey] = listForDate
        }

        _notesMap.value = current
        persistAll(current)
    }

    private fun persistAll(map: Map<String, List<CalendarNoteItem>>) {
        try {
            val jsonArray = JSONArray()
            map.values.flatten().forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("dateKey", item.dateKey)
                    put("text", item.text)
                    put("colorIndex", item.colorIndex)
                    put("timestampMillis", item.timestampMillis)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_ALL_NOTES, jsonArray.toString()).apply()
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val PREFS_NAME = "calendar_notes_preferences"
        private const val KEY_ALL_NOTES = "key_all_calendar_notes"

        fun formatDateKey(year: Int, month: Int, day: Int): String {
            return String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month, day)
        }

        @Volatile
        private var instance: CalendarNotesRepository? = null

        fun getInstance(context: Context): CalendarNotesRepository {
            return instance ?: synchronized(this) {
                instance ?: CalendarNotesRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
