package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CompassSensorManager
import com.example.data.CompassSensorReading
import com.example.data.LocationTracker
import com.example.data.QiblaPreferencesRepository
import com.example.data.SensorAccuracyLevel
import com.example.domain.CityPreset
import com.example.domain.PrayerTimeItem
import com.example.domain.PresetCitiesRepository
import com.example.domain.QiblaCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

enum class QiblaNavigationTab {
    COMPASS,
    PRAYER_TIMES,
    SENSOR_DIAGNOSTICS
}

data class QiblaUiState(
    val selectedTab: QiblaNavigationTab = QiblaNavigationTab.COMPASS,
    val latitude: Double = PresetCitiesRepository.defaultCity.latitude,
    val longitude: Double = PresetCitiesRepository.defaultCity.longitude,
    val altitudeMeters: Double = 0.0,
    val locationLabel: String = PresetCitiesRepository.defaultCity.name,
    val timezoneOffsetHours: Double = PresetCitiesRepository.defaultCity.timezoneOffsetHours,
    val isLiveGpsActive: Boolean = false,
    val isFetchingGps: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val gpsAccuracyMeters: Float? = null,
    val gpsStatusMessage: String? = null,

    // Calculated Qibla Geodesic Data
    val qiblaBearingTrueNorthDeg: Float = 295.15f,
    val distanceToKaabaKm: Double = 7919.0,
    val magneticDeclinationDeg: Float = 0.8f,

    // Sensor & Heading Data
    val rawMagneticAzimuthDeg: Float = 0f,
    val effectiveDeviceHeadingDeg: Float = 0f,
    val manualHeadingOffsetDeg: Float = 0f,
    val isManualRotationOverride: Boolean = false,
    val isHardwareSensorAvailable: Boolean = true,
    val sensorSourceName: String = "Fusi Sensor 3D (Rotation Vector + Magnetometer)",
    val pitchDeg: Float = 0f,
    val rollDeg: Float = 0f,
    val magneticFieldStrengthUt: Float = 45.0f,
    val sensorAccuracy: SensorAccuracyLevel = SensorAccuracyLevel.HIGH,

    // Preferences
    val useTrueNorthCorrection: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = false,

    // Modals & Sheets
    val showCityPickerSheet: Boolean = false,
    val showCalibrationSheet: Boolean = false,

    // Prayer Times
    val prayerTimes: List<PrayerTimeItem> = emptyList(),
    val nextPrayerName: String = "Maghrib",
    val nextPrayerTimeFormatted: String = "17:54",
    val nextPrayerCountdownLabel: String = ""
) {
    /**
     * Shortest signed angle delta in [-180, +180] from current phone heading to Qibla.
     * Positive -> turn right (clockwise), Negative -> turn left (counter-clockwise).
     */
    val angleDeltaToQiblaDeg: Float
        get() = QiblaCalculator.shortestAngleDelta(effectiveDeviceHeadingDeg, qiblaBearingTrueNorthDeg)

    /**
     * Whether the phone is currently aligned with the Holy Kaaba within ±3.0 degrees.
     */
    val isAlignedWithKaaba: Boolean
        get() = abs(angleDeltaToQiblaDeg) <= 3.0f

    /**
     * Whether the phone is close to alignment (±12 degrees) for visual glow ramping.
     */
    val isNearAlignmentWithKaaba: Boolean
        get() = abs(angleDeltaToQiblaDeg) <= 12.0f

    val hasMagneticInterference: Boolean
        get() = isHardwareSensorAvailable && (magneticFieldStrengthUt < 18f || magneticFieldStrengthUt > 85f)

    val isDeviceTilted: Boolean
        get() = isHardwareSensorAvailable && !isManualRotationOverride && (abs(pitchDeg) > 32f || abs(rollDeg) > 32f)
}

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val compassSensorManager = CompassSensorManager(application)
    private val locationTracker = LocationTracker(application)
    private val preferencesRepository = QiblaPreferencesRepository(application)

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var gpsStreamJob: Job? = null

    init {
        val initialPerm = locationTracker.hasLocationPermission()
        _uiState.update {
            it.copy(
                hasLocationPermission = initialPerm,
                isHardwareSensorAvailable = compassSensorManager.hasHardwareCompass
            )
        }
        recomputeGeodesicAndPrayers(
            lat = _uiState.value.latitude,
            lon = _uiState.value.longitude,
            alt = 0.0,
            tzOffset = _uiState.value.timezoneOffsetHours
        )

        observePreferences()
        observeSensorStream()

        if (initialPerm) {
            refreshLiveGpsLocation()
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collectLatest { prefs ->
                _uiState.update { current ->
                    val effectiveHeading = computeEffectiveHeading(
                        rawMagnetic = current.rawMagneticAzimuthDeg,
                        declination = current.magneticDeclinationDeg,
                        useTrueNorth = prefs.useTrueNorthCorrection,
                        isManual = current.isManualRotationOverride,
                        manualAngle = current.manualHeadingOffsetDeg,
                        hasHardware = current.isHardwareSensorAvailable
                    )
                    current.copy(
                        latitude = prefs.latitude,
                        longitude = prefs.longitude,
                        locationLabel = prefs.locationLabel,
                        timezoneOffsetHours = prefs.timezoneOffsetHours,
                        isLiveGpsActive = prefs.isUsingLiveGps && current.hasLocationPermission,
                        useTrueNorthCorrection = prefs.useTrueNorthCorrection,
                        hapticFeedbackEnabled = prefs.hapticFeedbackEnabled,
                        darkThemeEnabled = prefs.darkThemeEnabled,
                        effectiveDeviceHeadingDeg = effectiveHeading
                    )
                }
                recomputeGeodesicAndPrayers(
                    lat = prefs.latitude,
                    lon = prefs.longitude,
                    alt = _uiState.value.altitudeMeters,
                    tzOffset = prefs.timezoneOffsetHours
                )
            }
        }
    }

    private fun observeSensorStream() {
        viewModelScope.launch {
            compassSensorManager.observeCompassReadings().collectLatest { reading: CompassSensorReading ->
                _uiState.update { current ->
                    val effectiveHeading = computeEffectiveHeading(
                        rawMagnetic = reading.magneticAzimuthDeg,
                        declination = current.magneticDeclinationDeg,
                        useTrueNorth = current.useTrueNorthCorrection,
                        isManual = current.isManualRotationOverride,
                        manualAngle = current.manualHeadingOffsetDeg,
                        hasHardware = reading.isHardwareSensorAvailable
                    )
                    current.copy(
                        rawMagneticAzimuthDeg = reading.magneticAzimuthDeg,
                        effectiveDeviceHeadingDeg = effectiveHeading,
                        pitchDeg = reading.pitchDeg,
                        rollDeg = reading.rollDeg,
                        magneticFieldStrengthUt = reading.magneticFieldStrengthUt,
                        sensorAccuracy = reading.accuracy,
                        sensorSourceName = reading.sensorSourceName,
                        isHardwareSensorAvailable = reading.isHardwareSensorAvailable
                    )
                }
            }
        }
    }

    private fun computeEffectiveHeading(
        rawMagnetic: Float,
        declination: Float,
        useTrueNorth: Boolean,
        isManual: Boolean,
        manualAngle: Float,
        hasHardware: Boolean
    ): Float {
        if (isManual || !hasHardware) {
            return ((manualAngle % 360f) + 360f) % 360f
        }
        val corrected = if (useTrueNorth) rawMagnetic + declination else rawMagnetic
        return ((corrected % 360f) + 360f) % 360f
    }

    private fun recomputeGeodesicAndPrayers(
        lat: Double,
        lon: Double,
        alt: Double,
        tzOffset: Double
    ) {
        val bearing = QiblaCalculator.calculateQiblaBearing(lat, lon).toFloat()
        val distanceKm = QiblaCalculator.calculateDistanceToKaabaKm(lat, lon)
        val declination = QiblaCalculator.calculateMagneticDeclination(lat, lon, alt)

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val prayers = QiblaCalculator.calculateDailyPrayerTimes(
            latitude = lat,
            longitude = lon,
            timezoneOffsetHours = tzOffset,
            year = year,
            month = month,
            day = day
        )

        val currentDecimalHour = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0
        val obligatoryPrayers = prayers.filter { it.isObligatory }
        val nextPrayer = obligatoryPrayers.firstOrNull { it.decimalHours > currentDecimalHour }
            ?: obligatoryPrayers.firstOrNull()

        val countdownText = if (nextPrayer != null) {
            var diffHours = nextPrayer.decimalHours - currentDecimalHour
            if (diffHours < 0) diffHours += 24.0
            val h = diffHours.toInt()
            val m = ((diffHours - h) * 60).toInt().coerceIn(0, 59)
            if (h > 0) "${h}j ${m}m lagi" else "${m} menit lagi"
        } else {
            ""
        }

        _uiState.update { current ->
            val effectiveHeading = computeEffectiveHeading(
                rawMagnetic = current.rawMagneticAzimuthDeg,
                declination = declination,
                useTrueNorth = current.useTrueNorthCorrection,
                isManual = current.isManualRotationOverride,
                manualAngle = current.manualHeadingOffsetDeg,
                hasHardware = current.isHardwareSensorAvailable
            )
            current.copy(
                qiblaBearingTrueNorthDeg = bearing,
                distanceToKaabaKm = distanceKm,
                magneticDeclinationDeg = declination,
                effectiveDeviceHeadingDeg = effectiveHeading,
                prayerTimes = prayers,
                nextPrayerName = nextPrayer?.nameId ?: "Subuh",
                nextPrayerTimeFormatted = nextPrayer?.formattedTime ?: "04:35",
                nextPrayerCountdownLabel = countdownText
            )
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
        if (granted) {
            refreshLiveGpsLocation()
        } else {
            _uiState.update {
                it.copy(
                    isFetchingGps = false,
                    gpsStatusMessage = "Izin GPS ditolak • Menggunakan lokasi kota pilihan"
                )
            }
        }
    }

    fun refreshLiveGpsLocation() {
        val hasPerm = locationTracker.hasLocationPermission()
        _uiState.update { it.copy(hasLocationPermission = hasPerm) }
        if (!hasPerm) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFetchingGps = true,
                    gpsStatusMessage = "Mengunci sinyal satelit GPS…"
                )
            }

            val coords = locationTracker.fetchCurrentLocationOnce()
            if (coords != null) {
                val tzHours = TimeZone.getDefault().rawOffset / 3600000.0
                val label = coords.addressLabel ?: "Koordinat GPS Real-Time"
                preferencesRepository.saveLocation(
                    latitude = coords.latitude,
                    longitude = coords.longitude,
                    label = label,
                    timezoneOffsetHours = tzHours,
                    isLiveGps = true
                )
                _uiState.update {
                    it.copy(
                        latitude = coords.latitude,
                        longitude = coords.longitude,
                        altitudeMeters = coords.altitudeMeters,
                        locationLabel = label,
                        timezoneOffsetHours = tzHours,
                        isLiveGpsActive = true,
                        isFetchingGps = false,
                        gpsAccuracyMeters = coords.accuracyMeters,
                        gpsStatusMessage = "GPS Terkunci (±${coords.accuracyMeters.toInt()}m)"
                    )
                }
                recomputeGeodesicAndPrayers(
                    lat = coords.latitude,
                    lon = coords.longitude,
                    alt = coords.altitudeMeters,
                    tzOffset = tzHours
                )
            } else {
                _uiState.update {
                    it.copy(
                        isFetchingGps = false,
                        gpsStatusMessage = "Sinyal GPS belum tersedia • Menggunakan koordinat tersimpan"
                    )
                }
            }

            // Start continuous GPS updates
            startContinuousGpsStream()
        }
    }

    private fun startContinuousGpsStream() {
        gpsStreamJob?.cancel()
        gpsStreamJob = viewModelScope.launch {
            locationTracker.observeLocationUpdates().collectLatest { coords ->
                // Only apply automatic GPS updates if the user hasn't explicitly locked a manual preset city
                if (_uiState.value.isLiveGpsActive) {
                    val tzHours = TimeZone.getDefault().rawOffset / 3600000.0
                    val resolvedName = locationTracker.resolveCityName(coords.latitude, coords.longitude)
                        ?: _uiState.value.locationLabel
                    _uiState.update {
                        it.copy(
                            latitude = coords.latitude,
                            longitude = coords.longitude,
                            altitudeMeters = coords.altitudeMeters,
                            locationLabel = resolvedName,
                            gpsAccuracyMeters = coords.accuracyMeters,
                            gpsStatusMessage = "GPS Real-Time Aktif (±${coords.accuracyMeters.toInt()}m)"
                        )
                    }
                    recomputeGeodesicAndPrayers(
                        lat = coords.latitude,
                        lon = coords.longitude,
                        alt = coords.altitudeMeters,
                        tzOffset = tzHours
                    )
                }
            }
        }
    }

    fun selectPresetCity(city: CityPreset) {
        viewModelScope.launch {
            preferencesRepository.saveLocation(
                latitude = city.latitude,
                longitude = city.longitude,
                label = "${city.name}, ${city.country}",
                timezoneOffsetHours = city.timezoneOffsetHours,
                isLiveGps = false
            )
            _uiState.update {
                it.copy(
                    latitude = city.latitude,
                    longitude = city.longitude,
                    locationLabel = city.name,
                    timezoneOffsetHours = city.timezoneOffsetHours,
                    isLiveGpsActive = false,
                    showCityPickerSheet = false,
                    gpsStatusMessage = "Kota dipilih: ${city.name}"
                )
            }
            recomputeGeodesicAndPrayers(
                lat = city.latitude,
                lon = city.longitude,
                alt = 0.0,
                tzOffset = city.timezoneOffsetHours
            )
        }
    }

    fun saveCustomCoordinates(lat: Double, lon: Double, customLabel: String) {
        val clampedLat = lat.coerceIn(-90.0, 90.0)
        val clampedLon = lon.coerceIn(-180.0, 180.0)
        val tzHours = kotlin.math.round(clampedLon / 15.0)
        val finalLabel = customLabel.ifBlank {
            "Koordinat Kustom (%.3f°, %.3f°)".format(clampedLat, clampedLon)
        }

        viewModelScope.launch {
            preferencesRepository.saveLocation(
                latitude = clampedLat,
                longitude = clampedLon,
                label = finalLabel,
                timezoneOffsetHours = tzHours,
                isLiveGps = false
            )
            _uiState.update {
                it.copy(
                    latitude = clampedLat,
                    longitude = clampedLon,
                    locationLabel = finalLabel,
                    timezoneOffsetHours = tzHours,
                    isLiveGpsActive = false,
                    showCityPickerSheet = false,
                    gpsStatusMessage = "Koordinat kustom diterapkan"
                )
            }
            recomputeGeodesicAndPrayers(
                lat = clampedLat,
                lon = clampedLon,
                alt = 0.0,
                tzOffset = tzHours
            )
        }
    }

    fun selectTab(tab: QiblaNavigationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setManualHeadingAngle(angleDeg: Float) {
        val normalized = ((angleDeg % 360f) + 360f) % 360f
        _uiState.update { current ->
            current.copy(
                isManualRotationOverride = true,
                manualHeadingOffsetDeg = normalized,
                effectiveDeviceHeadingDeg = normalized
            )
        }
    }

    fun snapToQiblaForPreview() {
        val target = _uiState.value.qiblaBearingTrueNorthDeg
        setManualHeadingAngle(target)
    }

    fun resumeLiveSensorTracking() {
        _uiState.update { current ->
            val effective = computeEffectiveHeading(
                rawMagnetic = current.rawMagneticAzimuthDeg,
                declination = current.magneticDeclinationDeg,
                useTrueNorth = current.useTrueNorthCorrection,
                isManual = false,
                manualAngle = 0f,
                hasHardware = current.isHardwareSensorAvailable
            )
            current.copy(
                isManualRotationOverride = false,
                effectiveDeviceHeadingDeg = effective
            )
        }
    }

    fun toggleTrueNorthCorrection(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setTrueNorthCorrection(enabled)
        }
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHapticFeedback(enabled)
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(enabled)
        }
    }

    fun setShowCityPickerSheet(show: Boolean) {
        _uiState.update { it.copy(showCityPickerSheet = show) }
    }

    fun setShowCalibrationSheet(show: Boolean) {
        _uiState.update { it.copy(showCalibrationSheet = show) }
    }
}
