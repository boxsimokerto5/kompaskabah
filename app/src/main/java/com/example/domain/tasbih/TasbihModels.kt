package com.example.domain.tasbih

import androidx.compose.ui.graphics.Color

data class TasbihDzikirPreset(
    val id: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val defaultTarget: Int,
    val fadhilah: String
)

enum class TasbihBeadTheme(
    val displayName: String,
    val baseColor: Color,
    val highlightColor: Color,
    val shadowColor: Color,
    val stringColor: Color,
    val accentGold: Color = Color(0xFFFFD700)
) {
    AMBER(
        displayName = "Kayu Gaharu Emas",
        baseColor = Color(0xFFD99B32),
        highlightColor = Color(0xFFFFF1C2),
        shadowColor = Color(0xFF6B4307),
        stringColor = Color(0xFF8D531B)
    ),
    EMERALD(
        displayName = "Batu Giok Zamrud",
        baseColor = Color(0xFF1E824C),
        highlightColor = Color(0xFF8CE3AA),
        shadowColor = Color(0xFF0D4524),
        stringColor = Color(0xFF1B4F32)
    ),
    PEARL(
        displayName = "Mutiara Al-Quds",
        baseColor = Color(0xFFE2E7ED),
        highlightColor = Color(0xFFFFFFFF),
        shadowColor = Color(0xFF95A0AA),
        stringColor = Color(0xFF78909C)
    ),
    RUBY(
        displayName = "Batu Akik Delima",
        baseColor = Color(0xFFC62828),
        highlightColor = Color(0xFFFF8A80),
        shadowColor = Color(0xFF630B0B),
        stringColor = Color(0xFF880E4F)
    ),
    OBSIDIAN(
        displayName = "Pualam Onyx Hitam",
        baseColor = Color(0xFF2B303A),
        highlightColor = Color(0xFF8C98A8),
        shadowColor = Color(0xFF101318),
        stringColor = Color(0xFF455A64)
    )
}

data class TasbihState(
    val currentCount: Int = 0,
    val targetCount: Int = 33, // 33, 99, 100, 1000, 0 = Bebas
    val totalCount: Long = 0L,
    val lapCount: Int = 1,
    val selectedDzikirIndex: Int = 0,
    val beadTheme: TasbihBeadTheme = TasbihBeadTheme.AMBER,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoAdvanceTarget: Boolean = true,
    val showTargetCompletedDialog: Boolean = false
)

object TasbihPresets {
    val presets: List<TasbihDzikirPreset> = listOf(
        TasbihDzikirPreset(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            latin = "Subhanallah",
            translation = "Maha Suci Allah",
            defaultTarget = 33,
            fadhilah = "Dzikir tasbih ba\'da sholat fardhu menghapus dosa-dosa dan menenteramkan qolbu."
        ),
        TasbihDzikirPreset(
            id = "alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            latin = "Alhamdulillah",
            translation = "Segala puji bagi Allah",
            defaultTarget = 33,
            fadhilah = "Ungkapan rasa syukur paling utama yang memberatkan timbangan kebaikan di akhirat."
        ),
        TasbihDzikirPreset(
            id = "allahu_akbar",
            arabic = "اللَّهُ أَكْبَرُ",
            latin = "Allahu Akbar",
            translation = "Allah Maha Besar",
            defaultTarget = 33,
            fadhilah = "Meneguhkan keagungan Allah di atas segala urusan dunia dan kekhawatiran hidup."
        ),
        TasbihDzikirPreset(
            id = "astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ",
            latin = "Astaghfirullahal 'Azhim",
            translation = "Aku memohon ampun kepada Allah Yang Maha Agung",
            defaultTarget = 100,
            fadhilah = "Membuka pintu rezeki, melapangkan kesempitan, dan menggugurkan dosa."
        ),
        TasbihDzikirPreset(
            id = "tahlil",
            arabic = "لَا إِلَهَ إِلَّا اللَّهُ",
            latin = "La Ilaha Illallah",
            translation = "Tiada sesembahan yang berhak disembah selain Allah",
            defaultTarget = 100,
            fadhilah = "Kalimat tauhid paling utama, benteng keselamatan seorang mukmin di dunia dan akhirat."
        ),
        TasbihDzikirPreset(
            id = "sholawat",
            arabic = "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ",
            latin = "Allahumma Sholli 'Ala Sayyidina Muhammad",
            translation = "Ya Allah limpahkanlah rahmat kepada junjungan kami Nabi Muhammad",
            defaultTarget = 100,
            fadhilah = "Barangsiapa bersholawat 1 kali, Allah akan bersholawat kepadanya 10 kali lipat."
        ),
        TasbihDzikirPreset(
            id = "tasbih_lengkap",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
            latin = "Subhanallahi wa bihamdihi, Subhanallahil 'Azhim",
            translation = "Maha Suci Allah dengan memuji-Nya, Maha Suci Allah Yang Maha Agung",
            defaultTarget = 100,
            fadhilah = "Dua kalimat yang ringan di lisan, berat di timbangan amal, dan dicintai Ar-Rahman (HR. Bukhari)."
        ),
        TasbihDzikirPreset(
            id = "hauqalah",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            latin = "La hawla wa la quwwata illa billahil 'aliyyil 'azhim",
            translation = "Tiada daya dan upaya kecuali dengan pertolongan Allah Yang Maha Tinggi lagi Maha Agung",
            defaultTarget = 33,
            fadhilah = "Salah satu simpanan harta karun surga (kanzun min kunuzil jannah) dan obat bagi kegundahan."
        ),
        TasbihDzikirPreset(
            id = "yunus",
            arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            latin = "La ilaha illa Anta subhanaka inni kuntu minazh-zhalimin",
            translation = "Tiada Tuhan selain Engkau, Maha Suci Engkau, sungguh aku termasuk orang yang zalim",
            defaultTarget = 33,
            fadhilah = "Doa Dzun Nun (Nabi Yunus AS) di dalam perut ikan paus. Siapa pun yang berdoa dengannya niscaya diijabah."
        ),
        TasbihDzikirPreset(
            id = "bebas",
            arabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ",
            latin = "Dzikir & Wirid Pribadi",
            translation = "Wahai Dzat Yang Maha Hidup, dengan rahmat-Mu aku memohon pertolongan",
            defaultTarget = 0, // Bebas
            fadhilah = "Dzikir khusus dengan target hitungan bebas sesuai kebiasaan dan amalan wirid harian Anda."
        )
    )
}
