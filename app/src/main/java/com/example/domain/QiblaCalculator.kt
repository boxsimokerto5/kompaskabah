package com.example.domain

import android.hardware.GeomagneticField
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * High-precision astronomical and geodesic calculations for Qibla (Kaaba) direction,
 * great-circle distance, magnetic declination, and daily Islamic prayer times.
 */
object QiblaCalculator {

    // Official WGS84 coordinates of the Holy Kaaba in Masjid al-Haram, Makkah
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0088

    /**
     * Calculates the exact initial Great-Circle bearing (forward azimuth) in degrees [0, 360)
     * from the user's coordinates (lat, lon) to the Holy Kaaba in Makkah.
     *
     * Formula:
     * Δλ = λ_Kaaba - λ_user
     * θ = atan2( sin(Δλ) * cos(φ_Kaaba), cos(φ_user) * sin(φ_Kaaba) - sin(φ_user) * cos(φ_Kaaba) * cos(Δλ) )
     */
    fun calculateQiblaBearing(userLat: Double, userLon: Double): Double {
        val phi1 = Math.toRadians(userLat)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - userLon)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val thetaRadians = atan2(y, x)

        return (Math.toDegrees(thetaRadians) + 360.0) % 360.0
    }

    /**
     * Calculates the Haversine Great-Circle distance in kilometers between the user's
     * coordinates and the Holy Kaaba.
     */
    fun calculateDistanceToKaabaKm(userLat: Double, userLon: Double): Double {
        val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
        val dLon = Math.toRadians(KAABA_LONGITUDE - userLon)
        val rLat1 = Math.toRadians(userLat)
        val rLat2 = Math.toRadians(KAABA_LATITUDE)

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                sin(dLon / 2.0) * sin(dLon / 2.0) * cos(rLat1) * cos(rLat2)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Computes the World Magnetic Model (WMM) magnetic declination in degrees at the given
     * coordinates and altitude for the current timestamp.
     * Adding magnetic declination to Magnetic North azimuth yields True North azimuth.
     */
    fun calculateMagneticDeclination(
        latitude: Double,
        longitude: Double,
        altitudeMeters: Double = 0.0,
        timeMillis: Long = System.currentTimeMillis()
    ): Float {
        return try {
            val geoField = GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitudeMeters.toFloat(),
                timeMillis
            )
            geoField.declination
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * Computes the shortest signed angular difference in degrees [-180, +180]
     * from [currentHeading] to [targetBearing].
     * Positive means turn clockwise (Right / Kanan), Negative means turn counter-clockwise (Left / Kiri).
     */
    fun shortestAngleDelta(currentHeading: Float, targetBearing: Float): Float {
        var delta = (targetBearing - currentHeading) % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        return delta
    }

    /**
     * Circular Low-Pass Filter for smoothing compass heading angles across the 359° -> 0° wrap-around
     * boundary using sin/cos vector interpolation.
     */
    fun smoothAngle(previousAngle: Float, newAngle: Float, alpha: Float): Float {
        val prevRad = Math.toRadians(previousAngle.toDouble())
        val newRad = Math.toRadians(newAngle.toDouble())

        val sinSum = (1.0 - alpha) * sin(prevRad) + alpha * sin(newRad)
        val cosSum = (1.0 - alpha) * cos(prevRad) + alpha * cos(newRad)

        val smoothedDeg = Math.toDegrees(atan2(sinSum, cosSum)).toFloat()
        return (smoothedDeg + 360f) % 360f
    }

    /**
     * Formats cardinal direction in Indonesian (U, TL, T, TG, S, BD, B, BL).
     */
    fun getIndonesianCardinalDirection(degree: Float): String {
        val normalized = ((degree % 360f) + 360f) % 360f
        return when {
            normalized >= 337.5f || normalized < 22.5f -> "U (Utara)"
            normalized < 67.5f -> "TL (Timur Laut)"
            normalized < 112.5f -> "T (Timur)"
            normalized < 157.5f -> "TG (Tenggara)"
            normalized < 202.5f -> "S (Selatan)"
            normalized < 247.5f -> "BD (Barat Daya)"
            normalized < 292.5f -> "B (Barat)"
            else -> "BL (Barat Laut)"
        }
    }

    /**
     * Calculates daily Islamic Prayer Times (Subuh, Terbit, Dzuhur, Ashar, Maghrib, Isya)
     * using standard Kemenag RI / MWL solar position equations (Fajr 20°, Isha 18°, Ihtiyat +2 min).
     */
    fun calculateDailyPrayerTimes(
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        year: Int,
        month: Int,
        day: Int
    ): List<PrayerTimeItem> {
        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)
        val d = jd - 2451545.0

        // Mean anomaly, mean longitude, ecliptic longitude
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2.0 * g))

        val e = 23.439 - 0.00000036 * d
        val ra = datan2(dcos(e) * dsin(l), dcos(l)) / 15.0
        val decl = dasin(dsin(e) * dsin(l))
        val eqt = q / 15.0 - fixHour(ra)

        // Solar Noon (Transit)
        val noon = 12.0 + timezoneOffsetHours - (longitude / 15.0) - eqt

        fun sunAngleTime(angle: Double, isMorning: Boolean): Double {
            val num = -dsin(angle) - dsin(decl) * dsin(latitude)
            val den = dcos(decl) * dcos(latitude)
            val cosVal = (num / den).coerceIn(-1.0, 1.0)
            val t = dacos(cosVal) / 15.0
            return if (isMorning) noon - t else noon + t
        }

        // Asr shadow factor = 1 (Shafi'i / Kemenag standard)
        val asrAngle = -dacot(1.0 + dtan(kotlin.math.abs(latitude - decl)))
        val asrTime = sunAngleTime(asrAngle, isMorning = false)

        // Kemenag RI convention: Fajr = 20 deg below horizon, Sunrise = 0.833 deg, Maghrib = 0.833 deg, Isha = 18 deg
        // Add +2 minutes (0.0333h) Ihtiyat (precautionary margin) for prayer times
        val ihtiyat = 2.0 / 60.0

        val subuhHours = sunAngleTime(20.0, isMorning = true) + ihtiyat
        val terbitHours = sunAngleTime(0.833, isMorning = true)
        val dzuhurHours = noon + ihtiyat
        val asharHours = asrTime + ihtiyat
        val maghribHours = sunAngleTime(0.833, isMorning = false) + ihtiyat
        val isyaHours = sunAngleTime(18.0, isMorning = false) + ihtiyat

        return listOf(
            PrayerTimeItem("Subuh", "الفجر", formatHoursToHm(subuhHours), subuhHours, isObligatory = true),
            PrayerTimeItem("Terbit", "الشروق", formatHoursToHm(terbitHours), terbitHours, isObligatory = false),
            PrayerTimeItem("Dzuhur", "الظهر", formatHoursToHm(dzuhurHours), dzuhurHours, isObligatory = true),
            PrayerTimeItem("Ashar", "العصر", formatHoursToHm(asharHours), asharHours, isObligatory = true),
            PrayerTimeItem("Maghrib", "المغرب", formatHoursToHm(maghribHours), maghribHours, isObligatory = true),
            PrayerTimeItem("Isya", "العشاء", formatHoursToHm(isyaHours), isyaHours, isObligatory = true)
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun formatHoursToHm(hoursInput: Double): String {
        val hNorm = fixHour(hoursInput + 0.5 / 60.0) // round to nearest minute
        val hours = floor(hNorm).toInt()
        val minutes = floor((hNorm - hours) * 60.0).toInt().coerceIn(0, 59)
        return "%02d:%02d".format(hours, minutes)
    }

    private fun fixAngle(a: Double): Double {
        val res = a - 360.0 * floor(a / 360.0)
        return if (res < 0) res + 360.0 else res
    }

    private fun fixHour(a: Double): Double {
        val res = a - 24.0 * floor(a / 24.0)
        return if (res < 0) res + 24.0 else res
    }

    private fun dsin(d: Double) = sin(Math.toRadians(d))
    private fun dcos(d: Double) = cos(Math.toRadians(d))
    private fun dtan(d: Double) = tan(Math.toRadians(d))
    private fun dasin(x: Double) = Math.toDegrees(asin(x.coerceIn(-1.0, 1.0)))
    private fun dacos(x: Double) = Math.toDegrees(acos(x.coerceIn(-1.0, 1.0)))
    private fun datan2(y: Double, x: Double) = Math.toDegrees(atan2(y, x))
    private fun dacot(x: Double) = Math.toDegrees(atan2(1.0, x))
}

data class PrayerTimeItem(
    val nameId: String,
    val arabicName: String,
    val formattedTime: String,
    val decimalHours: Double,
    val isObligatory: Boolean
)
