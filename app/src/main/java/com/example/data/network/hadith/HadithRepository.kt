package com.example.data.network.hadith

import com.example.R
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
                description = "Kitab hadits paling otentik dalam Islam setelah Al-Qur'an (Muttafaqun 'Alaih). Menghimpun hadits-hadits shahih dengan sanad tersambung tanpa cacat.",
                imageRes = R.drawable.img_quran_yasin_rehal_1791679991654,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Permulaan Turunnya Wahyu", "Revelation", "Hadits 1 - 7", "Kisah turunnya wahyu pertama kepada Rasulullah ﷺ di Gua Hira & permulaan risalah kenabian."),
                    HadithBookSection(2, "Bab 2: Kitab Keimanan", "Belief", "Hadits 8 - 58", "Hakikat rukun iman, cabang-cabang keimanan, dan tanda-tanda kemanisan iman."),
                    HadithBookSection(3, "Bab 3: Kitab Ilmu Pengetahuan", "Knowledge", "Hadits 59 - 134", "Kewajiban menuntut ilmu syar'i, adab bertanya, dan menjaga amanah ilmu."),
                    HadithBookSection(4, "Bab 4: Kitab Wudhu & Bersuci", "Ablutions (Wudu')", "Hadits 135 - 247", "Tata cara bersuci, syarat sah sholat, membasuh anggota wudhu, dan istinja'."),
                    HadithBookSection(5, "Bab 5: Kitab Mandi Wajib", "Bathing (Ghusl)", "Hadits 248 - 293", "Ketentuan mandi junub dan bersuci dari hadats besar sesuai sunnah Rasulullah ﷺ."),
                    HadithBookSection(8, "Bab 8: Kitab Mendirikan Sholat", "Prayers (Salat)", "Hadits 349 - 520", "Kewajiban mendirikan sholat lima waktu, kiblat, dan keutamaan sholat berjamaah."),
                    HadithBookSection(10, "Bab 10: Kitab Adzan & Panggilan Sholat", "Call to Prayers (Adhan)", "Hadits 603 - 875", "Lafadz seruan adzan, iqamah, dan keutamaan menjawab panggilan muadzin."),
                    HadithBookSection(24, "Bab 24: Kitab Zakat", "Obligatory Charity (Zakat)", "Hadits 1395 - 1512", "Kewajiban menunaikan zakat mal, zakat fitrah, dan pahala bersedekah."),
                    HadithBookSection(30, "Bab 30: Kitab Puasa (Shaum)", "Fasting", "Hadits 1891 - 2007", "Keutamaan puasa Ramadhan, rukyatul hilal, adab sahur, dan berbuka puasa."),
                    HadithBookSection(78, "Bab 78: Kitab Adab & Akhlak Luhur", "Good Manners and Form (Al-Adab)", "Hadits 5970 - 6226", "Adab berbakti pada orang tua, menyambung silaturahmi, dan budi pekerti mulia.")
                )
            ),
            HadithBook(
                id = "muslim",
                title = "Shahih Muslim",
                author = "Imam Muslim bin Al-Hajjaj An-Naisaburi",
                totalHadithEstimate = "± 5.362 Hadits",
                gradeStatus = "🟢 Shahih Mutlak",
                badgeColorHex = 0xFF1B8A5A,
                description = "Kitab hadits shahih terpercaya kedua dengan sistematika penulisan matan dan jalur sanad yang sangat rapi dan komprehensif.",
                imageRes = R.drawable.img_nabawi_green_dome_1791680082165,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Iman & Tauhid", "Faith", "Hadits 1 - 222", "Pondasi rukun iman, keesaan Allah SWT, dan larangan syirik besar."),
                    HadithBookSection(2, "Bab 2: Kitab Thaharah & Bersuci", "Purification", "Hadits 223 - 292", "Kesucian badan dan pakaian, tata cara wudhu, siwak, dan tayamum."),
                    HadithBookSection(4, "Bab 4: Kitab Sholat Fardhu & Sunnah", "Prayers", "Hadits 397 - 684", "Gerakan rukun sholat, bacaan tasyahud, sujud sahwi, dan sholat sunnah."),
                    HadithBookSection(5, "Bab 5: Kitab Masjid & Tempat Ibadah", "Mosques and Places of Prayer", "Hadits 685 - 1160", "Keutamaan memakmurkan masjid dan adab melangkah menuju rumah Allah."),
                    HadithBookSection(12, "Bab 12: Kitab Zakat & Sedekah", "Zakat", "Hadits 979 - 1078", "Hukum nisab zakat, keutamaan tangan di atas, dan sedekah jariyah."),
                    HadithBookSection(13, "Bab 13: Kitab Puasa Ramadhan", "Fasting", "Hadits 1079 - 1170", "Ketentuan ibadah shaum, lailatul qadar, dan puasa enam hari di bulan Syawal."),
                    HadithBookSection(45, "Bab 45: Kitab Kebajikan & Silaturahmi", "Virtue, Good Manners and Joining of Ties", "Hadits 2548 - 2642", "Kewajiban berbuat baik kepada tetangga, saudara seiman, dan kaum dhuafa."),
                    HadithBookSection(48, "Bab 48: Kitab Dzikir, Doa & Taubat", "Dhikr, Supplication, Repentance", "Hadits 2675 - 2735", "Kumpulan wirid harian, doa perlindungan, dan pintu taubat yang senantiasa terbuka.")
                )
            ),
            HadithBook(
                id = "tirmidhi",
                title = "Jami' At-Tirmidzi",
                author = "Imam Abu Isa Muhammad At-Tirmidzi",
                totalHadithEstimate = "± 3.625 Hadits",
                gradeStatus = "🟡 Hasan & Shahih",
                badgeColorHex = 0xFFC07000,
                description = "Pelopor pengelompokan derajat hadits Hasan serta memuat penjelasan ikhtilaf pandangan madzhab fiqih para fuqaha.",
                imageRes = R.drawable.img_golden_mosque_sunset_1791637680885,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci (Thaharah)", "Purification", "Hadits 1 - 148", "Hukum air, tatacara wudhu, dan doa setelah bersuci."),
                    HadithBookSection(2, "Bab 2: Kitab Sholat", "Salat", "Hadits 149 - 451", "Waktu-waktu sholat fardhu, sholat witir, dan bacaan ruku' sujud."),
                    HadithBookSection(3, "Bab 3: Kitab Zakat", "Zakat", "Hadits 617 - 681", "Kadar nisab emas perak, zakat fitrah, dan pahala sedekah tersembunyi."),
                    HadithBookSection(4, "Bab 4: Kitab Puasa", "Fasting", "Hadits 682 - 807", "Rukyatul hilal, puasa sunnah Senin-Kamis, Ayyamul Bidh, dan 'Asyura."),
                    HadithBookSection(27, "Bab 27: Kitab Kebajikan & Adab Sosial", "Righteousness and Relations", "Hadits 1897 - 2037", "Kasih sayang sesama muslim, berbakti kepada orang tua, dan menjauhi permusuhan."),
                    HadithBookSection(48, "Bab 48: Kitab Doa-Doa Mustajab", "Supplications", "Hadits 3370 - 3604", "Doa mustajab pagi-petang, doa keselamatan dunia akhirat, dan zikir penenang hati.")
                )
            ),
            HadithBook(
                id = "abudawud",
                title = "Sunan Abu Dawud",
                author = "Imam Abu Dawud Sulaiman bin Al-Asy'ats",
                totalHadithEstimate = "± 4.590 Hadits",
                gradeStatus = "🟡 Shahih & Hasan",
                badgeColorHex = 0xFFC07000,
                description = "Koleksi hadits fiqih dan hukum syariat paling lengkap yang menjadi rujukan utama para mujtahid dan hakim Islam.",
                imageRes = R.drawable.img_grand_mosque_blue_sky_1791642671931,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci (Thaharah)", "Purification", "Hadits 1 - 390", "Hukum thaharah, adab buang hajat, dan rukun wudhu secara rinci."),
                    HadithBookSection(2, "Bab 2: Kitab Sholat", "Prayer", "Hadits 391 - 1160", "Tata cara sholat berjamaah, imam sholat, dan sholat musafir."),
                    HadithBookSection(9, "Bab 9: Kitab Zakat", "Zakat", "Hadits 1556 - 1700", "Pembagian zakat kepada delapan asnaf dan larangan menimbun harta."),
                    HadithBookSection(14, "Bab 14: Kitab Puasa", "Fasting", "Hadits 2313 - 2476", "Niat puasa sebelum fajar dan kafarat bagi yang membatalkan puasa sengaja."),
                    HadithBookSection(43, "Bab 43: Kitab Adab & Etika Perilaku", "General Behavior (Adab)", "Hadits 4773 - 5274", "Etika bertutur kata, membalas salam, hak tetangga, dan menjauhi ghibah.")
                )
            ),
            HadithBook(
                id = "nasai",
                title = "Sunan An-Nasa'i",
                author = "Imam Ahmad bin Syu'aib An-Nasa'i",
                totalHadithEstimate = "± 5.364 Hadits",
                gradeStatus = "🟡 Shahih & Hasan",
                badgeColorHex = 0xFFC07000,
                description = "Dikenal dengan seleksi perawi yang sangat teliti dan ketat, memuat hadits-hadits ibadah praktis berderajat tinggi.",
                imageRes = R.drawable.img_sujud_grand_mosque_1791732157493,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci", "Purification", "Hadits 1 - 347", "Sunnah-sunnah fitrah, siwak, dan pembatal wudhu."),
                    HadithBookSection(5, "Bab 5: Kitab Sholat & Waktu", "Salah", "Hadits 448 - 687", "Pembagian waktu sholat lima waktu dan peringatan melalaikannya."),
                    HadithBookSection(22, "Bab 22: Kitab Puasa", "Fasting", "Hadits 2090 - 2434", "Keutamaan puasa sunnah, shaum Daud, dan puasa di bulan haram."),
                    HadithBookSection(23, "Bab 23: Kitab Zakat", "Zakat", "Hadits 2435 - 2618", "Syarat wajib zakat dan anjuran memperbanyak infaq di jalan Allah.")
                )
            ),
            HadithBook(
                id = "ibnmajah",
                title = "Sunan Ibnu Majah",
                author = "Imam Muhammad bin Yazid Ibnu Majah",
                totalHadithEstimate = "± 4.331 Hadits",
                gradeStatus = "🟠 Hasan & Dha'if Ringan",
                badgeColorHex = 0xFFD32F2F,
                description = "Salah satu Kutubus Sittah yang sarat dengan riwayat fadhilah amal, tazkiyatun nufus, dan nasihat zuhud kehidupan.",
                imageRes = R.drawable.img_desert_prayer_hero_1791637232343,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Kitab Bersuci & Sunnahnya", "Purification and its Sunnah", "Hadits 267 - 666", "Tata cara bersuci dan doa-doa sunnah dalam thaharah."),
                    HadithBookSection(5, "Bab 5: Kitab Sholat Berjamaah", "Establishing the Prayer", "Hadits 734 - 1432", "Keutamaan sholat berjamaah 27 derajat dan sholat sunnah rawatib."),
                    HadithBookSection(7, "Bab 7: Kitab Puasa", "Fasting", "Hadits 1638 - 1782", "Fadhilah menahan hawa nafsu dan doa mustajab orang yang berpuasa."),
                    HadithBookSection(37, "Bab 37: Kitab Zuhud & Menjaga Hati", "Zuhd", "Hadits 4100 - 4342", "Nasihat menjauhi ketamakan duniawi, mengingat kematian, dan mempersiapkan akhirat.")
                )
            ),
            HadithBook(
                id = "malik",
                title = "Al-Muwatta Imam Malik",
                author = "Imam Malik bin Anas (Madinah)",
                totalHadithEstimate = "± 1.594 Hadits",
                gradeStatus = "🟢 Shahih Ash-Shahih",
                badgeColorHex = 0xFF1B8A5A,
                description = "Kitab hadits dan atsar fiqih tertua karya Imam Darul Hijrah, merangkum tradisi amalan shahih penduduk Madinah.",
                imageRes = R.drawable.img_oasis_wudhu_spring_1791679946849,
                sections = listOf(
                    HadithBookSection(1, "Bab 1: Waktu-Waktu Sholat", "Prayer Times", "Hadits 1 - 31", "Awal dan akhir waktu sholat fardhu dari amalan sahabat di Madinah."),
                    HadithBookSection(2, "Bab 2: Bersuci (Thaharah)", "Purity", "Hadits 32 - 128", "Kesucian air, wudhu Rasulullah ﷺ, dan mandi besar."),
                    HadithBookSection(3, "Bab 3: Sholat Berjamaah", "Prayer", "Hadits 129 - 290", "Ketentuan sholat berjamaah, posisi makmum, dan adab imam."),
                    HadithBookSection(18, "Bab 18: Puasa (Shaum)", "Fasting", "Hadits 629 - 688", "Rukyatul hilal Madinah, keutamaan beriktikaf, dan zakat fitri.")
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
            val fallback = HadithFallbackData.getFallbackHadiths(bookId, sectionNumber)
            if (fallback != null && fallback.isNotEmpty()) {
                memoryCache[cacheKey] = fallback
                Result.success(fallback)
            } else {
                Result.failure(e)
            }
        }
    }
}
