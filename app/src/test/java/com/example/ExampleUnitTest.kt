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

    @Test
    fun haditsCatalog_containsClassifiedHaditsWithAllGradesAndClarifications() {
        val items = com.example.domain.hadits.HaditsData.items
        assertTrue("Hadits catalog should have at least 20 items", items.size >= 20)

        val grades = items.map { it.grade }.toSet()
        assertTrue(grades.contains(com.example.domain.hadits.HaditsGrade.SHAHIH))
        assertTrue(grades.contains(com.example.domain.hadits.HaditsGrade.HASAN))
        assertTrue(grades.contains(com.example.domain.hadits.HaditsGrade.DHAIF))
        assertTrue(grades.contains(com.example.domain.hadits.HaditsGrade.POPULER_KLARIFIKASI))

        items.forEach { hadits ->
            assertTrue(hadits.title.isNotBlank())
            assertTrue(hadits.arabic.isNotBlank())
            assertTrue(hadits.latin.isNotBlank())
            assertTrue(hadits.translation.isNotBlank())
            assertTrue(hadits.narrator.isNotBlank())
            assertTrue(hadits.faedah.isNotBlank())
        }

        val dhaifItems = items.filter { it.grade == com.example.domain.hadits.HaditsGrade.DHAIF }
        assertTrue(dhaifItems.isNotEmpty())
        dhaifItems.forEach {
            assertTrue("Dha'if hadits should provide clarification or authentic replacement", it.clarification != null)
        }
    }

    @Test
    fun hadithRepository_getAvailableBooks_returnsCanonicalCollections() {
        val repo = com.example.data.network.hadith.HadithRepositoryImpl()
        val books = repo.getAvailableBooks()
        assertTrue("Should have canonical books", books.size >= 7)

        val bukhari = books.find { it.id == "bukhari" }
        assertTrue(bukhari != null)
        assertEquals("Shahih Al-Bukhari", bukhari?.title)
        assertTrue(bukhari!!.sections.isNotEmpty())

        val muslim = books.find { it.id == "muslim" }
        assertTrue(muslim != null)
        assertEquals("Shahih Muslim", muslim?.title)
    }

    @Test
    fun hadithRepository_fetchSectionHadiths_parsesAndPairsArabicAndIndonesian() = kotlinx.coroutines.test.runTest {
        val fakeApiService = object : com.example.data.network.hadith.HadithApiService {
            override suspend fun getArabicSection(book: String, section: Int): com.example.data.network.hadith.HadithSectionResponse {
                return com.example.data.network.hadith.HadithSectionResponse(
                    metadata = com.example.data.network.hadith.HadithMetadataResponse(name = "Test Book"),
                    hadiths = listOf(
                        com.example.data.network.hadith.HadithRawItem(hadithnumber = 1, text = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ"),
                        com.example.data.network.hadith.HadithRawItem(hadithnumber = 2, text = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ")
                    )
                )
            }

            override suspend fun getIndonesianSection(book: String, section: Int): com.example.data.network.hadith.HadithSectionResponse {
                return com.example.data.network.hadith.HadithSectionResponse(
                    metadata = com.example.data.network.hadith.HadithMetadataResponse(name = "Test Book"),
                    hadiths = listOf(
                        com.example.data.network.hadith.HadithRawItem(hadithnumber = 1, text = "Sesungguhnya setiap amalan bergantung pada niatnya."),
                        com.example.data.network.hadith.HadithRawItem(hadithnumber = 2, text = "Islam dibangun di atas lima perkara.")
                    )
                )
            }
        }

        val repository = com.example.data.network.hadith.HadithRepositoryImpl(fakeApiService)
        val result = repository.fetchSectionHadiths("bukhari", 1)

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        assertEquals(2, items.size)

        val first = items[0]
        assertEquals(1, first.number)
        assertEquals("إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ", first.arabicText)
        assertEquals("Sesungguhnya setiap amalan bergantung pada niatnya.", first.indonesianText)
        assertEquals("Shahih Al-Bukhari", first.bookName)

        val second = items[1]
        assertEquals(2, second.number)
        assertEquals("بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ", second.arabicText)
        assertEquals("Islam dibangun di atas lima perkara.", second.indonesianText)
    }
}
