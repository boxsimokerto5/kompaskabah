package com.example.data.network.hadith

object HadithFallbackData {

    fun getFallbackHadiths(bookId: String, sectionNumber: Int): List<HadithParsedItem>? {
        return when (bookId) {
            "bukhari" -> when (sectionNumber) {
                1 -> bukhariBab1
                2 -> bukhariBab2
                else -> null
            }
            "muslim" -> when (sectionNumber) {
                1 -> muslimBab1
                else -> null
            }
            "tirmidhi" -> when (sectionNumber) {
                1 -> tirmidhiBab1
                else -> null
            }
            "abudawud" -> when (sectionNumber) {
                1 -> abudawudBab1
                else -> null
            }
            "nasai" -> when (sectionNumber) {
                1 -> nasaiBab1
                else -> null
            }
            "ibnmajah" -> when (sectionNumber) {
                1 -> ibnMajahBab1
                else -> null
            }
            "malik" -> when (sectionNumber) {
                1 -> malikBab1
                else -> null
            }
            else -> null
        }
    }

    // -------------------------------------------------------------
    // SHAHIH AL-BUKHARI - BAB 1: PERMULAAN TURUNNYA WAHYU (1 - 7)
    // -------------------------------------------------------------
    private val bukhariBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "حَدَّثَنَا الْحُمَيْدِيُّ عَبْدُ اللَّهِ بْنُ الزُّبَيْرِ، قَالَ: حَدَّثَنَا سُفْيَانُ، قَالَ: حَدَّثَنَا يَحْيَى بْنُ سَعِيدٍ الأَنْصَارِيُّ، قَالَ: أَخْبَرَنِي مُحَمَّدُ بْنُ إِبْرَاهِيمَ التَّيْمِيُّ، أَنَّهُ سَمِعَ عَلْقَمَةَ بْنَ وَقَّاصٍ اللَّيْثِيَّ يَقُولُ: سَمِعْتُ عُمَرَ بْنَ الْخَطَّابِ رَضِيَ اللَّهُ عَنْهُ عَلَى الْمِنْبَرِ قَالَ: سَمِعْتُ رَسُولَ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ يَقُولُ: «إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا، أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا، فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ».",
            indonesianText = "Aku mendengar Rasulullah ﷺ bersabda: \"Sesungguhnya setiap amalan tergantung pada niatnya, dan sesungguhnya setiap orang akan mendapatkan balasan sesuai dengan apa yang ia niatkan. Barangsiapa yang hijrahnya karena (mencari keuntungan) dunia yang ingin diraihnya atau karena wanita yang ingin dinikahinya, maka hijrahnya itu menuju kepada apa yang ia tuju.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 2,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنْ عَائِشَةَ أُمِّ الْمُؤْمِنِينَ رَضِيَ اللَّهُ عَنْهَا: أَنَّ الْحَارِثَ بْنَ هِشَامٍ رَضِيَ اللَّهُ عَنْهُ سَأَلَ رَسُولَ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ فَقَالَ: يَا رَسُولَ اللَّهِ، كَيْفَ يَأْتِيكَ الْوَحْيُ؟ فَقَالَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ: «أَحْيَانًا يَأْتِينِي مِثْلَ صَلْصَلَةِ الْجَرَسِ - وَهُوَ أَشَدُّهُ عَلَيَّ - فَيُفْصَمُ عَنِّي وَقَدْ وَعَيْتُ عَنْهُ مَا قَالَ، وَأَحْيَانًا يَتَمَثَّلُ لِيَ الْمَلَكُ رَجُلاً فَيُكَلِّمُنِي فَأَعِي مَا يَقُولُ». قَالَتْ عَائِشَةُ رَضِيَ اللَّهُ عَنْهَا: وَلَقَدْ رَأَيْتُهُ يَنْزِلُ عَلَيْهِ الْوَحْيُ فِي الْيَوْمِ الشَّدِيدِ الْبَرْدِ، فَيَفْصِمُ عَنْهُ وَإِنَّ جَبِينَهُ لَيَتَفَصَّدُ عَرَقًا.",
            indonesianText = "Dari Aisyah Ummul Mukminin bahwa Al-Harits bin Hisyam bertanya kepada Rasulullah ﷺ: \"Wahai Rasulullah, bagaimanakah wahyu datang kepadamu?\" Rasulullah ﷺ menjawab: \"Terkadang wahyu datang kepadaku seperti gemerincing lonceng, dan itulah yang paling berat bagiku. Lalu terputus dariku ketika aku telah memahami apa yang disampaikannya. Dan terkadang malaikat menjelma sebagai seorang lelaki lalu berbicara kepadaku sehingga aku memahami apa yang dikatakannya.\" Aisyah berkata: \"Sungguh aku melihat beliau saat turunnya wahyu pada hari yang sangat dingin, lalu setelah wahyu itu berhenti, dahi beliau bercucuran keringat.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 3,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنْ عَائِشَةَ أُمِّ الْمُؤْمِنِينَ أَنَّهَا قَالَتْ: أَوَّلُ مَا بُدِئَ بِهِ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ مِنَ الْوَحْيِ الرُّؤْيَا الصَّالِحَةُ فِي النَّوْمِ، فَكَانَ لاَ يَرَى رُؤْيَا إِلاَّ جَاءَتْ مِثْلَ فَلَقِ الصُّبْحِ، ثُمَّ حُبِّبَ إِلَيْهِ الْخَلاَءُ، وَكَانَ يَخْلُو بِغَارِ حِرَاءٍ فَيَتَحَنَّثُ فِيهِ... حَتَّى جَاءَهُ الْحَقُّ وَهُوَ فِي غَارِ حِرَاءٍ، فَجَاءَهُ الْمَلَكُ فَقَالَ: اقْرَأْ، قَالَ: «مَا أَنَا بِقَارِئٍ»، قَالَ: «فَأَخَذَنِي فَغَطَّنِي حَتَّى بَلَغَ مِنِّي الْجَهْدَ ثُمَّ أَرْسَلَنِي»، فَقَالَ: اقْرَأْ، قُلْتُ: «مَا أَنَا بِقَارِئٍ»، فَأَخَذَنِي فَغَطَّنِي الثَّانِيَةَ حَتَّى بَلَغَ مِنِّي الْجَهْدَ ثُمَّ أَرْسَلَنِي، فَقَالَ: اقْرَأْ، فَقُلْتُ: «مَا أَنَا بِقَارِئٍ»، فَأَخَذَنِي فَغَطَّنِي الثَّالِثَةَ ثُمَّ أَرْسَلَنِي، فَقَالَ: {اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ . خَلَقَ الإِنْسَانَ مِنْ عَلَقٍ . اقْرَأْ وَرَبُّكَ الأَكْرَمُ}.",
            indonesianText = "Dari Aisyah Ummul Mukminin bahwa beliau berkata: \"Awal mula wahyu yang datang kepada Rasulullah ﷺ adalah mimpi yang benar saat tidur. Tidaklah beliau melihat mimpi melainkan datang seperti fajar subuh. Kemudian beliau menyukai menyendiri, beliau beribadah di Gua Hira... hingga kebenaran datang saat beliau di Gua Hira. Datanglah Malaikat (Jibril) lalu berkata: 'Bacalah!' Beliau menjawab: 'Aku tidak bisa membaca.' Nabi ﷺ menceritakan: 'Malaikat itu memelukku dan mendekapku kuat-kuat hingga aku kepayahan, lalu melepaskanku dan berkata: Bacalah!' Aku menjawab: 'Aku tidak bisa membaca.' Lalu ia memelukku kedua kalinya hingga aku kepayahan kemudian melepaskanku dan berkata: 'Bacalah!' Aku menjawab: 'Aku tidak bisa membaca.' Maka ia memelukku ketiga kalinya lalu melepaskanku dan berkata: 'Bacalah dengan menyebut nama Tuhanmu yang menciptakan. Dia telah menciptakan manusia dari segumpal darah. Bacalah, dan Tuhanmulah Yang Maha Mulia' (QS. Al-'Alaq: 1-3).\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 4,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنْ جَابِرِ بْنِ عَبْدِ اللَّهِ الأَنْصَارِيِّ رَضِيَ اللَّهُ عَنْهُمَا، قَالَ وَهُوَ يُحَدِّثُ عَنْ فَتْرَةِ الْوَحْيِ: فَقَالَ فِي حَدِيثِهِ: «بَيْنَا أَنَا أَمْشِي إِذْ سَمِعْتُ صَوْتًا مِنَ السَّمَاءِ، فَرَفَعْتُ بَصَرِي، فَإِذَا الْمَلَكُ الَّذِي جَاءَنِي بِحِرَاءٍ جَالِسٌ عَلَى كُرْسِيٍّ بَيْنَ السَّمَاءِ وَالأَرْضِ، فَرُعِبْتُ مِنْهُ، فَرَجَعْتُ فَقُلْتُ: زَمِّلُونِي زَمِّلُونِي»، فَأَنْزَلَ اللَّهُ تَعَالَى: {يَا أَيُّهَا الْمُدَّثِّرُ . قُمْ فَأَنْذِرْ . وَرَبَّكَ فَكَبِّرْ . وَثِيَابَكَ فَطَهِّرْ . وَالرُّجْزَ فَاهْجُرْ}، فَحَمِيَ الْوَحْيُ وَتَتَابَعَ.",
            indonesianText = "Dari Jabir bin Abdullah ra. ketika menceritakan tentang masa terputusnya wahyu, Rasulullah ﷺ bersabda: \"Ketika aku sedang berjalan, tiba-tiba aku mendengar suara dari langit. Aku menengadahkan pandanganku, ternyata malaikat yang pernah mendatangiku di Gua Hira sedang duduk di atas kursi antara langit dan bumi. Aku merasa gemetar ketakutan, lalu pulang dan berkata: 'Selimutilah aku! Selimutilah aku!' Maka Allah Ta'ala menurunkan firman-Nya: 'Wahai orang yang berselimut! Bangunlah, lalu berilah peringatan! Dan agungkanlah Tuhanmu, bersihkanlah pakaianmu, dan tinggalkanlah segala perbuatan dosa!' (QS. Al-Muddatstsir: 1-5). Setelah itu wahyu datang bertubi-tubi tanpa henti.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 5,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنِ ابْنِ عَبَّاسٍ رَضِيَ اللَّهُ عَنْهُمَا فِي قَوْلِهِ تَعَالَى: {لاَ تُحَرِّكْ بِهِ لِسَانَكَ لِتَعْجَلَ بِهِ}، قَالَ: كَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ يُعَالِجُ مِنَ التَّنْزِيلِ شِدَّةً، وَكَانَ مِمَّا يُحَرِّكُ شَفَتَيْهِ... فَأَنْزَلَ اللَّهُ تَعَالَى: {لاَ تُحَرِّكْ بِهِ لِسَانَكَ لِتَعْجَلَ بِهِ . إِنَّ عَلَيْنَا جَمْعَهُ وَقُرْآنَهُ}، قَالَ: جَمْعُهُ لَكَ فِي صَدْرِكَ وَتَقْرَأَهُ، {فَإِذَا قَرَأْنَاهُ فَاتَّبِعْ قُرْآنَهُ}، قَالَ: فَاسْتَمِعْ لَهُ وَأَنْصِتْ، {ثُمَّ إِنَّ عَلَيْنَا بَيَانَهُ}، ثُمَّ إِنَّ عَلَيْنَا أَنْ تَقْرَأَهُ. فَكَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ بَعْدَ ذَلِكَ إِذَا أَتَاهُ جِبْرِيلُ اسْتَمَعَ، فَإِذَا انْطَلَقَ جِبْرِيلُ قَرَأَهُ النَّبِيُّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ كَمَا قَرَأَهُ.",
            indonesianText = "Dari Ibnu Abbas ra. mengenai firman Allah: \"Jangan engkau gerakkan lidahmu untuk (membaca) Al-Qur'an karena hendak cepat-cepat (menguasai)nya\" (QS. Al-Qiyamah: 16). Ibnu Abbas berkata: Rasulullah ﷺ berusaha sekuat tenaga menahan beban saat wahyu turun, dan beliau menggerak-gerakkan kedua bibirnya tergesa-gesa menghafal. Maka Allah menurunkan ayat: \"Sesungguhnya Kami yang akan mengumpulkannya (di dadamu) dan membacakannya. Apabila Kami telah selesai membacakannya maka ikutilah bacaannya itu. Kemudian sesungguhnya Kami yang akan menjelaskannya.\" Maka setelah itu jika Jibril datang, Rasulullah ﷺ menyimak dengan tenang, dan ketika Jibril pergi, Nabi ﷺ membacanya persis sebagaimana yang dibacakan Jibril.",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 6,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنِ ابْنِ عَبَّاسٍ قَالَ: كَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ أَجْوَدَ النَّاسِ، وَكَانَ أَجْوَدُ مَا يَكُونُ فِي رَمَضَانَ حِينَ يَلْقَاهُ جِبْرِيلُ، وَكَانَ يَلْقَاهُ فِي كُلِّ لَيْلَةٍ مِنْ رَمَضَانَ فَيُدَارِسُهُ الْقُرْآنَ، فَلَرَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ أَجْوَدُ بِالْخَيْرِ مِنَ الرِّيحِ الْمُرْسَلَةِ.",
            indonesianText = "Dari Ibnu Abbas ra. berkata: \"Rasulullah ﷺ adalah manusia yang paling dermawan, dan beliau menjadi jauh lebih dermawan lagi di bulan Ramadhan ketika ditemui oleh Malaikat Jibril. Jibril menemui beliau pada setiap malam di bulan Ramadhan untuk bertadarus Al-Qur'an bersama. Sungguh Rasulullah ﷺ saat itu lebih cepat memberi kebaikan daripada angin yang berhembus kencang membawakan rahmat hujan.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 7,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 1,
            sectionName = "Bab 1: Permulaan Turunnya Wahyu",
            arabicText = "عَنْ عَبْدِ اللَّهِ بْنِ عَبَّاسٍ أَنَّ أَبَا سُفْيَانَ بْنَ حَرْبٍ أَخْبَرَهُ: أَنَّ هِرَقْلَ أَرْسَلَ إِلَيْهِ فِي رَكْبٍ مِنْ قُرَيْشٍ، وَكَانُوا تِجَارًا بِالشَّأْمِ فِي الْمُدَّةِ الَّتِي كَانَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ مَادَّ فِيهَا أَبَا سُفْيَانَ وَكُفَّارَ قُرَيْشٍ... فَسَأَلَهُ هِرَقْلُ عَنْ صِفَاتِ النَّبِيِّ ﷺ، فَقَالَ: مَاذَا يَأْمُرُكُمْ؟ قَالَ أَبُو سُفْيَانَ: قُلْتُ: يَقُولُ: «اعْبُدُوا اللَّهَ وَحْدَهُ وَلاَ تُشْرِكُوا بِهِ شَيْئًا، وَاتْرُكُوا مَا يَقُولُ آبَاؤُكُمْ، وَيَأْمُرُنَا بِالصَّلاَةِ وَالصِّدْقِ وَالْعَفَافِ وَالصِّلَةِ». فَقَالَ هِرَقْلُ: إِنْ كَانَ مَا تَقُولُ حَقًّا فَسَيَمْلِكُ مَوْضِعَ قَدَمَيَّ هَاتَيْنِ.",
            indonesianText = "Dari Ibnu Abbas bahwa Abu Sufyan bin Harb menceritakan ketika ia dipanggil oleh Kaisar Romawi Heraklius di Syam. Heraklius menginterogasi Abu Sufyan tentang pribadi Nabi Muhammad ﷺ. Heraklius bertanya: 'Apa yang beliau perintahkan kepada kalian?' Abu Sufyan menjawab: 'Beliau bersabda: Sembahlah Allah semata dan jangan mempersekutukan-Nya dengan sesuatu pun, tinggalkanlah tradisi syirik nenek moyang kalian. Beliau memerintahkan kami untuk mendirikan sholat, berlaku jujur, menjaga kesucian diri, dan menyambung silaturahmi.' Mendengar itu Kaisar Heraklius berkata: 'Jika apa yang engkau katakan benar, niscaya ia akan menguasai tempat di mana kedua telapak kakiku ini berpijak.'",
            gradeTag = "🟢 Shahih Mutlak"
        )
    )

    // -------------------------------------------------------------
    // SHAHIH AL-BUKHARI - BAB 2: KITAB KEIMANAN (8 - 10)
    // -------------------------------------------------------------
    private val bukhariBab2 = listOf(
        HadithParsedItem(
            number = 8,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 2,
            sectionName = "Bab 2: Kitab Keimanan",
            arabicText = "عَنِ ابْنِ عُمَرَ رَضِيَ اللَّهُ عَنْهُمَا قَالَ: قَالَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ: «بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالْحَجِّ، وَصَوْمِ رَمَضَانَ».",
            indonesianText = "Dari Ibnu Umar ra. berkata: Rasulullah ﷺ bersabda: \"Islam dibangun di atas lima perkara: bersaksi bahwa tiada sesembahan yang berhak disembah selain Allah dan Muhammad adalah utusan Allah, mendirikan sholat, menunaikan zakat, menunaikan haji ke Baitullah, dan berpuasa di bulan Ramadhan.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 9,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 2,
            sectionName = "Bab 2: Kitab Keimanan",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ، عَنِ النَّبِيِّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «الإِيمَانُ بِضْعٌ وَسِتُّونَ شُعْبَةً، وَالْحَيَاءُ شُعْبَةٌ مِنَ الإِيمَانِ».",
            indonesianText = "Dari Abu Hurairah ra., dari Nabi ﷺ bersabda: \"Iman itu memiliki lebih dari enam puluh cabang, dan sifat malu adalah salah satu cabang dari keimanan.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 10,
            bookId = "bukhari",
            bookName = "Shahih Al-Bukhari",
            sectionNumber = 2,
            sectionName = "Bab 2: Kitab Keimanan",
            arabicText = "عَنْ عَبْدِ اللَّهِ بْنِ عَمْرٍو رَضِيَ اللَّهُ عَنْهُمَا، عَنِ النَّبِيِّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ، وَالْمُهَاجِرُ مَنْ هَجَرَ مَا نَهَى اللَّهُ عَنْهُ».",
            indonesianText = "Dari Abdullah bin Amr ra., dari Nabi ﷺ bersabda: \"Seorang muslim sejati adalah orang yang kaum muslimin lainnya selamat dari kejahatan lisan dan tangannya, dan orang yang berhijrah sejati adalah orang yang meninggalkan segala apa yang dilarang oleh Allah.\"",
            gradeTag = "🟢 Shahih Mutlak"
        )
    )

    // -------------------------------------------------------------
    // SHAHIH MUSLIM - BAB 1: KITAB IMAN & TAUHID (1 - 3)
    // -------------------------------------------------------------
    private val muslimBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "muslim",
            bookName = "Shahih Muslim",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Iman & Tauhid",
            arabicText = "عَنْ عُمَرَ بْنِ الْخَطَّابِ رَضِيَ اللَّهُ عَنْهُ قَالَ: بَيْنَمَا نَحْنُ عِنْدَ رَسُولِ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ ذَاتَ يَوْمٍ إِذْ طَلَعَ عَلَيْنَا رَجُلٌ شَدِيدُ بَيَاضِ الثِّيَابِ شَدِيدُ سَوَادِ الشَّعَرِ لاَ يُرَى عَلَيْهِ أَثَرُ السَّفَرِ وَلاَ يَعْرِفُهُ مِنَّا أَحَدٌ... فَقَالَ: يَا مُحَمَّدُ أَخْبِرْنِي عَنِ الإِسْلاَمِ؟ فَقَالَ: «الإِسْلاَمُ أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلاً». قَالَ: صَدَقْتَ... قَالَ: فَأَخْبِرْنِي عَنِ الإِيمَانِ؟ قَالَ: «أَنْ تُؤْمِنَ بِاللَّهِ وَمَلاَئِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ وَالْيَوْمِ الآخِرِ وَتُؤْمِنَ بِالْقَدَرِ خَيْرِهِ وَشَرِّهِ». قَالَ: صَدَقْتَ... قَالَ: فَأَخْبِرْنِي عَنِ الإِحْسَانِ؟ قَالَ: «أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ»... ثُمَّ قَالَ النَّبِيُّ ﷺ: «يَا عُمَرُ، هَذَا جِبْرِيلُ أَتَاكُمْ يُعَلِّمُكُمْ دِينَكُمْ».",
            indonesianText = "Dari Umar bin Khaththab ra. (Hadits Jibril yang Termasyhur): Suatu hari ketika kami duduk bersama Rasulullah ﷺ, muncul seorang pria berpakaian sangat putih dan berambut sangat hitam, tidak tampak tanda perjalanan padanya. Ia bertanya tentang Islam, Rasulullah ﷺ menjawab rukun Islam; ia bertanya tentang Iman, Rasulullah ﷺ menjawab 6 rukun iman (beriman kepada Allah, malaikat-Nya, kitab-kitab-Nya, rasul-rasul-Nya, hari akhir, dan takdir baik maupun buruk); ia bertanya tentang Ihsan, beliau menjawab: \"Engkau beribadah kepada Allah seolah-olah engkau melihat-Nya, dan jika engkau tidak melihat-Nya, sesungguhnya Dia melihatmu.\" Di akhir hadits Nabi ﷺ bersabda: \"Wahai Umar, itu adalah Jibril yang datang mengajarkan agama kepada kalian.\"",
            gradeTag = "🟢 Shahih Mutlak"
        ),
        HadithParsedItem(
            number = 2,
            bookId = "muslim",
            bookName = "Shahih Muslim",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Iman & Tauhid",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ، أَنَّ رَسُولَ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «وَالَّذِي نَفْسُ مُحَمَّدٍ بِيَدِهِ، لاَ يَسْمَعُ بِي أَحَدٌ مِنْ هَذِهِ الأُمَّةِ يَهُودِيٌّ وَلاَ نَصْرَانِيٌّ، ثُمَّ يَمُوتُ وَلَمْ يُؤْمِنْ بِالَّذِي أُرْسِلْتُ بِهِ، إِلاَّ كَانَ مِنْ أَصْحَابِ النَّارِ».",
            indonesianText = "Dari Abu Hurairah ra. bahwa Rasulullah ﷺ bersabda: \"Demi Dzat yang jiwa Muhammad berada di tangan-Nya, tidaklah seorang pun dari umat ini, baik Yahudi maupun Nasrani, yang mendengar tentang risalahku lalu mati dalam keadaan tidak beriman kepada apa yang aku diutus dengannya, melainkan ia termasuk penghuni neraka.\"",
            gradeTag = "🟢 Shahih Mutlak"
        )
    )

    // -------------------------------------------------------------
    // JAMI' AT-TIRMIDZI - BAB 1: KITAB BERSUCI (1 - 2)
    // -------------------------------------------------------------
    private val tirmidhiBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "tirmidhi",
            bookName = "Jami' At-Tirmidzi",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci (Thaharah)",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ قَالَ: قَالَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ: «مِفْتَاحُ الصَّلاَةِ الطُّهُورُ، وَتَحْرِيمُهَا التَّكْبِيرُ، وَتَحْلِيلُهَا التَّسْلِيمُ».",
            indonesianText = "Dari Abu Hurairah ra. berkata: Rasulullah ﷺ bersabda: \"Kunci sahnya sholat adalah bersuci (thaharah), pengharamannya (memulai sholat dan mengharamkan perkara dunia) adalah takbiratul ihram, dan penghalalannya (mengakhiri sholat) adalah salam.\"",
            gradeTag = "🟡 Hasan & Shahih"
        ),
        HadithParsedItem(
            number = 2,
            bookId = "tirmidhi",
            bookName = "Jami' At-Tirmidzi",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci (Thaharah)",
            arabicText = "عَنْ عَلِيٍّ رَضِيَ اللَّهُ عَنْهُ، عَنِ النَّبِيِّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «إِذَا اسْتَيْقَظَ أَحَدُكُمْ مِنْ نَوْمِهِ فَلاَ يَغْمِسْ يَدَهُ فِي الإِنَاءِ حَتَّى يَغْسِلَهَا ثَلاَثًا، فَإِنَّهُ لاَ يَدْرِي أَيْنَ بَاتَتْ يَدُهُ».",
            indonesianText = "Dari Ali ra. dari Nabi ﷺ bersabda: \"Apabila salah seorang di antara kalian bangun dari tidurnya, janganlah ia mencelupkan tangannya ke dalam bejana air sebelum ia membasuhnya tiga kali, karena ia tidak mengetahui di mana tangannya bermalam.\"",
            gradeTag = "🟡 Hasan & Shahih"
        )
    )

    // -------------------------------------------------------------
    // SUNAN ABU DAWUD - BAB 1: THAHARAH (1 - 2)
    // -------------------------------------------------------------
    private val abudawudBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "abudawud",
            bookName = "Sunan Abu Dawud",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci (Thaharah)",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ قَالَ: قَالَ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ: «لاَ يَقْبَلُ اللَّهُ صَلاَةَ أَحَدِكُمْ إِذَا أَحْدَثَ حَتَّى يَتَوَضَّأَ».",
            indonesianText = "Dari Abu Hurairah ra. berkata: Rasulullah ﷺ bersabda: \"Allah tidak menerima sholat salah seorang dari kalian apabila ia berhadats hingga ia berwudhu terlebih dahulu.\"",
            gradeTag = "🟡 Shahih & Hasan"
        ),
        HadithParsedItem(
            number = 2,
            bookId = "abudawud",
            bookName = "Sunan Abu Dawud",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci (Thaharah)",
            arabicText = "عَنْ أَبِي سَعِيدٍ الْخُدْرِيِّ رَضِيَ اللَّهُ عَنْهُ قَالَ: قِيلَ: يَا رَسُولَ اللَّهِ، أَنَتَوَضَّأُ مِنْ بِئْرِ بُضَاعَةَ؟ فَقَالَ رَسُولُ اللَّهِ ﷺ: «إِنَّ الْمَاءَ طَهُورٌ لاَ يُنَجِّسُهُ شَيْءٌ».",
            indonesianText = "Dari Abu Sa'id Al-Khudri ra. berkata: Ditanyakan kepada Nabi ﷺ: 'Wahai Rasulullah, bolehkah kami berwudhu dari sumur Budha'ah?' Rasulullah ﷺ menjawab: 'Sesungguhnya air itu suci dan mensucikan, tidak ada sesuatu pun yang menajiskannya (selama tidak berubah bau, rasa, atau warnanya).'",
            gradeTag = "🟡 Shahih & Hasan"
        )
    )

    // -------------------------------------------------------------
    // SUNAN AN-NASA'I - BAB 1: BERSUCI (1 - 2)
    // -------------------------------------------------------------
    private val nasaiBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "nasai",
            bookName = "Sunan An-Nasa'i",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci",
            arabicText = "عَنْ عَائِشَةَ رَضِيَ اللَّهُ عَنْهَا، عَنِ النَّبِيِّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «السِّوَاكُ مَطْهَرَةٌ لِلْفَمِ مَرْضَاةٌ لِلرَّبِّ».",
            indonesianText = "Dari Aisyah ra., dari Nabi ﷺ bersabda: \"Bersiwak itu membersihkan mulut dan mendatangkan keridhaan dari Rabb (Allah SWT).\"",
            gradeTag = "🟡 Shahih & Hasan"
        ),
        HadithParsedItem(
            number = 2,
            bookId = "nasai",
            bookName = "Sunan An-Nasa'i",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ قَالَ: قَالَ رَسُولُ اللَّهِ ﷺ: «لَوْلاَ أَنْ أَشُقَّ عَلَى أُمَّتِي لأَمَرْتُهُمْ بِالسِّوَاكِ عِنْدَ كُلِّ صَلاَةٍ».",
            indonesianText = "Dari Abu Hurairah ra. berkata: Rasulullah ﷺ bersabda: \"Seandainya tidak memberatkan umatku, niscaya akan kuperintahkan mereka untuk bersiwak setiap kali hendak mendirikan sholat.\"",
            gradeTag = "🟡 Shahih & Hasan"
        )
    )

    // -------------------------------------------------------------
    // SUNAN IBNU MAJAH - BAB 1: BERSUCI (267 - 268)
    // -------------------------------------------------------------
    private val ibnMajahBab1 = listOf(
        HadithParsedItem(
            number = 267,
            bookId = "ibnmajah",
            bookName = "Sunan Ibnu Majah",
            sectionNumber = 1,
            sectionName = "Bab 1: Kitab Bersuci & Sunnahnya",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ، أَنَّ رَسُولَ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ قَالَ: «لاَ وُضُوءَ لِمَنْ لَمْ يَذْكُرِ اسْمَ اللَّهِ عَلَيْهِ».",
            indonesianText = "Dari Abu Hurairah ra. bahwa Rasulullah ﷺ bersabda: \"Tidak sempurna wudhu bagi orang yang tidak menyebut nama Allah (membaca bismillah) atas wudhunya.\"",
            gradeTag = "🟠 Hasan & Dha'if Ringan"
        )
    )

    // -------------------------------------------------------------
    // AL-MUWATTA IMAM MALIK - BAB 1: WAKTU SHOLAT (1 - 2)
    // -------------------------------------------------------------
    private val malikBab1 = listOf(
        HadithParsedItem(
            number = 1,
            bookId = "malik",
            bookName = "Al-Muwatta Imam Malik",
            sectionNumber = 1,
            sectionName = "Bab 1: Waktu-Waktu Sholat",
            arabicText = "عَنِ ابْنِ شِهَابٍ أَنَّ عُمَرَ بْنَ عَبْدِ الْعَزِيزِ أَخَّرَ الصَّلاَةَ يَوْمًا، فَدَخَلَ عَلَيْهِ عُرْوَةُ بْنُ الزُّبَيْرِ فَأَخْبَرَهُ: أَنَّ الْمُغِيرَةَ بْنَ شُعْبَةَ أَخَّرَ الصَّلاَةَ يَوْمًا وَهُوَ بِالْكُوفَةِ، فَدَخَلَ عَلَيْهِ أَبُو مَسْعُودٍ الأَنْصَارِيُّ فَقَالَ: مَا هَذَا يَا مُغِيرَةُ؟ أَلَيْسَ قَدْ عَلِمْتَ أَنَّ جِبْرِيلَ نَزَلَ فَصَلَّى فَصَلَّى رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ... ثُمَّ قَالَ: بِهَذَا أُمِرْتُ.",
            indonesianText = "Dari Ibnu Syihab menceritakan bahwa Malaikat Jibril 'alaihissalam turun mengimami Rasulullah ﷺ untuk mengajarkan batas awal dan akhir setiap waktu sholat fardhu lima waktu, kemudian bersabda: 'Dengan inilah engkau diperintahkan.'",
            gradeTag = "🟢 Shahih Ash-Shahih"
        )
    )
}
