package com.example

import com.example.domain.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun calculateQiblaBearing_fromJakarta_returnsAccurateAzimuth() {
        // Jakarta (Masjid Istiqlal): -6.1702, 106.8314 -> Qibla is ~295.15° (West-Northwest)
        val bearing = QiblaCalculator.calculateQiblaBearing(-6.1702, 106.8314)
        assertEquals(295.15, bearing, 0.25)
    }

    @Test
    fun calculateDistanceToKaaba_fromJakarta_returnsAccurateKilometers() {
        val distanceKm = QiblaCalculator.calculateDistanceToKaabaKm(-6.1702, 106.8314)
        assertTrue(distanceKm in 7900.0..7940.0)
    }

    @Test
    fun shortestAngleDelta_handlesWrapAroundCorrectly() {
        val deltaRight = QiblaCalculator.shortestAngleDelta(350f, 10f)
        assertEquals(20f, deltaRight, 0.01f)

        val deltaLeft = QiblaCalculator.shortestAngleDelta(10f, 350f)
        assertEquals(-20f, deltaLeft, 0.01f)
    }

    @Test
    fun adaptiveLowPassFilterAngle_smoothsWrapAroundAndSuppressesMicroJitter() {
        // Micro-jitter (< 0.35 deg) should be heavily damped so the needle does not vibrate
        val steady = QiblaCalculator.adaptiveLowPassFilterAngle(295.0f, 295.2f, 0.02f)
        assertTrue(steady in 295.0f..295.02f)

        // Wrap-around across 359° -> 1° should interpolate across 0° smoothly without jumping backwards
        val wrapped = QiblaCalculator.adaptiveLowPassFilterAngle(359.0f, 3.0f, 0.02f)
        assertTrue(wrapped > 359.0f || wrapped < 1.0f)
    }

    @Test
    fun calculateDailyPrayerTimes_returnsSixScheduleItems() {
        val prayers = QiblaCalculator.calculateDailyPrayerTimes(
            latitude = -6.1702,
            longitude = 106.8314,
            timezoneOffsetHours = 7.0,
            year = 2026,
            month = 10,
            day = 10
        )
        assertEquals(6, prayers.size)
        assertEquals("Subuh", prayers.first().nameId)
        assertEquals("Isya", prayers.last().nameId)
    }

    @Test
    fun islamicCalendarHelper_convertsOctober2026Accurately() {
        val hijri11Oct = com.example.domain.calendar.IslamicCalendarHelper.gregorianToHijri(2026, 10, 11)
        assertEquals(1448, hijri11Oct.year)
        // Check day and month
        assertTrue(hijri11Oct.month in 4..5)
        val grid = com.example.domain.calendar.IslamicCalendarHelper.getMonthCalendarGrid(2026, 10, 2026, 10, 11)
        assertTrue(grid.size >= 35)
        val todayCell = grid.find { it.isToday }
        assertTrue(todayCell != null)
        assertEquals(11, todayCell?.gregorianDay)
    }
}
