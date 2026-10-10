package com.example.domain.amalan

object DoaSholatData {
    val dzikirList: List<DoaItem> = listOf(
        DoaItem(
            id = "dzikir_istighfar",
            title = "1. Istighfar Ba'da Sholat",
            arabicText = "أَسْتَغْفِرُ اللّٰهَ الْعَظِيمَ (٣×)، الَّذِي لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
            latinText = "Astaghfirullaahal 'azhiim (3x), alladzii laaa ilaaha illaa huwal hayyul qayyuumu wa atuubu ilayh.",
            translation = "Aku memohon ampun kepada Allah Yang Maha Agung (3x), yang tiada Tuhan selain Dia Yang Maha Hidup lagi Berdiri Sendiri, dan aku bertobat kepada-Nya.",
            sourceOrFadhilah = "Sunnah dibaca 3 kali tepat setelah salam."
        ),
        DoaItem(
            id = "dzikir_salam",
            title = "2. Doa Keselamatan (Antas Salam)",
            arabicText = "اللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            latinText = "Allahumma antas salaam, wa minkas salaam, tabaarakta yaa dzal jalaali wal ikraam.",
            translation = "Ya Allah, Engkaulah Dzat yang memberi keselamatan, dan dari-Mulah keselamatan. Maha Berkah Engkau wahai Tuhan Pemilik Keagungan dan Kemuliaan.",
            sourceOrFadhilah = "HR. Muslim no. 591 dari Tsauban RA."
        ),
        DoaItem(
            id = "dzikir_tauhid",
            title = "3. Kalimat Tauhid & Pujian",
            arabicText = "لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِي وَيُمِيتُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ. اللّٰهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ وَلَا مُعْطِيَ لِمَا مَنَعْتَ وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
            latinText = "Laa ilaaha illallaahu wahdahu laa syariika lah, lahul mulku wa lahul hamdu yuhyii wa yumiitu wa huwa 'alaa kulli syay-in qadiir. Allahumma laa maani'a limaa a'thayta wa laa mu'thiya limaa mana'ta wa laa yanfa'u dzal jaddi minkal jaddu.",
            translation = "Tiada Tuhan selain Allah Yang Maha Esa tiada sekutu bagi-Nya. Bagi-Nya kerajaan dan bagi-Nya segala puji, Dia Yang Menghidupkan dan Mematikan, dan Dia Maha Kuasa atas segala sesuatu. Ya Allah tiada yang dapat mencegah apa yang Engkau beri, dan tiada yang dapat memberi apa yang Engkau cegah, dan tiada berguna kekayaan orang kaya di hadapan-Mu.",
            sourceOrFadhilah = "HR. Bukhari no. 844 dan Muslim no. 593."
        ),
        DoaItem(
            id = "dzikir_tasbih_tahmid_takbir",
            title = "4. Tasbih, Tahmid, Takbir (33x)",
            arabicText = "سُبْحَانَ اللّٰهِ (٣٣×)، الْحَمْدُ لِلّٰهِ (٣٣×)، اللّٰهُ أَكْبَرُ (٣٣×)",
            latinText = "Subhaanallaah (33x), Alhamdu lillaah (33x), Allaahu Akbar (33x).",
            translation = "Maha Suci Allah (33x), Segala puji bagi Allah (33x), Allah Maha Besar (33x).",
            sourceOrFadhilah = "Dihapuskan dosa-dosanya walaupun sebanyak buih di lautan (HR. Muslim no. 597)."
        ),
        DoaItem(
            id = "dzikir_penutup_100",
            title = "5. Kalimat Penggenap 100",
            arabicText = "لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            latinText = "Laa ilaaha illallaahu wahdahu laa syariika lah, lahul mulku wa lahul hamdu wa huwa 'alaa kulli syay-in qadiir.",
            translation = "Tiada Tuhan selain Allah Yang Maha Esa tiada sekutu bagi-Nya, bagi-Nya kerajaan dan segala puji, dan Dia Maha Kuasa atas segala sesuatu.",
            sourceOrFadhilah = "Menyempurnakan jumlah 100 dzikir setelah sholat."
        )
    )

    val kumpulanDoaMustajab: List<DoaItem> = listOf(
        DoaItem(
            id = "doa_selamat",
            title = "Doa Keselamatan Agama, Dunia & Akhirat",
            arabicText = "اللّٰهُمَّ إِنَّا نَسْأَلُكَ سَلَامَةً فِي الدِّينِ، وَعَافِيَةً فِي الْجَسَدِ، وَزِيَادَةً فِي الْعِلْمِ، وَبَرَكَةً فِي الرِّزْقِ، وَتَوْبَةً قَبْلَ الْمَوْتِ، وَرَحْمَةً عِنْدَ الْمَوْتِ، وَمَغْفِرَةً بَعْدَ الْمَوْتِ. اللّٰهُمَّ هَوِّنْ عَلَيْنَا فِي سَكَرَاتِ الْمَوْتِ، وَالنَّجَاةَ مِنَ النَّارِ، وَالْعَفْوَ عِنْدَ الْحِسَابِ",
            latinText = "Allahumma innaa nas-aluka salaamatan fid diin, wa 'aafiyatan fil jasad, wa ziyaadatan fil 'ilmi, wa barakatan fir rizq, wa tawbatan qablal mawt, wa rahmatan 'indal mawt, wa maghfiratam ba'dal mawt. Allahumma hawwin 'alaynaa fii sakaraatil mawti wan najaata minan naari wal 'afwa 'indal hisaab.",
            translation = "Ya Allah, kami memohon kepada-Mu keselamatan dalam agama, kesehatan raga, bertambahnya ilmu, keberkahan rezeki, tobat sebelum maut, rahmat saat maut, dan ampunan setelah maut. Ya Allah, mudahkanlah kami saat menghadapi sakaratul maut, selamatkanlah kami dari siksa api neraka, dan anugerahilah kemaafan saat hisab.",
            sourceOrFadhilah = "Doa komprehensif yang lazim dibaca para ulama setelah sholat fardhu."
        ),
        DoaItem(
            id = "doa_orang_tua",
            title = "Doa Ampunan Untuk Kedua Orang Tua",
            arabicText = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            latinText = "Rabbighfir lii wa liwaalidayya warhamhumaa kamaa rabbayaanii shaghiiraa.",
            translation = "Wahai Tuhanku, ampunilah aku dan kedua orang tuaku, dan sayangilah mereka berdua sebagaimana mereka telah menyayangiku dan mendidikku di waktu kecil.",
            sourceOrFadhilah = "Berdasarkan QS. Al-Isra ayat 24, wujud bakti anak saleh (birrul walidain)."
        ),
        DoaItem(
            id = "doa_ilmu_rezeki",
            title = "Doa Memohon Ilmu, Rezeki Halal & Amal Diterima",
            arabicText = "اللّٰهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            latinText = "Allahumma innii as-aluka 'ilman naafi'aa, wa rizqan thayyibaa, wa 'amalam mutaqabbalaa.",
            translation = "Ya Allah, sesungguhnya aku memohon kepada-Mu ilmu yang bermanfaat, rezeki yang baik (halal), dan amal ibadah yang diterima.",
            sourceOrFadhilah = "HR. Ibnu Majah no. 925, dibaca Rasulullah SAW setelah sholat Subuh."
        ),
        DoaItem(
            id = "doa_ketetapan_iman",
            title = "Doa Keteguhan Hati & Iman",
            arabicText = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَىٰ دِينِكَ وَعَلَىٰ طَاعَتِكَ",
            latinText = "Yaa muqallibal quluub, tsabbit qalbii 'alaa diinika wa 'alaa thaa'atik.",
            translation = "Wahai Dzat yang membolak-balikkan hati, teguhkanlah hatiku di atas agama-Mu dan dalam ketaatan kepada-Mu.",
            sourceOrFadhilah = "HR. Tirmidzi no. 2140 dan Ahmad."
        ),
        DoaItem(
            id = "doa_sayyidul_istighfar",
            title = "Sayyidul Istighfar (Penghulu Istighfar)",
            arabicText = "اللّٰهُمَّ أَنْتَ رَبِّي لَا إِلٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            latinText = "Allahumma anta rabbii laaa ilaaha illaa anta, khalaqtanii wa ana 'abduka, wa ana 'alaa 'ahdika wa wa'dika mastatha'tu, a'uudzu bika min syarri maa shana'tu, abuu-u laka bini'matika 'alayya, wa abuu-u laka bi dzambii faghfir lii fa-innahuu laa yaghfirudz dzunuuba illaa anta.",
            translation = "Ya Allah, Engkaulah Tuhanku tiada Tuhan selain Engkau. Engkau menciptakanku dan aku adalah hamba-Mu. Aku berusaha menepati janji-Mu semampuku. Aku berlindung kepada-Mu dari keburukan perbuatanku. Aku mengakui nikmat-Mu kepadaku dan aku mengakui dosaku, maka ampunilah aku karena tiada yang mengampuni dosa selain Engkau.",
            sourceOrFadhilah = "Barangsiapa membacanya di waktu pagi/petang lalu wafat, ia termasuk penghuni surga (HR. Bukhari no. 6306)."
        ),
        DoaItem(
            id = "doa_sapu_jagad",
            title = "Doa Sapu Jagad (Kebaikan Dunia & Akhirat)",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latinText = "Rabbanaa aatinaa fid dunyaa hasanataw wa fil aakhirati hasanataw wa qinaa 'adzaaban naar.",
            translation = "Wahai Tuhan kami, berikanlah kami kebaikan di dunia dan kebaikan di akhirat, dan lindungilah kami dari siksa api neraka.",
            sourceOrFadhilah = "QS. Al-Baqarah ayat 201, doa yang paling sering dibaca Rasulullah SAW."
        )
    )
}
