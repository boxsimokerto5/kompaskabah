package com.example.domain.amalan

object WudhuData {
    val steps: List<WudhuStep> = listOf(
        WudhuStep(
            stepNumber = 1,
            title = "Membaca Basmalah & Niat Wudhu",
            arabicText = "نَوَيْتُ الْوُضُوءَ لِرَفْعِ الْحَدَثِ الْأَصْغَرِ فَرْضًا لِلّٰهِ تَعَالَى",
            latinText = "Nawaitul wudhuu-a liraf'il hadatsil ashghari fardhal lillaahi ta'aalaa.",
            translation = "Aku berniat berwudhu untuk menghilangkan hadas kecil, fardhu karena Allah Ta'ala.",
            description = "Niat diucapkan dalam hati bertepatan saat membasuh wajah pertama kali. Membaca Bismillah sebelum memulai adalah sunnah muakkad.",
            isRukun = true
        ),
        WudhuStep(
            stepNumber = 2,
            title = "Membasuh Kedua Telapak Tangan",
            arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ",
            latinText = "Bismillaahir rahmaanir rahiim.",
            translation = "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang.",
            description = "Membasuh kedua telapak tangan hingga pergelangan sebanyak 3 kali sambil menyela-nyela jemari.",
            isRukun = false
        ),
        WudhuStep(
            stepNumber = 3,
            title = "Berkumur-kumur (Madhmadhoh)",
            arabicText = "اللّٰهُمَّ أَعِنِّي عَلَى تِلَاوَةِ الْقُرْآنِ وَذِكْرِكَ وَشُكْرِكَ",
            latinText = "Allahumma a'innii 'alaa tilaawatil qur-aani wa dzikrika wa syukrika.",
            translation = "Ya Allah, tolonglah aku untuk senantiasa membaca Al-Qur'an, berdzikir, dan bersyukur kepada-Mu.",
            description = "Memasukkan air ke dalam mulut dan berkumur-kumur sebanyak 3 kali hingga bersih dari sisa makanan.",
            isRukun = false
        ),
        WudhuStep(
            stepNumber = 4,
            title = "Menghirup & Mengeluarkan Air dari Hidung (Istinsyaq)",
            arabicText = "اللّٰهُمَّ أَرِحْنِي رَائِحَةَ الْجَنَّةِ",
            latinText = "Allahumma arihnii raa-ihatal jannah.",
            translation = "Ya Allah, ciumkanlah kepadaku wangi aroma surga-Mu.",
            description = "Menghirup air ke rongga hidung lalu mengeluarkannya kembali dengan tangan kiri sebanyak 3 kali.",
            isRukun = false
        ),
        WudhuStep(
            stepNumber = 5,
            title = "Membasuh Seluruh Wajah (Rukun)",
            arabicText = "نَوَيْتُ الْوُضُوءَ لِرَفْعِ الْحَدَثِ الْأَصْغَرِ فَرْضًا لِلّٰهِ تَعَالَى",
            latinText = "Nawaitul wudhuu-a liraf'il hadatsil ashghari fardhal lillaahi ta'aalaa.",
            translation = "Aku berniat berwudhu untuk menghilangkan hadas kecil, fardhu karena Allah Ta'ala.",
            description = "Membasuh seluruh wajah mulai dari batas tumbuhnya rambut kepala hingga ujung dagu, dan dari telinga kanan ke telinga kiri secara merata sebanyak 3 kali.",
            isRukun = true
        ),
        WudhuStep(
            stepNumber = 6,
            title = "Membasuh Kedua Tangan Hingga Siku (Rukun)",
            arabicText = "اللّٰهُمَّ أَعْطِنِي كِتَابِي بِيَمِينِي وَحَاسِبْنِي حِسَابًا يَسِيرًا",
            latinText = "Allahumma a'thinii kitaabii bi yamiinii wa haasibnii hisaaban yasiiraa.",
            translation = "Ya Allah, berikanlah buku catatan amalku di tangan kananku dan hisablah aku dengan hisab yang mudah.",
            description = "Membasuh tangan kanan hingga melewati siku 3 kali, dilanjutkan tangan kiri hingga melewati siku 3 kali sambil menyela-nyela jari.",
            isRukun = true
        ),
        WudhuStep(
            stepNumber = 7,
            title = "Mengusap Sebagian Rambut Kepala (Rukun)",
            arabicText = "اللّٰهُمَّ حَرِّمْ شَعْرِي وَبَشَرِي عَلَى النَّارِ",
            latinText = "Allahumma harrim sya'rii wa basyarii 'alan naar.",
            translation = "Ya Allah, haramkanlah rambut dan kulit tubuhku dari sentuhan api neraka.",
            description = "Mengusap sebagian kulit/rambut kepala dengan air basahan telapak tangan sebanyak 3 kali.",
            isRukun = true
        ),
        WudhuStep(
            stepNumber = 8,
            title = "Mengusap Kedua Daun Telinga",
            arabicText = "اللّٰهُمَّ اجْعَلْنِي مِنَ الَّذِينَ يَسْتَمِعُونَ الْقَوْلَ فَيَتَّبِعُونَ أَحْسَنَهُ",
            latinText = "Allahummaj'alnii minal ladziina yastami'uunal qawla fayattabi'uuna ahsanah.",
            translation = "Ya Allah, jadikanlah aku termasuk orang yang mendengarkan perkataan lalu mengikuti yang terbaik darinya.",
            description = "Memasukkan jari telunjuk ke lubang telinga dan ibu jari mengusap bagian belakang daun telinga luar sebanyak 3 kali.",
            isRukun = false
        ),
        WudhuStep(
            stepNumber = 9,
            title = "Membasuh Kedua Kaki Hingga Mata Kaki (Rukun)",
            arabicText = "اللّٰهُمَّ ثَبِّتْ قَدَمَيَّ عَلَى الصِّرَاطِ يَوْمَ تَزِلُّ فِيهِ الْأَقْدَامُ",
            latinText = "Allahumma tsabbit qadamayya 'alash shiraathi yawma tazillu fiihil aqdaam.",
            translation = "Ya Allah, kokohkanlah kedua telapak kakiku di atas jembatan Shirath pada hari ketika kaki-kaki tergelincir.",
            description = "Membasuh kaki kanan hingga mata kaki 3 kali, lalu kaki kiri hingga mata kaki 3 kali sambil menyela-nyela jemari kaki.",
            isRukun = true
        ),
        WudhuStep(
            stepNumber = 10,
            title = "Tertib & Membaca Doa Setelah Wudhu",
            arabicText = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ. اللّٰهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ وَاجْعَلْنِي مِنْ عِبَادِكَ الصَّالِحِينَ",
            latinText = "Asyhadu allaa ilaaha illallaahu wahdahu laa syariika lah, wa asyhadu anna Muhammadan 'abduhuu wa rasuuluh. Allahummaj'alnii minat tawwaabiina waj'alnii minal mutathahhiriina waj'alnii min 'ibaadikas shaalihiin.",
            translation = "Aku bersaksi tiada Tuhan selain Allah Yang Maha Esa tiada sekutu bagi-Nya, dan aku bersaksi Nabi Muhammad adalah hamba dan utusan-Nya. Ya Allah jadikanlah aku termasuk orang yang bertobat, orang yang bersuci, dan hamba-Mu yang saleh.",
            description = "Melakukan semua rukun secara berurutan (tertib) tanpa jeda, lalu menghadap kiblat dan membaca doa setelah wudhu.",
            isRukun = true
        )
    )
}
