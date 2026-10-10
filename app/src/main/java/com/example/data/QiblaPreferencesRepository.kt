package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.PresetCitiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.qiblaDataStore: DataStore<Preferences> by preferencesDataStore(name = "qibla_preferences")

data class StoredQiblaPreferences(
    val latitude: Double = PresetCitiesRepository.defaultCity.latitude,
    val longitude: Double = PresetCitiesRepository.defaultCity.longitude,
    val locationLabel: String = PresetCitiesRepository.defaultCity.name,
    val timezoneOffsetHours: Double = PresetCitiesRepository.defaultCity.timezoneOffsetHours,
    val isUsingLiveGps: Boolean = false,
    val useTrueNorthCorrection: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = false
)

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
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: false
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
}
