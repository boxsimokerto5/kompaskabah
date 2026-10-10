package com.example.domain.amalan

enum class AmalanCategory(
    val title: String,
    val subtitle: String,
    val badge: String,
    val description: String,
    val ornamentSymbol: String
) {
    ASMAUL_HUSNA(
        title = "Asmaul Husna",
        subtitle = "99 Nama Allah Yang Maha Indah",
        badge = "99 Nama",
        description = "Mengenal sifat-sifat keagungan Allah SWT dengan teks Arab, Latin, dan arti lengkap.",
        ornamentSymbol = "ﷲ"
    ),
    TATA_CARA_WUDHU(
        title = "Tata Cara Wudhu",
        subtitle = "Panduan Bersuci Sesuai Sunnah",
        badge = "Rukun & Doa",
        description = "Panduan praktis berwudhu mulai dari niat, urutan membasuh, hingga doa setelah wudhu.",
        ornamentSymbol = "۩"
    ),
    TATA_CARA_SHOLAT(
        title = "Tata Cara Sholat",
        subtitle = "Lengkap Bacaan & Suara Membaca",
        badge = "Audio Lengkap",
        description = "Panduan gerakan sholat fardhu dari takbir hingga salam dengan bacaan Arab, Latin & audio.",
        ornamentSymbol = "۞"
    ),
    YASIN(
        title = "Surat Yasin",
        subtitle = "83 Ayat Lengkap Arab, Latin & Terjemah",
        badge = "Makkiyyah",
        description = "Jantung Al-Qur'an (Qalb Al-Qur'an) dengan teks Arab yang jelas, Latin dan terjemahannya.",
        ornamentSymbol = "📖"
    ),
    TAHLIL(
        title = "Tahlil & Doa Arwah",
        subtitle = "Susunan Bacaan Tahlil Lengkap",
        badge = "Dzikir & Doa",
        description = "Rangkaian bacaan tahlil, istighfar, tasbih, tahmid, sholawat, hingga doa arwah.",
        ornamentSymbol = "📿"
    ),
    DOA_SETELAH_SHOLAT(
        title = "Doa Setelah Sholat",
        subtitle = "Dzikir & Doa Ba'da Sholat Fardhu",
        badge = "Mustajab",
        description = "Wirid dan doa ma'tsur setelah sholat fardhu untuk memohon keselamatan dunia & akhirat.",
        ornamentSymbol = "🤲"
    ),
    SHOLAWAT_LENGKAP(
        title = "Sholawat Lengkap",
        subtitle = "Kumpulan 8 Sholawat Agung & Fadhilah",
        badge = "8 Sholawat",
        description = "Sholawat Ibrahimiyah, Nariyah, Tibbil Qulub, Munjiyat, Fatih, Badar, Asyghil & Jibril.",
        ornamentSymbol = "✨"
    )
}

data class AsmaulHusnaItem(
    val number: Int,
    val arabic: String,
    val latin: String,
    val meaningId: String,
    val explanation: String
)

data class WudhuStep(
    val stepNumber: Int,
    val title: String,
    val arabicText: String = "",
    val latinText: String = "",
    val translation: String = "",
    val description: String,
    val isRukun: Boolean = true
)

data class SholatStepItem(
    val stepNumber: Int,
    val movementName: String,
    val arabicRecitation: String,
    val latinRecitation: String,
    val indonesianMeaning: String,
    val explanation: String
)

data class YasinAyat(
    val ayatNumber: Int,
    val arabicText: String,
    val latinText: String,
    val indonesianTranslation: String
)

data class TahlilItem(
    val orderNumber: Int,
    val sectionTitle: String,
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val repetitionNote: String = "1x"
)

data class DoaItem(
    val id: String,
    val title: String,
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val sourceOrFadhilah: String
)

data class SholawatItem(
    val id: String,
    val title: String,
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val fadhilah: String
)
