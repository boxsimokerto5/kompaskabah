package com.example.domain.amalan

object SholatData {
    val prayerNiyyahList: List<DoaItem> = listOf(
        DoaItem(
            id = "niat_subuh",
            title = "Niat Sholat Subuh (2 Rakaat)",
            arabicText = "أُصَلِّي فَرْضَ الصُّبْحِ رَكْعَتَيْنِ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً (مَأْمُومًا/إِمَامًا) لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardhas shubhi rak'ataini mustaqbilal qiblati adaa-an (ma'muuman/imaaman) lillaahi ta'aalaa.",
            translation = "Aku berniat sholat fardhu Subuh dua rakaat menghadap kiblat pada waktunya (makmum/imam) karena Allah Ta'ala.",
            sourceOrFadhilah = "Sholat fardhu fajar dengan pahala disaksikan oleh malaikat malam dan malaikat siang."
        ),
        DoaItem(
            id = "niat_dzuhur",
            title = "Niat Sholat Dzuhur (4 Rakaat)",
            arabicText = "أُصَلِّي فَرْضَ الظُّهْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً (مَأْمُومًا/إِمَامًا) لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardhadh dhuhri arba'a raka'aatin mustaqbilal qiblati adaa-an (ma'muuman/imaaman) lillaahi ta'aalaa.",
            translation = "Aku berniat sholat fardhu Dzuhur empat rakaat menghadap kiblat pada waktunya (makmum/imam) karena Allah Ta'ala.",
            sourceOrFadhilah = "Dikerjakan setelah matahari tergelincir dari puncaknya."
        ),
        DoaItem(
            id = "niat_ashar",
            title = "Niat Sholat Ashar (4 Rakaat)",
            arabicText = "أُصَلِّي فَرْضَ الْعَصْرِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً (مَأْمُومًا/إِمَامًا) لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardhal 'ashri arba'a raka'aatin mustaqbilal qiblati adaa-an (ma'muuman/imaaman) lillaahi ta'aalaa.",
            translation = "Aku berniat sholat fardhu Ashar empat rakaat menghadap kiblat pada waktunya (makmum/imam) karena Allah Ta'ala.",
            sourceOrFadhilah = "Sholat Wustha yang sangat ditekankan pemeliharaannya dalam Al-Qur'an."
        ),
        DoaItem(
            id = "niat_maghrib",
            title = "Niat Sholat Maghrib (3 Rakaat)",
            arabicText = "أُصَلِّي فَرْضَ الْمَغْرِبِ ثَلَاثَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً (مَأْمُومًا/إِمَامًا) لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardhal maghribi tsalaatsa raka'aatin mustaqbilal qiblati adaa-an (ma'muuman/imaaman) lillaahi ta'aalaa.",
            translation = "Aku berniat sholat fardhu Maghrib tiga rakaat menghadap kiblat pada waktunya (makmum/imam) karena Allah Ta'ala.",
            sourceOrFadhilah = "Dikerjakan tepat setelah terbenamnya matahari."
        ),
        DoaItem(
            id = "niat_isya",
            title = "Niat Sholat Isya (4 Rakaat)",
            arabicText = "أُصَلِّي فَرْضَ الْعِشَاءِ أَرْبَعَ رَكَعَاتٍ مُسْتَقْبِلَ الْقِبْلَةِ أَدَاءً (مَأْمُومًا/إِمَامًا) لِلّٰهِ تَعَالَى",
            latinText = "Ushallii fardhal 'isyaa-i arba'a raka'aatin mustaqbilal qiblati adaa-an (ma'muuman/imaaman) lillaahi ta'aalaa.",
            translation = "Aku berniat sholat fardhu Isya empat rakaat menghadap kiblat pada waktunya (makmum/imam) karena Allah Ta'ala.",
            sourceOrFadhilah = "Sholat penutup fardhu di malam hari dengan keutamaan pahala setengah malam."
        )
    )

    val steps: List<SholatStepItem> = listOf(
        SholatStepItem(
            stepNumber = 1,
            movementName = "Takbiratul Ihram & Niat",
            arabicRecitation = "اللّٰهُ أَكْبَرُ",
            latinRecitation = "Allaahu Akbar",
            indonesianMeaning = "Allah Maha Besar.",
            explanation = "Mengangkat kedua tangan sejajar dengan telinga atau bahu seraya menghadirkan niat sholat di dalam hati."
        ),
        SholatStepItem(
            stepNumber = 2,
            movementName = "Membaca Doa Iftitah (Sunnah Muakkad)",
            arabicRecitation = "اللّٰهُ أَكْبَرُ كَبِيرًا وَالْحَمْدُ لِلّٰهِ كَثِيرًا وَسُبْحَانَ اللّٰهِ بُكْرَةً وَأَصِيلًا. إِنِّي وَجَّهْتُ وَجْهِيَ لِلَّذِي فَطَرَ السَّمَاوَاتِ وَالْأَرْضَ حَنِيفًا مُسْلِمًا وَمَا أَنَا مِنَ الْمُشْرِكِينَ. إِنَّ صَلَاتِي وَنُسُكِي وَمَحْيَايَ وَمَمَاتِي لِلّٰهِ رَبِّ الْعَالَمِينَ. لَا شَرِيكَ لَهُ وَبِذٰلِكَ أُمِرْتُ وَأَنَا مِنَ الْمُسْلِمِينَ",
            latinRecitation = "Allaahu akbaru kabiiraa, walhamdu lillaahi katsiiraa, wa subhaanallaahi bukrataw wa ashiilaa. Innii wajjahtu wajhiya lilladzii fatharas samaawaati wal ardha haniifam muslimaw wamaa ana minal musyrikiin. Inna shalaatii wa nusukii wa mahyaaya wa mamaatii lillaahi rabbil 'aalamiin. Laa syariika lahu wa bidzaalika umirtu wa ana minal muslimiin.",
            indonesianMeaning = "Allah Maha Besar sebesar-besarnya. Segala puji bagi Allah sebanyak-banyaknya. Maha Suci Allah pagi dan petang. Sesungguhnya aku hadapkan wajahku kepada Pencipta langit dan bumi dengan lurus dan berserah diri, dan aku bukanlah dari golongan orang musyrik. Sesungguhnya sholatku, ibadahku, hidupku, dan matiku hanyalah untuk Allah Tuhan semesta alam. Tiada sekutu bagi-Nya, dan demikianlah aku diperintahkan serta aku termasuk orang-orang yang berserah diri.",
            explanation = "Dibaca dengan suara pelan (sirr) setelah takbiratul ihram pada rakaat pertama sebelum membaca Al-Fatihah."
        ),
        SholatStepItem(
            stepNumber = 3,
            movementName = "Membaca Surat Al-Fatihah (Rukun)",
            arabicRecitation = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ (١) الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ (٢) الرَّحْمٰنِ الرَّحِيمِ (٣) مَالِكِ يَوْمِ الدِّينِ (٤) إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ (٥) اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ (٦) صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ (٧)",
            latinRecitation = "Bismillaahir rahmaanir rahiim. Alhamdu lillaahi rabbil 'aalamiin. Ar-rahmaanir rahiim. Maaliki yawmid diin. Iyyaaka na'budu wa iyyaaka nasta'iin. Ihdinash shiraathal mustaqiim. Shiraathal ladziina an'amta 'alayhim ghayril maghdhuubi 'alayhim waladh dhaalliin. Aamiin.",
            indonesianMeaning = "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang. Segala puji bagi Allah, Tuhan semesta alam. Maha Pengasih lagi Maha Penyayang. Pemilik hari pembalasan. Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan. Tunjukilah kami jalan yang lurus. Yaitu jalan orang-orang yang telah Engkau beri nikmat, bukan jalan mereka yang dimurkai dan bukan pula jalan mereka yang sesat. Aamiin.",
            explanation = "Wajib dibaca pada setiap rakaat sholat. Dilanjutkan dengan membaca surat pendek pada rakaat pertama dan kedua."
        ),
        SholatStepItem(
            stepNumber = 4,
            movementName = "Ruku' & Tuma'ninah (Rukun)",
            arabicRecitation = "سُبْحَانَ رَبِّيَ الْعَظِيمِ وَبِحَمْدِهِ",
            latinRecitation = "Subhaana rabbiyal 'azhiimi wa bihamdih (3x)",
            indonesianMeaning = "Maha Suci Tuhanku Yang Maha Agung dan dengan memuji-Nya (dibaca 3 kali).",
            explanation = "Membungkuk hingga punggung lurus mendatar sejajar dengan kepala, kedua telapak tangan memegang lutut dengan jemari renggang."
        ),
        SholatStepItem(
            stepNumber = 5,
            movementName = "I'tidal & Tuma'ninah (Rukun)",
            arabicRecitation = "سَمِعَ اللّٰهُ لِمَنْ حَمِدَهُ. رَبَّنَا لَكَ الْحَمْدُ مِلْءَ السَّمَاوَاتِ وَمِلْءَ الْأَرْضِ وَمِلْءَ مَا شِئْتَ مِنْ شَيْءٍ بَعْدُ",
            latinRecitation = "Sami'allaahu liman hamidah. Rabbanaa lakal hamdu mil-us samaawaati wa mil-ul ardhi wa mil-u maa syi'ta min syay-im ba'du.",
            indonesianMeaning = "Allah mendengar orang yang memuji-Nya. Wahai Tuhan kami, bagi-Mu segala puji sepenuh langit, sepenuh bumi, dan sepenuh apa yang Engkau kehendaki sesudah itu.",
            explanation = "Bangkit dari ruku' hingga berdiri tegak lurus seraya mengangkat kedua tangan, kemudian lurus ke bawah di samping badan."
        ),
        SholatStepItem(
            stepNumber = 6,
            movementName = "Sujud Pertama & Kedua (Rukun)",
            arabicRecitation = "سُبْحَانَ رَبِّيَ الْأَعْلَى وَبِحَمْدِهِ",
            latinRecitation = "Subhaana rabbiyal a'laa wa bihamdih (3x)",
            indonesianMeaning = "Maha Suci Tuhanku Yang Maha Tinggi dan dengan memuji-Nya (dibaca 3 kali).",
            explanation = "Menempelkan 7 anggota sujud ke lantai: dahi beserta hidung, kedua telapak tangan, kedua lutut, dan ujung jari-jari kedua kaki."
        ),
        SholatStepItem(
            stepNumber = 7,
            movementName = "Duduk di Antara Dua Sujud (Iftirasy)",
            arabicRecitation = "رَبِّ اغْفِرْ لِي وَارْحَمْنِي وَاجْبُرْنِي وَارْفَعْنِي وَارْزُقْنِي وَاهْدِنِي وَعَافِنِي وَاعْفُ عَنِّي",
            latinRecitation = "Rabbighfir lii warhamnii wajburnii warfa'nii warzuqnii wahdinii wa 'aafinii wa'fu 'annii.",
            indonesianMeaning = "Ya Tuhanku, ampunilah aku, sayangilah aku, cukupkanlah kekuranganku, tinggikanlah derajatku, berilah aku rezeki, berilah aku petunjuk, sehatkanlah aku, dan maafkanlah kesalahanku.",
            explanation = "Duduk iftirasy di atas telapak kaki kiri dengan kaki kanan ditegakkan, telapak tangan diletakkan di atas paha dekat lutut."
        ),
        SholatStepItem(
            stepNumber = 8,
            movementName = "Tasyahud Awal (Duduk Iftirasy)",
            arabicRecitation = "التَّحِيَّاتُ الْمُبَارَكَاتُ الصَّلَوَاتُ الطَّيِّبَاتُ لِلّٰهِ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللّٰهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَى عِبَادِ اللّٰهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللّٰهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ. اللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ",
            latinRecitation = "Attahiyyaatul mubaarakaatush shalawaatuth thayyibaatu lillaah. Assalaamu 'alayka ayyuhan nabiyyu wa rahmatullaahi wa barakaatuh. Assalaamu 'alaynaa wa 'alaa 'ibaadillaahish shaalihiin. Asyhadu allaa ilaaha illallaah wa asyhadu anna Muhammadar rasuulullaah. Allahumma shalli 'alaa sayyidinaa Muhammad.",
            indonesianMeaning = "Segala kehormatan, keberkahan, kebahagiaan, dan kebaikan hanyalah kepunyaan Allah. Keselamatan, rahmat Allah, dan berkah-Nya semoga tercurah kepadamu wahai Nabi. Keselamatan semoga tercurah kepada kami dan hamba-hamba Allah yang saleh. Aku bersaksi tiada Tuhan selain Allah dan aku bersaksi Muhammad utusan Allah. Ya Allah berilah rahmat kepada junjungan kami Nabi Muhammad.",
            explanation = "Dilakukan pada rakaat kedua dalam sholat 3 atau 4 rakaat. Mengacungkan jari telunjuk saat lafadz 'illallaah'."
        ),
        SholatStepItem(
            stepNumber = 9,
            movementName = "Tasyahud Akhir (Duduk Tawarruk - Rukun)",
            arabicRecitation = "التَّحِيَّاتُ الْمُبَارَكَاتُ الصَّلَوَاتُ الطَّيِّبَاتُ لِلّٰهِ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللّٰهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَى عِبَادِ اللّٰهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللّٰهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ. اللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى سَيِّدِنَا إِبْرَاهِيمَ وَعَلَى آلِ سَيِّدِنَا إِبْرَاهِيمَ وَبَارِكْ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى سَيِّدِنَا إِبْرَاهِيمَ وَعَلَى آلِ سَيِّدِنَا إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            latinRecitation = "Attahiyyaatul mubaarakaatush shalawaatuth thayyibaatu lillaah. Assalaamu 'alayka ayyuhan nabiyyu wa rahmatullaahi wa barakaatuh. Assalaamu 'alaynaa wa 'alaa 'ibaadillaahish shaalihiin. Asyhadu allaa ilaaha illallaah wa asyhadu anna Muhammadar rasuulullaah. Allahumma shalli 'alaa sayyidinaa Muhammad wa 'alaa aali sayyidinaa Muhammad kamaa shallayta 'alaa sayyidinaa Ibraahiim wa 'alaa aali sayyidinaa Ibraahiim, wa baarik 'alaa sayyidinaa Muhammad wa 'alaa aali sayyidinaa Muhammad kamaa baarakta 'alaa sayyidinaa Ibraahiim wa 'alaa aali sayyidinaa Ibraahiim fil 'aalamiina innaka hamiidum majiid.",
            indonesianMeaning = "Segala kehormatan, keberkahan, shalawat, dan kebaikan hanyalah bagi Allah... Ya Allah limpahkanlah shalawat dan berkah kepada Nabi Muhammad dan keluarga Nabi Muhammad sebagaimana Engkau limpahkan kepada Nabi Ibrahim dan keluarga Nabi Ibrahim, sesungguhnya Engkau Maha Terpuji lagi Maha Mulia di alam semesta.",
            explanation = "Duduk tawarruk dengan memasukkan kaki kiri di bawah kaki kanan dan pantat langsung duduk di lantai."
        ),
        SholatStepItem(
            stepNumber = 10,
            movementName = "Salam (Rukun)",
            arabicRecitation = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللّٰهِ وَبَرَكَاتُهُ",
            latinRecitation = "Assalaamu 'alaykum wa rahmatullaahi wa barakaatuh",
            indonesianMeaning = "Semoga keselamatan, rahmat Allah, dan berkah-Nya tercurah kepada kalian.",
            explanation = "Menolehkan kepala ke arah kanan hingga pipi terlihat dari belakang (rukun), kemudian menoleh ke arah kiri (sunnah)."
        )
    )
}
