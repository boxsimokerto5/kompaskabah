package com.example.domain

/**
 * Represents a preset or custom geographical location for Qibla calculation.
 */
data class CityPreset(
    val id: String,
    val name: String,
    val country: String,
    val provinceOrRegion: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 7.0
)

object PresetCitiesRepository {
    val allCities: List<CityPreset> = listOf(
        CityPreset("jkt", "Jakarta (Masjid Istiqlal)", "Indonesia", "DKI Jakarta • WIB", -6.1702, 106.8314, 7.0),
        CityPreset("sby", "Surabaya (Masjid Al-Akbar)", "Indonesia", "Jawa Timur • WIB", -7.3364, 112.7150, 7.0),
        CityPreset("bdg", "Bandung (Masjid Raya)", "Indonesia", "Jawa Barat • WIB", -6.9218, 107.6061, 7.0),
        CityPreset("yog", "Yogyakarta (Masjid Gedhe Kauman)", "Indonesia", "DI Yogyakarta • WIB", -7.8039, 110.3622, 7.0),
        CityPreset("smg", "Semarang (Masjid Agung Jawa Tengah)", "Indonesia", "Jawa Tengah • WIB", -6.9839, 110.4453, 7.0),
        CityPreset("mlg", "Malang (Masjid Agung Jami')", "Indonesia", "Jawa Timur • WIB", -7.9826, 112.6300, 7.0),
        CityPreset("slo", "Surakarta / Solo (Masjid Sheikh Zayed)", "Indonesia", "Jawa Tengah • WIB", -7.5546, 110.8350, 7.0),
        CityPreset("mdn", "Medan (Masjid Raya Al-Mashun)", "Indonesia", "Sumatera Utara • WIB", 3.5752, 98.6873, 7.0),
        CityPreset("bna", "Banda Aceh (Masjid Raya Baiturrahman)", "Indonesia", "Aceh • WIB", 5.5536, 95.3172, 7.0),
        CityPreset("pdg", "Padang (Masjid Raya Sumatera Barat)", "Indonesia", "Sumatera Barat • WIB", -0.9242, 100.3624, 7.0),
        CityPreset("plm", "Palembang (Masjid Agung)", "Indonesia", "Sumatera Selatan • WIB", -2.9879, 104.7600, 7.0),
        CityPreset("pku", "Pekanbaru (Masjid Agung An-Nur)", "Indonesia", "Riau • WIB", 0.5269, 101.4515, 7.0),
        CityPreset("bks", "Bekasi", "Indonesia", "Jawa Barat • WIB", -6.2383, 106.9756, 7.0),
        CityPreset("tgr", "Tangerang", "Indonesia", "Banten • WIB", -6.1783, 106.6319, 7.0),
        CityPreset("dpk", "Depok (Masjid Kubah Emas)", "Indonesia", "Jawa Barat • WIB", -6.3839, 106.7720, 7.0),
        CityPreset("bgr", "Bogor", "Indonesia", "Jawa Barat • WIB", -6.5971, 106.8060, 7.0),
        CityPreset("dps", "Denpasar", "Indonesia", "Bali • WITA", -8.6705, 115.2126, 8.0),
        CityPreset("mtr", "Mataram (Islamic Center NTB)", "Indonesia", "Nusa Tenggara Barat • WITA", -8.5800, 116.1008, 8.0),
        CityPreset("mks", "Makassar (Masjid 99 Kubah)", "Indonesia", "Sulawesi Selatan • WITA", -5.1436, 119.4045, 8.0),
        CityPreset("bpn", "Balikpapan (Islamic Center)", "Indonesia", "Kalimantan Timur • WITA", -1.2379, 116.8529, 8.0),
        CityPreset("smd", "Samarinda (Islamic Center)", "Indonesia", "Kalimantan Timur • WITA", -0.5022, 117.1214, 8.0),
        CityPreset("bjm", "Banjarmasin (Masjid Raya Sabilal Muhtadin)", "Indonesia", "Kalimantan Selatan • WITA", -3.3167, 114.5913, 8.0),
        CityPreset("ptk", "Pontianak (Masjid Raya Mujahidin)", "Indonesia", "Kalimantan Barat • WIB", -0.0406, 109.3361, 7.0),
        CityPreset("mdc", "Manado", "Indonesia", "Sulawesi Utara • WITA", 1.4748, 124.8421, 8.0),
        CityPreset("amb", "Ambon (Masjid Raya Al-Fatah)", "Indonesia", "Maluku • WIT", -3.6954, 128.1814, 9.0),
        CityPreset("jyp", "Jayapura", "Indonesia", "Papua • WIT", -2.5337, 140.7181, 9.0),
        CityPreset("sin", "Singapura (Masjid Sultan)", "Singapura", "Kampong Glam • SGT", 1.3023, 103.8590, 8.0),
        CityPreset("kul", "Kuala Lumpur (Masjid Negara)", "Malaysia", "Wilayah Persekutuan • MYT", 3.1419, 101.6917, 8.0),
        CityPreset("bwn", "Bandar Seri Begawan (Masjid Omar Ali Saifuddien)", "Brunei", "Brunei-Muara • BNT", 4.8895, 114.9393, 8.0),
        CityPreset("med", "Madinah (Masjid Nabawi)", "Arab Saudi", "Al Madinah • AST", 24.4672, 39.6112, 3.0),
        CityPreset("ist", "Istanbul (Masjid Hagia Sophia)", "Turki", "Fatih • TRT", 41.0086, 28.9802, 3.0),
        CityPreset("cai", "Kairo (Masjid Al-Azhar)", "Mesir", "Kairo • EET", 30.0459, 31.2627, 2.0),
        CityPreset("tyo", "Tokyo (Tokyo Camii)", "Jepang", "Shibuya • JST", 35.6681, 139.6766, 9.0)
    )

    val defaultCity: CityPreset = allCities.first() // Jakarta (Masjid Istiqlal)
}
