package com.example.domain.hadits

import androidx.compose.ui.graphics.Color

enum class HaditsGrade(
    val displayName: String,
    val badgeLabel: String,
    val description: String,
    val primaryColor: Color,
    val containerColor: Color
) {
    SHAHIH(
        displayName = "Shahih",
        badgeLabel = "🟢 Shahih",
        description = "Sanad bersambung, perawi adil dan dhabith (kuat hafalan), tidak syadz dan tanpa 'illat (cacat). Landasan utama hukum & akidah.",
        primaryColor = Color(0xFF1B8A5A),
        containerColor = Color(0xFFE8F7F0)
    ),
    HASAN(
        displayName = "Hasan",
        badgeLabel = "🟡 Hasan",
        description = "Memenuhi kriteria shahih namun ketelitian perawi setingkat di bawahnya. Sah dijadikan hujjah hukum dan amalan ibadah.",
        primaryColor = Color(0xFFC07000),
        containerColor = Color(0xFFFFF7E6)
    ),
    DHAIF(
        displayName = "Dha'if (Lemah)",
        badgeLabel = "🟠 Dha'if",
        description = "Sanad tidak memenuhi syarat hadits maqbul karena kelemahan perawi atau sanad terputus. Tidak dipakai untuk akidah & hukum.",
        primaryColor = Color(0xFFD32F2F),
        containerColor = Color(0xFFFFEBEE)
    ),
    POPULER_KLARIFIKASI(
        displayName = "Klarifikasi Populer",
        badgeLabel = "🔵 Klarifikasi",
        description = "Hadits masyhur di masyarakat yang perlu diluruskan derajat keasliannya dan diarahkan ke riwayat shahih yang semakna.",
        primaryColor = Color(0xFF1976D2),
        containerColor = Color(0xFFE3F2FD)
    )
}

enum class HaditsTopic(
    val title: String,
    val iconSymbol: String
) {
    SEMUA(
        title = "Semua",
        iconSymbol = "📚"
    ),
    NIAT_AKIDAH(
        title = "Niat & Akidah",
        iconSymbol = "🤍"
    ),
    SHOLAT_IBADAH(
        title = "Sholat & Ibadah",
        iconSymbol = "🕌"
    ),
    AKHLAK_ADAB(
        title = "Akhlak & Adab",
        iconSymbol = "🌸"
    ),
    MUAMALAH_SOSIAL(
        title = "Sosial & Muamalah",
        iconSymbol = "🤝"
    ),
    ILMU_DZIKIR(
        title = "Ilmu & Dzikir",
        iconSymbol = "📖"
    ),
    KELUARGA_ORANG_TUA(
        title = "Ibu Bapak & Keluarga",
        iconSymbol = "👨‍👩‍👧"
    ),
    KLARIFIKASI_POPULER(
        title = "Klarifikasi Viral",
        iconSymbol = "🔍"
    )
}

data class HaditsItem(
    val id: String,
    val number: Int,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val narrator: String,
    val grade: HaditsGrade,
    val gradeExplanation: String,
    val topic: HaditsTopic,
    val takhrijBook: String,
    val faedah: String,
    val clarification: String? = null
)
