package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.R
import com.example.domain.PresetCitiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.qiblaDataStore: DataStore<Preferences> by preferencesDataStore(name = "qibla_preferences")

enum class MuezzinVoiceOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val rawResId: Int
) {
    MAKKAH(
        id = "makkah",
        title = "Adzan Makkah Al-Mukarramah",
        subtitle = "Masjidil Haram • Syaikh Ali Ahmed Mulla",
        rawResId = R.raw.adzan_makkah
    ),
    ALAFASY(
        id = "alafasy",
        title = "Adzan Madinah & Mishary Alafasy",
        subtitle = "Lantunan Adzan Merdu & Syahdu",
        rawResId = R.raw.adzan_alafasy
    );

    companion object {
        fun fromId(id: String?): MuezzinVoiceOption {
            return entries.firstOrNull { it.id == id } ?: MAKKAH
        }
    }
}

data class StoredQiblaPreferences(
    val latitude: Double = PresetCitiesRepository.defaultCity.latitude,
    val longitude: Double = PresetCitiesRepository.defaultCity.longitude,
    val locationLabel: String = PresetCitiesRepository.defaultCity.name,
    val timezoneOffsetHours: Double = PresetCitiesRepository.defaultCity.timezoneOffsetHours,
    val isUsingLiveGps: Boolean = false,
    val useTrueNorthCorrection: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = false,
    val adhanAutoEnabled: Boolean = true,
    val selectedMuezzinId: String = MuezzinVoiceOption.MAKKAH.id,
    val enabledAdhanPrayers: Set<String> = DEFAULT_ADHAN_PRAYERS
) {
    val selectedMuezzin: MuezzinVoiceOption
        get() = MuezzinVoiceOption.fromId(selectedMuezzinId)

    companion object {
        val DEFAULT_ADHAN_PRAYERS = setOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
    }
}

class QiblaPreferencesRepository(private val context: Context) {

    private object Keys {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val LOCATION_LABEL = stringPreferencesKey("location_label")
        val TIMEZONE_OFFSET = doublePreferencesKey("timezone_offset")
        val IS_USING_LIVE_GPS = booleanPreferencesKey("is_using_live_gps")
        val USE_TRUE_NORTH = booleanPreferencesKey("use_true_north")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val ADHAN_AUTO_ENABLED = booleanPreferencesKey("adhan_auto_enabled")
        val SELECTED_MUEZZIN_ID = stringPreferencesKey("selected_muezzin_id")
        val ENABLED_ADHAN_PRAYERS = stringSetPreferencesKey("enabled_adhan_prayers")
    }

    val preferencesFlow: Flow<StoredQiblaPreferences> = context.qiblaDataStore.data.map { prefs ->
        StoredQiblaPreferences(
            latitude = prefs[Keys.LATITUDE] ?: PresetCitiesRepository.defaultCity.latitude,
            longitude = prefs[Keys.LONGITUDE] ?: PresetCitiesRepository.defaultCity.longitude,
            locationLabel = prefs[Keys.LOCATION_LABEL] ?: PresetCitiesRepository.defaultCity.name,
            timezoneOffsetHours = prefs[Keys.TIMEZONE_OFFSET] ?: PresetCitiesRepository.defaultCity.timezoneOffsetHours,
            isUsingLiveGps = prefs[Keys.IS_USING_LIVE_GPS] ?: false,
            useTrueNorthCorrection = prefs[Keys.USE_TRUE_NORTH] ?: true,
            hapticFeedbackEnabled = prefs[Keys.HAPTIC_ENABLED] ?: true,
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: false,
            adhanAutoEnabled = prefs[Keys.ADHAN_AUTO_ENABLED] ?: true,
            selectedMuezzinId = prefs[Keys.SELECTED_MUEZZIN_ID] ?: MuezzinVoiceOption.MAKKAH.id,
            enabledAdhanPrayers = prefs[Keys.ENABLED_ADHAN_PRAYERS] ?: StoredQiblaPreferences.DEFAULT_ADHAN_PRAYERS
        )
    }

    suspend fun saveLocation(
        latitude: Double,
        longitude: Double,
        label: String,
        timezoneOffsetHours: Double,
        isLiveGps: Boolean
    ) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.LATITUDE] = latitude
            prefs[Keys.LONGITUDE] = longitude
            prefs[Keys.LOCATION_LABEL] = label
            prefs[Keys.TIMEZONE_OFFSET] = timezoneOffsetHours
            prefs[Keys.IS_USING_LIVE_GPS] = isLiveGps
        }
    }

    suspend fun setTrueNorthCorrection(enabled: Boolean) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.USE_TRUE_NORTH] = enabled
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun setAdhanAutoEnabled(enabled: Boolean) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.ADHAN_AUTO_ENABLED] = enabled
        }
    }

    suspend fun setSelectedMuezzin(muezzin: MuezzinVoiceOption) {
        context.qiblaDataStore.edit { prefs ->
            prefs[Keys.SELECTED_MUEZZIN_ID] = muezzin.id
        }
    }

    suspend fun togglePrayerAdhanEnabled(prayerName: String) {
        context.qiblaDataStore.edit { prefs ->
            val currentSet = prefs[Keys.ENABLED_ADHAN_PRAYERS]
                ?: StoredQiblaPreferences.DEFAULT_ADHAN_PRAYERS
            val updated = if (currentSet.contains(prayerName)) {
                currentSet - prayerName
            } else {
                currentSet + prayerName
            }
            prefs[Keys.ENABLED_ADHAN_PRAYERS] = updated
        }
    }
}
