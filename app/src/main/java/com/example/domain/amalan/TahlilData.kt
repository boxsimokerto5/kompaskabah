package com.example.domain.amalan

object TahlilData {
    val items: List<TahlilItem> = listOf(
        TahlilItem(
            orderNumber = 1,
            sectionTitle = "1. Pengantar Al-Fatihah (Hadhrat)",
            arabicText = "إِلَى حَضْرَةِ النَّبِيِّ الْمُصْطَفَى مُحَمَّدٍ صَلَّى اللّٰهُ عَلَيْهِ وَسَلَّمَ وَآلِهِ وَأَصْحَابِهِ وَأَزْوَاجِهِ وَذُرِّيَّاتِهِ، ثُمَّ إِلَى أَرْوَاحِ جَمِيعِ أَهْلِ الْقُبُورِ مِنَ الْمُسْلِمِينَ وَالْمُسْلِمَاتِ، الْفَاتِحَةُ...",
            latinText = "Ilaa hadhratin nabiyyil musthafaa Muhammadin shallallaahu 'alayhi wa sallam wa aalihii wa ash-haabihii wa azwaajihii wa dzurriyyatihii, tsumma ilaa arwaahi jamii'i ahlil qubuuri minal muslimiina wal muslimaat, Al-Faatihah...",
            translation = "Kepada hadirat Nabi pilihan Muhammad SAW, segenap keluarga, sahabat, istri, dan keturunan beliau, kemudian kepada seluruh arwah ahli kubur kaum muslimin dan muslimat. (Membaca Al-Fatihah).",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 2,
            sectionTitle = "2. Membaca Surat Al-Ikhlas",
            arabicText = "قُلْ هُوَ اللّٰهُ أَحَدٌ (١) اللّٰهُ الصَّمَدُ (٢) لَمْ يَلِدْ وَلَمْ يُولَدْ (٣) وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ (٤)",
            latinText = "Qul huwallaahu ahad. Allaahush shamad. Lam yalid wa lam yuulad. Wa lam yakul lahuu kufuwan ahad.",
            translation = "Katakanlah (Muhammad): 'Dialah Allah, Yang Maha Esa. Allah tempat meminta segala sesuatu. (Allah) tidak beranak dan tidak pula diperanakkan. Dan tidak ada sesuatu yang setara dengan Dia.'",
            repetitionNote = "3x"
        ),
        TahlilItem(
            orderNumber = 3,
            sectionTitle = "3. Tahlil & Takbir Antara Surat",
            arabicText = "لَا إِلٰهَ إِلَّا اللّٰهُ وَاللّٰهُ أَكْبَرُ وَلِلّٰهِ الْحَمْدُ",
            latinText = "Laa ilaaha illallaahu wallaahu akbar wa lillaahil hamd.",
            translation = "Tiada Tuhan selain Allah, Allah Maha Besar, dan bagi Allah segala puji.",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 4,
            sectionTitle = "4. Membaca Surat Al-Falaq",
            arabicText = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ (١) مِنْ شَرِّ مَا خَلَقَ (٢) وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ (٣) وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ (٤) وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ (٥)",
            latinText = "Qul a'uudzu bi rabbil falaq. Min syarri maa khalaq. Wa min syarri ghaasiqin idzaa waqab. Wa min syarrin naffaatsaati fil 'uqad. Wa min syarri haasidin idzaa hasad.",
            translation = "Katakanlah: 'Aku berlindung kepada Tuhan yang menguasai subuh (fajar), dari kejahatan (makhluk yang) Dia ciptakan...'",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 5,
            sectionTitle = "5. Membaca Surat An-Nas",
            arabicText = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ (١) مَلِكِ النَّاسِ (٢) إِلَٰهِ النَّاسِ (٣) مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ (٤) الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ (٥) مِنَ الْجِنَّةِ وَالنَّاسِ (٦)",
            latinText = "Qul a'uudzu bi rabbin naas. Malikin naas. Ilaahin naas. Min syarril waswaasil khannaas. Alladzii yuwaswisu fii shuduurin naas. Minal jinnati wan naas.",
            translation = "Katakanlah: 'Aku berlindung kepada Tuhannya manusia, Raja manusia, Sembahan manusia, dari kejahatan (bisikan) setan yang bersembunyi...'",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 6,
            sectionTitle = "6. Ayat Kursi (Surat Al-Baqarah: 255)",
            arabicText = "اللّٰهُ لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            latinText = "Allaahu laaa ilaaha illaa huwal hayyul qayyuum. Laa ta'khudzuhuu sinatuw wa laa nawm. Lahuu maa fis samaawaati wa maa fil ardh. Man dzalladzii yasyfa'u 'indahuuu illaa bi-idznih. Ya'lamu maa bayna aydiihim wa maa khalfahum, wa laa yuhiithuuna bi syay-im min 'ilmihiii illaa bimaa syaaa'. Wasi'a kursiyyuhus samaawaati wal ardh, wa laa ya-uuduhuu hifzhuhumaa, wa huwal 'aliyyul 'azhiim.",
            translation = "Allah, tidak ada tuhan selain Dia, Yang Maha Hidup, Yang terus-menerus mengurus (makhluk-Nya). Tidak mengantuk dan tidak tidur. Milik-Nya apa yang ada di langit dan di bumi... Dan Dia Maha Tinggi, Maha Besar.",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 7,
            sectionTitle = "7. Istighfar Agung",
            arabicText = "أَسْتَغْفِرُ اللّٰهَ الْعَظِيمَ الَّذِي لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
            latinText = "Astaghfirullaahal 'azhiimal ladzii laaa ilaaha illaa huwal hayyul qayyuumu wa atuubu ilayh.",
            translation = "Aku memohon ampun kepada Allah Yang Maha Agung, yang tiada Tuhan selain Dia Yang Maha Hidup lagi Maha Berdiri Sendiri, dan aku bertobat kepada-Nya.",
            repetitionNote = "3x"
        ),
        TahlilItem(
            orderNumber = 8,
            sectionTitle = "8. Inti Dzikir Tahlil",
            arabicText = "أَفْضَلُ الذِّكْرِ فَاعْلَمْ أَنَّهُ: لَا إِلٰهَ إِلَّا اللّٰهُ",
            latinText = "Afdhaludz dzikri fa'lam annahu: LAA ILAAHA ILLALLAAH.",
            translation = "Ketahuilah bahwa dzikir yang paling utama adalah: TIADA TUHAN SELAIN ALLAH.",
            repetitionNote = "33x / 100x"
        ),
        TahlilItem(
            orderNumber = 9,
            sectionTitle = "9. Penutup Kalimat Syahadat",
            arabicText = "لَا إِلٰهَ إِلَّا اللّٰهُ مُحَمَّدٌ رَسُولُ اللّٰهِ صَلَّى اللّٰهُ عَلَيْهِ وَسَلَّمَ، كَلِمَةُ حَقٍّ عَلَيْهَا نَحْيَا وَعَلَيْهَا نَمُوتُ وَبِهَا نُبْعَثُ إِنْ شَاءَ اللّٰهُ تَعَالَى مِنَ الْآمِنِينَ",
            latinText = "Laa ilaaha illallaahu Muhammadur rasuulullaahi shallallaahu 'alayhi wa sallam, kalimatu haqqin 'alayhaa nahyaa wa 'alayhaa namuutu wa bihaa nub'atsu in syaaa-allaahu ta'aalaa minal aaminiin.",
            translation = "Tiada Tuhan selain Allah, Nabi Muhammad utusan Allah SAW. Kalimat kebenaran yang dengannya kami hidup, dengannya kami mati, dan dengannya kami dibangkitkan insya Allah termasuk golongan orang-orang yang aman tenteram.",
            repetitionNote = "1x"
        ),
        TahlilItem(
            orderNumber = 10,
            sectionTitle = "10. Tasbih & Tahmid",
            arabicText = "سُبْحَانَ اللّٰهِ وَبِحَمْدِهِ سُبْحَانَ اللّٰهِ الْعَظِيمِ",
            latinText = "Subhaanallaahi wa bihamdihii subhaanallaahil 'azhiim.",
            translation = "Maha Suci Allah dengan segala puji bagi-Nya, Maha Suci Allah Yang Maha Agung.",
            repetitionNote = "33x"
        ),
        TahlilItem(
            orderNumber = 11,
            sectionTitle = "11. Doa Tahlil & Doa Arwah Lengkap",
            arabicText = "الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِينَ، حَمْدًا يُوَافِي نِعَمَهُ وَيُكَافِئُ مَزِيدَهُ. اللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ. اللّٰهُمَّ تَقَبَّلْ وَأَوْصِلْ ثَوَابَ مَا قَرَأْنَاهُ مِنَ الْقُرْآنِ الْعَظِيمِ، وَمَا هَلَّلْنَا وَمَا سَبَّحْنَا وَمَا اسْتَغْفَرْنَا وَمَا صَلَّيْنَا عَلَى سَيِّدِنَا مُحَمَّدٍ، هَدِيَّةً وَاصِلَةً وَرَحْمَةً نَازِلَةً وَبَرَكَةً شَامِلَةً إِلَى حَضْرَةِ حَبِيبِنَا وَشَفِيعِنَا رَسُولِ اللّٰهِ صَلَّى اللّٰهُ عَلَيْهِ وَسَلَّمَ، ثُمَّ إِلَى أَرْوَاحِ آبَائِنَا وَأُمَّهَاتِنَا وَأَجْدَادِنَا وَجَدَّاتِنَا وَأَمْوَاتِنَا خَاصَّةً، اللّٰهُمَّ اغْفِرْ لَهُمْ وَارْحَمْهُمْ وَعَافِهِمْ وَاعْفُ عَنْهُمْ، اللّٰهُمَّ أَنْزِلِ الرَّحْمَةَ وَالْمَغْفِرَةَ عَلَى أَهْلِ الْقُبُورِ مِنْ أَهْلِ لَا إِلٰهَ إِلَّا اللّٰهُ مُحَمَّدٌ رَسُولُ اللّٰهِ. رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latinText = "Alhamdu lillaahi rabbil 'aalamiin, hamday yuwaafii ni'amahuu wa yukaafi-u maziidah. Allahumma shalli 'alaa sayyidinaa Muhammadin wa 'alaa aali sayyidinaa Muhammad. Allahumma taqabbal wa awshil tsawaaba maa qara'naahu minal qur-aanil 'azhiim, wa maa hallalnaa wa maa sabbahnaa wa mastaghfarnaa wa maa shallaynaa 'alaa sayyidinaa Muhammad, hadiyyatan waashilatan wa rahmatan naazilatan wa barakatan syaamilatan ilaa hadhrati habiibinaa wa syafii'inaa rasuulillaahi shallallaahu 'alayhi wa sallam, tsumma ilaa arwaahi aabaa-inaa wa ummahaatinaa wa ajdaadinaa wa jaddaatinaa wa amwaatinaa khaash-shah. Allahummaghfir lahum warhamhum wa 'aafihim wa'fu 'anhum. Allahumma anzilir rahmata wal maghfirata 'alaa ahlil qubuuri min ahli laa ilaaha illallaah Muhammadur rasuulullaah. Rabbanaa aatinaa fid dunyaa hasanataw wa fil aakhirati hasanataw wa qinaa 'adzaaban naar.",
            translation = "Segala puji bagi Allah Tuhan semesta alam... Ya Allah terimalah dan sampaikanlah pahala ayat Al-Qur'an yang kami baca, tahlil, tasbih, istighfar, dan sholawat kami sebagai hadiah yang sampai dan rahmat yang melimpah kepada junjungan kami Nabi Muhammad SAW, kemudian kepada arwah ayah, ibu, kakek, nenek, dan ahli kubur kami. Ya Allah ampunilah mereka, rahmatilah mereka, dan selamatkanlah mereka dari azab neraka. Wahai Tuhan kami berilah kami kebaikan di dunia dan kebaikan di akhirat serta peliharalah kami dari siksa neraka.",
            repetitionNote = "Doa Penutup"
        )
    )
}
