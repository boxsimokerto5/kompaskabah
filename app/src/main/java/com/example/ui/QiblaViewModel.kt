package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CompassSensorManager
import com.example.data.CompassSensorReading
import com.example.data.LocationTracker
import com.example.data.MuezzinVoiceOption
import com.example.data.NearbyMosqueItem
import com.example.data.NearbyMosquesRepository
import com.example.data.QiblaPreferencesRepository
import com.example.data.SensorAccuracyLevel
import com.example.data.StoredQiblaPreferences
import com.example.domain.PrayerTimeItem
import com.example.domain.QiblaCalculator
import com.example.service.AdhanAlarmScheduler
import com.example.service.AdhanForegroundService
import com.example.service.AdhanNotificationHelper
import com.example.service.AdhanPlaybackState
import com.example.service.ScheduledPrayerAlarmSummary
import com.example.widget.QiblaPrayerWidgetProvider
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
    ARROW_POINTER,
    PRAYER_TIMES,
    AMALAN,
    NEARBY_MOSQUES,
    SENSOR_DIAGNOSTICS
}

data class QiblaUiState(
    val selectedTab: QiblaNavigationTab = QiblaNavigationTab.COMPASS,
    val latitude: Double = -6.1702,
    val longitude: Double = 106.8314,
    val altitudeMeters: Double = 0.0,
    val locationLabel: String = "Mendeteksi Lokasi GPS…",
    val timezoneOffsetHours: Double = (TimeZone.getDefault().rawOffset / 3600000.0),
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

    // Adhan & Prayer Schedule Settings
    val adhanAutoEnabled: Boolean = true,
    val selectedMuezzin: MuezzinVoiceOption = MuezzinVoiceOption.MAKKAH,
    val enabledAdhanPrayers: Set<String> = StoredQiblaPreferences.DEFAULT_ADHAN_PRAYERS,
    val adhanPlaybackState: AdhanPlaybackState = AdhanPlaybackState(),
    val hasNotificationPermission: Boolean = true,
    val scheduledAlarms: List<ScheduledPrayerAlarmSummary> = emptyList(),

    // Prayer Times
    val prayerTimes: List<PrayerTimeItem> = emptyList(),
    val nextPrayerName: String = "Maghrib",
    val nextPrayerTimeFormatted: String = "17:54",
    val nextPrayerCountdownLabel: String = "",

    // Nearby Mosques (Automatic GPS Search)
    val nearbyMosques: List<NearbyMosqueItem> = emptyList(),
    val isLoadingMosques: Boolean = false,
    val mosquesErrorMessage: String? = null
) {
    val angleDeltaToQiblaDeg: Float
        get() = QiblaCalculator.shortestAngleDelta(effectiveDeviceHeadingDeg, qiblaBearingTrueNorthDeg)

    val isAlignedWithKaaba: Boolean
        get() = abs(angleDeltaToQiblaDeg) <= 3.0f

    val isNearAlignmentWithKaaba: Boolean
        get() = abs(angleDeltaToQiblaDeg) <= 12.0f

    val hasMagneticInterference: Boolean
        get() = isHardwareSensorAvailable && (magneticFieldStrengthUt < 18f || magneticFieldStrengthUt > 85f)

    val isDeviceTilted: Boolean
        get() = isHardwareSensorAvailable && !isManualRotationOverride && (abs(pitchDeg) > 32f || abs(rollDeg) > 32f)
}

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val compassSensorManager = CompassSensorManager(application)
    private val locationTracker = LocationTracker(application)
    private val preferencesRepository = QiblaPreferencesRepository(application)
    private val nearbyMosquesRepository = NearbyMosquesRepository()

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var gpsStreamJob: Job? = null
    private var lastMosqueFetchLat: Double = Double.NaN
    private var lastMosqueFetchLon: Double = Double.NaN

    init {
        AdhanNotificationHelper.ensureNotificationChannel(appContext)
        val initialPerm = locationTracker.hasLocationPermission()
        val notifPerm = AdhanNotificationHelper.hasNotificationPermission(appContext)
        _uiState.update {
            it.copy(
                hasLocationPermission = initialPerm,
                hasNotificationPermission = notifPerm,
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
        observeAdhanPlaybackState()
        refreshNearbyMosques(force = true)

        if (initialPerm) {
            refreshLiveGpsLocation()
        }
    }

    private fun observeAdhanPlaybackState() {
        viewModelScope.launch {
            AdhanForegroundService.playbackState.collectLatest { playback ->
                _uiState.update { it.copy(adhanPlaybackState = playback) }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.preferencesFlow.collectLatest { prefs ->
                _uiState.update { current ->
                    val effectiveHeading = computeEffectiveHeading(
                        rawMagnetic = current.rawMagneticAzimuthDeg,
                        declination = current.magneticDeclinationDeg,
                        useTrueNorth = prefs.useTrueNorthCorrection
                    )
                    val latToUse = if (current.isLiveGpsActive) current.latitude else prefs.latitude
                    val lonToUse = if (current.isLiveGpsActive) current.longitude else prefs.longitude
                    val labelToUse = if (current.isLiveGpsActive) {
                        current.locationLabel
                    } else if (prefs.isUsingLiveGps && prefs.locationLabel.isNotBlank()) {
                        prefs.locationLabel
                    } else {
                        "Menunggu Sinyal GPS…"
                    }

                    current.copy(
                        latitude = latToUse,
                        longitude = lonToUse,
                        locationLabel = labelToUse,
                        timezoneOffsetHours = prefs.timezoneOffsetHours,
                        useTrueNorthCorrection = prefs.useTrueNorthCorrection,
                        hapticFeedbackEnabled = prefs.hapticFeedbackEnabled,
                        darkThemeEnabled = prefs.darkThemeEnabled,
                        adhanAutoEnabled = prefs.adhanAutoEnabled,
                        selectedMuezzin = prefs.selectedMuezzin,
                        enabledAdhanPrayers = prefs.enabledAdhanPrayers,
                        effectiveDeviceHeadingDeg = effectiveHeading
                    )
                }

                val latest = _uiState.value
                recomputeGeodesicAndPrayers(
                    lat = latest.latitude,
                    lon = latest.longitude,
                    alt = latest.altitudeMeters,
                    tzOffset = latest.timezoneOffsetHours
                )
                val scheduled = AdhanAlarmScheduler.scheduleDailyAdhanAlarms(
                    context = appContext,
                    latitude = latest.latitude,
                    longitude = latest.longitude,
                    timezoneOffsetHours = latest.timezoneOffsetHours,
                    adhanAutoEnabled = prefs.adhanAutoEnabled,
                    enabledPrayers = prefs.enabledAdhanPrayers,
                    muezzin = prefs.selectedMuezzin,
                    locationLabel = latest.locationLabel
                )
                _uiState.update {
                    it.copy(
                        scheduledAlarms = scheduled,
                        hasNotificationPermission = AdhanNotificationHelper.hasNotificationPermission(appContext)
                    )
                }
                QiblaPrayerWidgetProvider.updateAllWidgets(appContext)
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
                        useTrueNorth = current.useTrueNorthCorrection
                    )
                    current.copy(
                        rawMagneticAzimuthDeg = reading.magneticAzimuthDeg,
                        effectiveDeviceHeadingDeg = effectiveHeading,
                        pitchDeg = reading.pitchDeg,
                        rollDeg = reading.rollDeg,
                        magneticFieldStrengthUt = reading.magneticFieldStrengthUt,
                        sensorAccuracy = reading.accuracy,
                        sensorSourceName = reading.sensorSourceName,
                        isHardwareSensorAvailable = reading.isHardwareSensorAvailable,
                        isManualRotationOverride = false
                    )
                }
            }
        }
    }

    private fun computeEffectiveHeading(
        rawMagnetic: Float,
        declination: Float,
        useTrueNorth: Boolean
    ): Float {
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
                useTrueNorth = current.useTrueNorthCorrection
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
        val notifPerm = AdhanNotificationHelper.hasNotificationPermission(appContext)
        _uiState.update {
            it.copy(
                hasLocationPermission = granted,
                hasNotificationPermission = notifPerm
            )
        }
        if (granted) {
            refreshLiveGpsLocation()
        } else {
            _uiState.update {
                it.copy(
                    isFetchingGps = false,
                    locationLabel = "Izin Lokasi GPS Diperlukan",
                    gpsStatusMessage = "Ketuk tombol GPS untuk mengizinkan akses lokasi otomatis"
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
                val formattedCoordsFallback = "GPS (%.4f°, %.4f°)".format(coords.latitude, coords.longitude)
                val label = coords.addressLabel ?: formattedCoordsFallback
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
                        gpsStatusMessage = "GPS Real-Time Aktif (±${coords.accuracyMeters.toInt()}m)"
                    )
                }
                recomputeGeodesicAndPrayers(
                    lat = coords.latitude,
                    lon = coords.longitude,
                    alt = coords.altitudeMeters,
                    tzOffset = tzHours
                )
                val scheduled = AdhanAlarmScheduler.scheduleDailyAdhanAlarms(
                    context = appContext,
                    latitude = coords.latitude,
                    longitude = coords.longitude,
                    timezoneOffsetHours = tzHours,
                    adhanAutoEnabled = _uiState.value.adhanAutoEnabled,
                    enabledPrayers = _uiState.value.enabledAdhanPrayers,
                    muezzin = _uiState.value.selectedMuezzin,
                    locationLabel = label
                )
                _uiState.update { it.copy(scheduledAlarms = scheduled) }
                refreshNearbyMosques(force = true)
            } else {
                _uiState.update {
                    it.copy(
                        isFetchingGps = false,
                        gpsStatusMessage = "Mencari sinyal satelit GPS…"
                    )
                }
            }

            startContinuousGpsStream()
        }
    }

    private fun startContinuousGpsStream() {
        gpsStreamJob?.cancel()
        gpsStreamJob = viewModelScope.launch {
            locationTracker.observeLocationUpdates().collectLatest { coords ->
                val tzHours = TimeZone.getDefault().rawOffset / 3600000.0
                val formattedCoordsFallback = "GPS (%.4f°, %.4f°)".format(coords.latitude, coords.longitude)
                val resolvedName = locationTracker.resolveCityName(coords.latitude, coords.longitude)
                    ?: _uiState.value.locationLabel.takeIf { !it.startsWith("Men") }
                    ?: formattedCoordsFallback

                preferencesRepository.saveLocation(
                    latitude = coords.latitude,
                    longitude = coords.longitude,
                    label = resolvedName,
                    timezoneOffsetHours = tzHours,
                    isLiveGps = true
                )

                _uiState.update {
                    it.copy(
                        latitude = coords.latitude,
                        longitude = coords.longitude,
                        altitudeMeters = coords.altitudeMeters,
                        locationLabel = resolvedName,
                        isLiveGpsActive = true,
                        isFetchingGps = false,
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
                refreshNearbyMosques(force = false)
            }
        }
    }

    fun refreshNearbyMosques(force: Boolean = true) {
        val lat = _uiState.value.latitude
        val lon = _uiState.value.longitude

        if (!force && !lastMosqueFetchLat.isNaN() && !lastMosqueFetchLon.isNaN()) {
            val movedMeters = QiblaCalculator.calculateDistanceBetweenPointsMeters(
                lat1 = lastMosqueFetchLat,
                lon1 = lastMosqueFetchLon,
                lat2 = lat,
                lon2 = lon
            )
            if (movedMeters < 200.0 && _uiState.value.nearbyMosques.isNotEmpty()) {
                return
            }
        }

        lastMosqueFetchLat = lat
        lastMosqueFetchLon = lon

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMosques = true, mosquesErrorMessage = null) }
            val result = nearbyMosquesRepository.fetchNearbyMosques(lat, lon)
            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        nearbyMosques = list,
                        isLoadingMosques = false,
                        mosquesErrorMessage = if (list.isEmpty()) {
                            "Tidak ditemukan data masjid dalam radius 6 km dari titik ini."
                        } else {
                            null
                        }
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoadingMosques = false,
                        mosquesErrorMessage = "Menunggu koneksi internet untuk memuat daftar masjid terdekat."
                    )
                }
            }
        }
    }

    fun selectTab(tab: QiblaNavigationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == QiblaNavigationTab.NEARBY_MOSQUES && _uiState.value.nearbyMosques.isEmpty()) {
            refreshNearbyMosques(force = true)
        }
    }

    fun setManualHeadingAngle(angleDeg: Float) {
        // No-op: Pure physical sensor mode is enforced
        resumeLiveSensorTracking()
    }

    fun snapToQiblaForPreview() {
        // No-op: Pure physical sensor mode is enforced
        resumeLiveSensorTracking()
    }

    fun resumeLiveSensorTracking() {
        _uiState.update { current ->
            val effective = computeEffectiveHeading(
                rawMagnetic = current.rawMagneticAzimuthDeg,
                declination = current.magneticDeclinationDeg,
                useTrueNorth = current.useTrueNorthCorrection
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

    fun toggleAdhanAutoEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAdhanAutoEnabled(enabled)
        }
    }

    fun selectMuezzin(muezzin: MuezzinVoiceOption) {
        viewModelScope.launch {
            preferencesRepository.setSelectedMuezzin(muezzin)
            if (_uiState.value.adhanPlaybackState.isPlaying) {
                AdhanForegroundService.startAdhan(
                    context = appContext,
                    prayerName = _uiState.value.adhanPlaybackState.prayerName.ifBlank { "Uji Suara Adzan" },
                    muezzin = muezzin,
                    isPreview = true
                )
            }
        }
    }

    fun togglePrayerAdhanAlarm(prayerName: String) {
        viewModelScope.launch {
            preferencesRepository.togglePrayerAdhanEnabled(prayerName)
        }
    }

    fun testOrStopAdhanPlayback(prayerName: String = "Uji Kumandang Adzan") {
        val currentPlayback = _uiState.value.adhanPlaybackState
        if (currentPlayback.isPlaying) {
            AdhanForegroundService.stopAdhan(appContext)
            AdhanNotificationHelper.cancelAllPrayerPushNotifications(appContext)
        } else {
            val state = _uiState.value
            AdhanAlarmScheduler.triggerTestAlarmNotificationNow(
                context = appContext,
                prayerName = prayerName,
                prayerTimeFormatted = state.nextPrayerTimeFormatted,
                locationLabel = state.locationLabel,
                muezzin = state.selectedMuezzin
            )
            AdhanNotificationHelper.showPrayerTimePushNotification(
                context = appContext,
                prayerName = prayerName,
                prayerTimeFormatted = state.nextPrayerTimeFormatted,
                locationLabel = state.locationLabel,
                muezzin = state.selectedMuezzin,
                isTestNotification = true
            )
            AdhanForegroundService.startAdhan(
                context = appContext,
                prayerName = prayerName,
                muezzin = state.selectedMuezzin,
                isPreview = true
            )
        }
    }

    fun stopAdhanPlayback() {
        AdhanForegroundService.stopAdhan(appContext)
        AdhanNotificationHelper.cancelAllPrayerPushNotifications(appContext)
    }
}
