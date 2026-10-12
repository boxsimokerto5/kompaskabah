package com.example.data.network.hadith

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

interface HadithRepository {
    fun getAvailableBooks(): List<HadithBook>
    suspend fun fetchSectionHadiths(bookId: String, sectionNumber: Int): Result<List<HadithParsedItem>>
}

class HadithRepositoryImpl(
    private val apiService: HadithApiService = HadithNetworkClient.apiService
) : HadithRepository {

    // In-memory cache for loaded sections to prevent redundant network calls
    private val memoryCache = ConcurrentHashMap<String, List<HadithParsedItem>>()

    override fun getAvailableBooks(): List<HadithBook> {
        return listOf(
            HadithBook(
                id = "bukhari",
                title = "Shahih Al-Bukhari",
                author = "Imam Muhammad bin Ismail Al-Bukhari",
                totalHadithEstimate = "± 7.008 Hadits",
                gradeStatus = "🟢 Shahih Mutlak",
                badgeColorHex = 0xFF1B8A5A,
                description = "Kitab hadits paling otentik dalam Islam setelah Al-Qur'an (Muttafaqun 'Alaih).",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Permulaan Turunnya Wahyu", "Revelation"),
                    HadithBookSection(2, "Bab 2: Kitab Keimanan", "Belief"),
                    HadithBookSection(3, "Bab 3: Kitab Ilmu Pengetahuan", "Knowledge"),
                    HadithBookSection(4, "Bab 4: Kitab Wudhu & Bersuci", "Ablutions (Wudu')"),
                    HadithBookSection(5, "Bab 5: Kitab Mandi Wajib", "Bathing (Ghusl)"),
                    HadithBookSection(8, "Bab 8: Kitab Mendirikan Sholat", "Prayers (Salat)"),
                    HadithBookSection(10, "Bab 10: Kitab Adzan & Panggilan Sholat", "Call to Prayers (Adhan)"),
                    HadithBookSection(24, "Bab 24: Kitab Zakat", "Obligatory Charity (Zakat)"),
                    HadithBookSection(30, "Bab 30: Kitab Puasa (Shaum)", "Fasting"),
                    HadithBookSection(78, "Bab 78: Kitab Adab & Akhlak Luhur", "Good Manners and Form (Al-Adab)")
                )
            ),
            HadithBook(
                id = "muslim",
                title = "Shahih Muslim",
                author = "Imam Muslim bin Al-Hajjaj An-Naisaburi",
                totalHadithEstimate = "± 5.362 Hadits",
                gradeStatus = "🟢 Shahih Mutlak",
                badgeColorHex = 0xFF1B8A5A,
                description = "Kitab hadits terpercaya kedua dengan sistematika penulisan sanad yang sangat rapi.",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Iman & Tauhid", "Faith"),
                    HadithBookSection(2, "Bab 2: Kitab Thaharah & Bersuci", "Purification"),
                    HadithBookSection(4, "Bab 4: Kitab Sholat Fardhu & Sunnah", "Prayers"),
                    HadithBookSection(5, "Bab 5: Kitab Masjid & Tempat Ibadah", "Mosques and Places of Prayer"),
                    HadithBookSection(12, "Bab 12: Kitab Zakat & Sedekah", "Zakat"),
                    HadithBookSection(13, "Bab 13: Kitab Puasa Ramadhan", "Fasting"),
                    HadithBookSection(45, "Bab 45: Kitab Kebajikan & Silaturahmi", "Virtue, Good Manners and Joining of Ties"),
                    HadithBookSection(48, "Bab 48: Kitab Dzikir, Doa & Taubat", "Dhikr, Supplication, Repentance")
                )
            ),
            HadithBook(
                id = "tirmidhi",
                title = "Jami' At-Tirmidzi",
                author = "Imam Abu Isa Muhammad At-Tirmidzi",
                totalHadithEstimate = "± 3.625 Hadits",
                gradeStatus = "🟡 Hasan & Shahih",
                badgeColorHex = 0xFFC07000,
                description = "Pelopor klasifikasi derajat hadits Hasan dan penjelasan perbedaan pandangan fiqih ulama.",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci (Thaharah)", "Purification"),
                    HadithBookSection(2, "Bab 2: Kitab Sholat", "Salat"),
                    HadithBookSection(3, "Bab 3: Kitab Zakat", "Zakat"),
                    HadithBookSection(4, "Bab 4: Kitab Puasa", "Fasting"),
                    HadithBookSection(27, "Bab 27: Kitab Kebajikan & Adab Sosial", "Righteousness and Relations"),
                    HadithBookSection(48, "Bab 48: Kitab Doa-Doa Mustajab", "Supplications")
                )
            ),
            HadithBook(
                id = "abudawud",
                title = "Sunan Abu Dawud",
                author = "Imam Abu Dawud Sulaiman bin Al-Asy'ats",
                totalHadithEstimate = "± 4.590 Hadits",
                gradeStatus = "🟡 Shahih & Hasan",
                badgeColorHex = 0xFFC07000,
                description = "Rujukan utama hadits-hadits hukum fiqih praktis dalam kehidupan sehari-hari.",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci (Thaharah)", "Purification"),
                    HadithBookSection(2, "Bab 2: Kitab Sholat", "Prayer"),
                    HadithBookSection(9, "Bab 9: Kitab Zakat", "Zakat"),
                    HadithBookSection(14, "Bab 14: Kitab Puasa", "Fasting"),
                    HadithBookSection(43, "Bab 43: Kitab Adab & Etika Perilaku", "General Behavior (Adab)")
                )
            ),
            HadithBook(
                id = "nasai",
                title = "Sunan An-Nasa'i",
                author = "Imam Ahmad bin Syu'aib An-Nasa'i",
                totalHadithEstimate = "± 5.364 Hadits",
                gradeStatus = "🟡 Shahih & Hasan",
                badgeColorHex = 0xFFC07000,
                description = "Sangat ketat dalam menyeleksi para perawi hadits (syuruth ar-rijal).",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci", "Purification"),
                    HadithBookSection(5, "Bab 5: Kitab Sholat & Waktu", "Salah"),
                    HadithBookSection(22, "Bab 22: Kitab Puasa", "Fasting"),
                    HadithBookSection(23, "Bab 23: Kitab Zakat", "Zakat")
                )
            ),
            HadithBook(
                id = "ibnmajah",
                title = "Sunan Ibnu Majah",
                author = "Imam Muhammad bin Yazid Ibnu Majah",
                totalHadithEstimate = "± 4.331 Hadits",
                gradeStatus = "🟠 Hasan & Dha'if Ringan",
                badgeColorHex = 0xFFD32F2F,
                description = "Salah satu Kutubus Sittah dengan banyak riwayat fadhilah amal dan nasihat zuhud.",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci & Sunnahnya", "Purification and its Sunnah"),
                    HadithBookSection(5, "Bab 5: Kitab Sholat Berjamaah", "Establishing the Prayer"),
                    HadithBookSection(7, "Bab 7: Kitab Puasa", "Fasting"),
                    HadithBookSection(37, "Bab 37: Kitab Zuhud & Menjaga Hati", "Zuhd")
                )
            ),
            HadithBook(
                id = "malik",
                title = "Al-Muwatta Imam Malik",
                author = "Imam Malik bin Anas (Madinah)",
                totalHadithEstimate = "± 1.594 Hadits",
                gradeStatus = "🟢 Shahih Ash-Shahih",
                badgeColorHex = 0xFF1B8A5A,
                description = "Kitab hadits dan atsar fiqih tertua karya Imam Darul Hijrah Madinah Munawwarah.",
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Waktu-Waktu Sholat", "Prayer Times"),
                    HadithBookSection(2, "Bab 2: Bersuci (Thaharah)", "Purity"),
                    HadithBookSection(3, "Bab 3: Sholat Berjamaah", "Prayer"),
                    HadithBookSection(18, "Bab 18: Puasa (Shaum)", "Fasting")
                )
            )
        )
    }

    override suspend fun fetchSectionHadiths(
        bookId: String,
        sectionNumber: Int
    ): Result<List<HadithParsedItem>> = withContext(Dispatchers.IO) {
        val cacheKey = "$bookId-$sectionNumber"
        val cached = memoryCache[cacheKey]
        if (cached != null && cached.isNotEmpty()) {
            return@withContext Result.success(cached)
        }

        try {
            // Concurrent fetching of Arabic and Indonesian editions via Retrofit
            val arabicDeferred = async { apiService.getArabicSection(bookId, sectionNumber) }
            val indonesianDeferred = async { apiService.getIndonesianSection(bookId, sectionNumber) }

            val arabicRes = arabicDeferred.await()
            val indonesianRes = indonesianDeferred.await()

            val bookInfo = getAvailableBooks().find { it.id == bookId }
            val bookName = bookInfo?.title ?: "Hadits $bookId"
            val sectionName = bookInfo?.sections?.find { it.sectionNumber == sectionNumber }?.titleId
                ?: "Bab $sectionNumber"
            val gradeTag = bookInfo?.gradeStatus ?: "Shahih/Hasan"

            // Index Indonesian translations by hadithnumber for O(1) matching
            val indoMap = indonesianRes.hadiths.associateBy { it.hadithnumber }

            val parsedList = arabicRes.hadiths.map { rawArab ->
                val matchingIndo = indoMap[rawArab.hadithnumber]
                val indoTranslation = matchingIndo?.text?.trim().orEmpty().ifEmpty {
                    "Terjemahan sedang diproses atau tidak tersedia untuk nomor ini."
                }

                HadithParsedItem(
                    number = rawArab.hadithnumber,
                    bookId = bookId,
                    bookName = bookName,
                    sectionNumber = sectionNumber,
                    sectionName = sectionName,
                    arabicText = rawArab.text.trim(),
                    indonesianText = indoTranslation,
                    gradeTag = gradeTag
                )
            }

            if (parsedList.isNotEmpty()) {
                memoryCache[cacheKey] = parsedList
            }

            Result.success(parsedList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
