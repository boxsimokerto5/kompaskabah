package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.tasbih.TasbihBeadTheme
import com.example.domain.tasbih.TasbihState

class TasbihPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tasbih_preferences_v1", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_COUNT = "key_tasbih_current_count"
        private const val KEY_TARGET_COUNT = "key_tasbih_target_count"
        private const val KEY_TOTAL_COUNT = "key_tasbih_total_count"
        private const val KEY_LAP_COUNT = "key_tasbih_lap_count"
        private const val KEY_SELECTED_DZIKIR = "key_tasbih_selected_dzikir"
        private const val KEY_BEAD_THEME = "key_tasbih_bead_theme"
        private const val KEY_SOUND_ENABLED = "key_tasbih_sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "key_tasbih_vibration_enabled"
        private const val KEY_AUTO_ADVANCE = "key_tasbih_auto_advance"
    }

    fun loadTasbihState(): TasbihState {
        val themeName = prefs.getString(KEY_BEAD_THEME, TasbihBeadTheme.AMBER.name)
        val beadTheme = try {
            TasbihBeadTheme.valueOf(themeName ?: TasbihBeadTheme.AMBER.name)
        } catch (_: Exception) {
            TasbihBeadTheme.AMBER
        }

        return TasbihState(
            currentCount = prefs.getInt(KEY_CURRENT_COUNT, 0),
            targetCount = prefs.getInt(KEY_TARGET_COUNT, 33),
            totalCount = prefs.getLong(KEY_TOTAL_COUNT, 0L),
            lapCount = prefs.getInt(KEY_LAP_COUNT, 1),
            selectedDzikirIndex = prefs.getInt(KEY_SELECTED_DZIKIR, 0),
            beadTheme = beadTheme,
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            autoAdvanceTarget = prefs.getBoolean(KEY_AUTO_ADVANCE, true)
        )
    }

    fun saveTasbihState(state: TasbihState) {
        prefs.edit()
            .putInt(KEY_CURRENT_COUNT, state.currentCount)
            .putInt(KEY_TARGET_COUNT, state.targetCount)
            .putLong(KEY_TOTAL_COUNT, state.totalCount)
            .putInt(KEY_LAP_COUNT, state.lapCount)
            .putInt(KEY_SELECTED_DZIKIR, state.selectedDzikirIndex)
            .putString(KEY_BEAD_THEME, state.beadTheme.name)
            .putBoolean(KEY_SOUND_ENABLED, state.soundEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, state.vibrationEnabled)
            .putBoolean(KEY_AUTO_ADVANCE, state.autoAdvanceTarget)
            .apply()
    }
}
