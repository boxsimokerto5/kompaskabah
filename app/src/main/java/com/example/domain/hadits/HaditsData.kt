package com.example.domain.hadits

object HaditsData {
    val items: List<HaditsItem> = listOf(
        // ------------------ 1. HADITS SHAHIH ------------------
        HaditsItem(
            id = "hadits_shahih_1",
            number = 1,
            title = "Semua Amal Tergantung pada Niat",
            arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا، فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            latin = "Innamal a'maalu bin-niyyaat, wa innamaa likullim-ri-in maa nawaa. Faman kaanat hijratuhu ilaa dunyaa yushiibuhaa awil-mar-atin yankihuhaa, fa hijratuhu ilaa maa haajara ilayh.",
            translation = "Sesungguhnya setiap amalan bergantung pada niatnya, dan sesungguhnya setiap orang akan mendapatkan sesuai dengan apa yang dia niatkan. Barangsiapa yang hijrahnya karena dunia yang ingin diraihnya atau karena wanita yang ingin dinikahinya, maka hijrahnya itu menuju apa yang ia tuju.",
            narrator = "HR. Bukhari (No. 1) & Muslim (No. 1907)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Disepakati keshahihannya oleh Imam Bukhari dan Imam Muslim (Muttafaqun 'Alaih) dari sahabat Umar bin Khattab r.a. Sanad bersambung melalui perawi-perawi tsiqah tingkat tertinggi.",
            topic = HaditsTopic.NIAT_AKIDAH,
            takhrijBook = "Shahih Al-Bukhari, Kitab Bad'il Wahyi & Shahih Muslim, Kitab Al-Imarah",
            faedah = "Niat adalah barometer diterimanya amal. Amal ibadah yang besar bisa runtuh tanpa pahala bila disusupi riya' (ingin dipuji), sementara amalan duniawi mubah bisa bernilai ibadah besar bila diniatkan untuk taat kepada Allah."
        ),

        HaditsItem(
            id = "hadits_shahih_2",
            number = 2,
            title = "Lima Rukun Pondasi Agama Islam",
            arabic = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالحَجِّ، وَصَوْمِ رَمَضَانَ",
            latin = "Buniyal Islaamu 'alaa khamsin: syahaadati allaa ilaaha illallaahu wa anna Muhammadar Rasuulullaah, wa iqaamish-shalaati, wa iitaa-iz-zakaati, wal-hajji, wa shawmi Ramadhaan.",
            translation = "Islam dibangun di atas lima perkara: bersaksi bahwa tiada sesembahan yang berhak diibadahi selain Allah dan bahwa Muhammad adalah utusan Allah, mendirikan sholat, menunaikan zakat, menunaikan haji, dan berpuasa di bulan Ramadhan.",
            narrator = "HR. Bukhari (No. 8) & Muslim (No. 16)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Shahih Muttafaqun 'Alaih dari sahabat Abdullah bin Umar r.a. Merupakan salah satu hadits pilar ushuluddin (pokok-pokok agama Islam).",
            topic = HaditsTopic.SHOLAT_IBADAH,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Iman & Shahih Muslim, Kitab Al-Iman",
            faedah = "Islam diumpamakan seperti bangunan megah yang ditopang oleh 5 pilar tiang kokoh. Sholat adalah tiang paling vital setelah dua kalimat syahadat."
        ),

        HaditsItem(
            id = "hadits_shahih_3",
            number = 3,
            title = "Kewajiban Menuntut Ilmu bagi Setiap Muslim",
            arabic = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            latin = "Thalabul 'ilmi fariidhatun 'alaa kulli muslim.",
            translation = "Menuntut ilmu (syar'i) adalah kewajiban bagi setiap muslim (laki-laki maupun perempuan).",
            narrator = "HR. Ibnu Majah (No. 224), dishahihkan Syaikh Al-Albani",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Diriwayatkan dari sahabat Anas bin Malik r.a. Memiliki banyak jalur sanad yang saling menguatkan (shahih lighairihi) dan disepakati oleh mayoritas ulama hadits muta'akhkhirin.",
            topic = HaditsTopic.ILMU_DZIKIR,
            takhrijBook = "Sunan Ibnu Majah, Al-Muqaddimah & Shahih Al-Jami' (No. 3913)",
            faedah = "Ilmu yang fardhu 'ain wajib dipelajari adalah ilmu tentang tata cara sholat, thaharah, aqidah tauhid dasar, dan kewajiban halal-haram harian yang sedang dihadapi."
        ),

        HaditsItem(
            id = "hadits_shahih_4",
            number = 4,
            title = "Menjaga Lisan dan Memuliakan Tetangga / Tamu",
            arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَاليَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ",
            latin = "Man kaana yu'minu billaahi wal-yawmil aakhiri fal-yaqul khayran aw li-yashmut, wa man kaana yu'minu billaahi wal-yawmil aakhiri fal-yukrim jaarahu, wa man kaana yu'minu billaahi wal-yawmil aakhiri fal-yukrim dhayfahu.",
            translation = "Barangsiapa beriman kepada Allah dan hari akhir, hendaklah ia berkata yang baik atau diam. Barangsiapa beriman kepada Allah dan hari akhir, hendaklah ia memuliakan tetangganya. Dan barangsiapa beriman kepada Allah dan hari akhir, hendaklah ia memuliakan tamunya.",
            narrator = "HR. Bukhari (No. 6018) & Muslim (No. 47)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Muttafaqun 'Alaih dari sahabat Abu Hurairah r.a. Redaksi shahih dengan sanad emas (ashahh al-asanid).",
            topic = HaditsTopic.AKHLAK_ADAB,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Adab & Shahih Muslim, Kitab Al-Iman",
            faedah = "Kesempurnaan iman seseorang tercermin nyata dari pengendalian lidahnya untuk tidak mengghibah, menyakiti sesama, atau menyebarkan hoaks, serta memuliakan hak tetangga."
        ),

        HaditsItem(
            id = "hadits_shahih_5",
            number = 5,
            title = "Nasihat Agung: Jangan Marah!",
            arabic = "أَنَّ رَجُلاً قَالَ لِلنَّبِيِّ ﷺ: أَوْصِنِي. قَالَ: «لاَ تَغْضَبْ». فَرَدَّدَ مِرَارًا، قَالَ: «لاَ تَغْضَبْ»",
            latin = "Anna rajulan qaala lin-Nabiyyi shallallaahu 'alayhi wa sallam: Awshinii. Qaala: 'Laa taghdhab'. Faraddada miraaran, qaala: 'Laa taghdhab'.",
            translation = "Bahwasanya ada seorang lelaki berkata kepada Nabi ﷺ: 'Berilah aku wasiat.' Beliau bersabda: 'Janganlah engkau marah!' Lelaki itu mengulang-ulang permintaannya beberapa kali, dan beliau tetap menjawab: 'Janganlah engkau marah!'",
            narrator = "HR. Bukhari (No. 6116)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Shahih riwayat Imam Bukhari dari sahabat Abu Hurairah r.a. Merupakan hadits ke-16 dalam kitab monumentalnya Imam An-Nawawi, Al-Arba'in An-Nawawiyyah.",
            topic = HaditsTopic.AKHLAK_ADAB,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Adab (Bab Al-Hadzr minal Ghadhab)",
            faedah = "Marah adalah pintu masuk setan yang merusak akal dan tali silaturahmi. Mencegah amarah mendatangkan ketenangan dan keselamatan di dunia maupun akhirat."
        ),

        HaditsItem(
            id = "hadits_shahih_6",
            number = 6,
            title = "Sholat sebagai Pembatas Keimanan",
            arabic = "بَيْنَ الرَّجُلِ وَبَيْنَ الشِّرْكِ وَالْكُفْرِ تَرْكُ الصَّلاَةِ",
            latin = "Baynar-rajuli wa baynasy-syirki wal-kufri tarkush-shalaah.",
            translation = "Batas antara seseorang dengan kemusyrikan dan kekafiran adalah meninggalkan sholat.",
            narrator = "HR. Muslim (No. 82)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Shahih riwayat Imam Muslim dalam Kitab Al-Iman dari sahabat Jabir bin Abdillah r.a. Sanad bersambung tanpa cela.",
            topic = HaditsTopic.SHOLAT_IBADAH,
            takhrijBook = "Shahih Muslim (Kitab Al-Iman)",
            faedah = "Peringatan sangat keras agar seorang muslim tidak pernah meremehkan apalagi meninggalkan sholat fardhu lima waktu dalam kondisi apa pun."
        ),

        HaditsItem(
            id = "hadits_shahih_7",
            number = 7,
            title = "Memuliakan Ibu Tiga Kali Lipat",
            arabic = "جَاءَ رَجُلٌ إِلَى رَسُولِ اللَّهِ ﷺ فَقَالَ: يَا رَسُولَ اللَّهِ، مَنْ أَحَقُّ النَّاسِ بِحُسْنِ صَحَابَتِي؟ قَالَ: «أُمُّكَ». قَالَ: ثُمَّ مَنْ؟ قَالَ: «ثُمَّ أُمُّكَ». قَالَ: ثُمَّ مَنْ؟ قَالَ: «ثُمَّ أُمُّكَ». قَالَ: ثُمَّ مَنْ؟ قَالَ: «ثُمَّ أَبُوكَ»",
            latin = "Jaa-a rajulun ilaa Rasuulillaahi shallallaahu 'alayhi wa sallam fa-qaala: Yaa Rasuulallaah, man ahaqqun-naasi bi husni shahaabatii? Qaala: 'Ummuk'. Qaala: Tsumma man? Qaala: 'Tsumma ummuk'. Qaala: Tsumma man? Qaala: 'Tsumma ummuk'. Qaala: Tsumma man? Qaala: 'Tsumma abuuk'.",
            translation = "Seorang pria datang kepada Rasulullah ﷺ lalu bertanya: 'Wahai Rasulullah, siapakah orang yang paling berhak aku perlakukan dengan baik?' Beliau menjawab: 'Ibumu.' Pria itu bertanya lagi: 'Lalu siapa?' Beliau menjawab: 'Ibumu.' Pria itu bertanya lagi: 'Lalu siapa?' Beliau menjawab: 'Ibumu.' Pria itu bertanya lagi: 'Lalu siapa?' Beliau menjawab: 'Kemudian ayahmu.'",
            narrator = "HR. Bukhari (No. 5971) & Muslim (No. 2548)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Muttafaqun 'Alaih dari sahabat Abu Hurairah r.a. Hadits masyhur dengan derajat keshahihan mutlak.",
            topic = HaditsTopic.KELUARGA_ORANG_TUA,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Adab & Shahih Muslim, Kitab Al-Birr wash-Shilah",
            faedah = "Ibu menanggung 3 kepayahan yang tidak dialami ayah: mengandung dengan susah payah, melahirkan bertaruh nyawa, dan menyusui selama dua tahun penuh."
        ),

        HaditsItem(
            id = "hadits_shahih_8",
            number = 8,
            title = "Meringankan Kesusahan Saudara & Menutup Aib",
            arabic = "مَنْ نَفَّسَ عَنْ مُؤْمِنٍ كُرْبَةً مِنْ كُرَبِ الدُّنْيَا نَفَّسَ اللَّهُ عَنْهُ كُرْبَةً مِنْ كُرَبِ يَوْمِ الْقِيَامَةِ، وَمَنْ يَسَّرَ عَلَى مُعْسِرٍ يَسَّرَ اللَّهُ عَلَيْهِ فِي الدُّنْيَا وَالآخِرَةِ، وَمَنْ سَتَرَ مُسْلِمًا سَتَرَهُ اللَّهُ فِي الدُّنْيَا وَالآخِرَةِ، وَاللَّهُ فِي عَوْنِ الْعَبْدِ مَا كَانَ الْعَبْدُ فِي عَوْنِ أَخِيهِ",
            latin = "Man naffasa 'an mu'minin kurbatam min kurabid-dunyaa naffasallaahu 'anhu kurbatam min kurabi yawmil qiyaamah, wa man yassara 'alaa mu'sirin yassarallaahu 'alayhi fid-dunyaa wal-aakhirah, wa man satara musliman satarahullaahu fid-dunyaa wal-aakhirah, wallaahu fii 'awnil 'abdi maa kaanal 'abdu fii 'awni akhiih.",
            translation = "Barangsiapa membebaskan seorang mukmin dari suatu kesulitan dunia, niscaya Allah akan membebaskannya dari kesulitan pada hari kiamat. Barangsiapa memberi kemudahan kepada orang yang kesulitan, niscaya Allah memudahkan urusannya di dunia dan akhirat. Barangsiapa menutupi aib seorang muslim, niscaya Allah menutupi aibnya di dunia dan akhirat. Dan Allah senantiasa menolong hamba-Nya selama hamba tersebut menolong saudaranya.",
            narrator = "HR. Muslim (No. 2699)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Shahih riwayat Imam Muslim dari Abu Hurairah r.a. Mengandung prinsip solidaritas sosial universal dalam Islam.",
            topic = HaditsTopic.MUAMALAH_SOSIAL,
            takhrijBook = "Shahih Muslim, Kitab Adz-Dzikr wad-Du'aa' wat-Taubah",
            faedah = "Al-Jaza' min jinsil 'amal (balasan sesuai jenis perbuatan). Membantu sesama akan mendatangkan perlindungan dan pertolongan langsung dari Allah Yang Maha Kuasa."
        ),

        HaditsItem(
            id = "hadits_shahih_9",
            number = 9,
            title = "Mencintai Saudara Seiman seperti Diri Sendiri",
            arabic = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            latin = "Laa yu'minu ahadukum hattaa yuhibba li-akhiihi maa yuhibbu linafsih.",
            translation = "Tidaklah sempurna iman salah seorang di antara kalian sampai dia mencintai untuk saudaranya apa yang dia cintai untuk dirinya sendiri.",
            narrator = "HR. Bukhari (No. 13) & Muslim (No. 45)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Muttafaqun 'Alaih dari sahabat Anas bin Malik r.a. Merupakan pondasi persaudaraan (ukhuwah Islamiyyah).",
            topic = HaditsTopic.AKHLAK_ADAB,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Iman & Shahih Muslim, Kitab Al-Iman",
            faedah = "Menghilangkan sifat dengki (hasad), iri hati, dan kesombongan. Menginginkan kebaikan yang sama bagi orang lain sebagaimana diri kita menginginkannya."
        ),

        HaditsItem(
            id = "hadits_shahih_10",
            number = 10,
            title = "Ucapan yang Baik dan Santun adalah Sedekah",
            arabic = "وَالْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ",
            latin = "Wal-kalimatut-thayyibatu shadaqah.",
            translation = "Dan perkataan yang baik (sopan, ramah, dan mendamaikan) adalah sedekah.",
            narrator = "HR. Bukhari (No. 2989) & Muslim (No. 1009)",
            grade = HaditsGrade.SHAHIH,
            gradeExplanation = "Potongan hadits shahih Muttafaqun 'Alaih dari sahabat Abu Hurairah r.a. tentang berbagai pintu sedekah non-materi.",
            topic = HaditsTopic.MUAMALAH_SOSIAL,
            takhrijBook = "Shahih Al-Bukhari, Kitab Al-Jihad & Shahih Muslim, Kitab Az-Zakat",
            faedah = "Sedekah tidak terbatas pada harta uang. Menyapa dengan hangat, mendamaikan orang berselisih, mengucap salam, dan menegur sapa dengan baik bernilai pahala sedekah di sisi Allah."
        ),

        // ------------------ 2. HADITS HASAN ------------------
        HaditsItem(
            id = "hadits_hasan_11",
            number = 11,
            title = "Tidak Boleh Merugikan dan Merugikan Balik",
            arabic = "لاَ ضَرَرَ وَلاَ ضِرَارَ",
            latin = "Laa dharara wa laa dhiraar.",
            translation = "Tidak boleh berbuat sesuatu yang membahayakan/merugikan (diri sendiri atau orang lain), dan tidak boleh pula membalas kemudharatan dengan kemudharatan yang serupa.",
            narrator = "HR. Ibnu Majah (No. 2340) & Ad-Daraquthni (No. 4540)",
            grade = HaditsGrade.HASAN,
            gradeExplanation = "Hadits hasan. Dikuatkan oleh jalur riwayat Ibnu Majah, Ad-Daraquthni, dan Baihaqi dari Ibnu Abbas dan Ubadah bin Shamit. Imam An-Nawawi memasukkannya sebagai kaidah fiqih ushul ke-32 dalam Arbain.",
            topic = HaditsTopic.MUAMALAH_SOSIAL,
            takhrijBook = "Sunan Ibnu Majah, Sunan Ad-Daraquthni, dinilai Hasan oleh An-Nawawi & Al-Albani",
            faedah = "Menjadi salah satu dari 5 kaidah fiqih induk dalam hukum Islam (Al-Qawa'id Al-Fiqhiyyah Al-Khams). Melarang segala transaksi riba, polusi yang mengganggu tetangga, dan kejahatan terencana."
        ),

        HaditsItem(
            id = "hadits_hasan_12",
            number = 12,
            title = "Senyum Tulus kepada Saudara adalah Sedekah",
            arabic = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            latin = "Tabassumuka fii wajhi akhiika laka shadaqah.",
            translation = "Senyumanmu di hadapan wajah saudaramu adalah bernilai sedekah bagimu.",
            narrator = "HR. At-Tirmidzi (No. 1956)",
            grade = HaditsGrade.HASAN,
            gradeExplanation = "Diriwayatkan dari sahabat Abu Dzar r.a. Dinilai hasan gharib oleh Imam At-Tirmidzi dan dishahihkan oleh Ibnu Hibban dan Syaikh Al-Albani.",
            topic = HaditsTopic.AKHLAK_ADAB,
            takhrijBook = "Jami' At-Tirmidzi, Kitab Al-Birr wash-Shilah & Shahih Ibni Hibban",
            faedah = "Menebarkan keceriaan dan keramahan membuka hati manusia. Wajah yang berseri-seri merupakan akhlak kenabian yang sangat dicintai."
        ),

        HaditsItem(
            id = "hadits_hasan_13",
            number = 13,
            title = "Tinggalkan yang Meragukan Menuju yang Meyakinkan",
            arabic = "دَعْ مَا يَرِيبُكَ إِلَى مَا لاَ يَرِيبُكَ",
            latin = "Da' maa yariibuka ilaa maa laa yariibuk.",
            translation = "Tinggalkanlah apa yang meragukanmu menuju apa yang tidak meragukanmu.",
            narrator = "HR. At-Tirmidzi (No. 2518) & An-Nasa'i (No. 5711)",
            grade = HaditsGrade.HASAN,
            gradeExplanation = "Diriwayatkan dari cucu Nabi, Al-Hasan bin Ali r.a. Imam At-Tirmidzi berkata: 'Hadits ini hasan shahih.'",
            topic = HaditsTopic.NIAT_AKIDAH,
            takhrijBook = "Jami' At-Tirmidzi, Sifatul Qiyamah & Sunan An-Nasa'i",
            faedah = "Kaidah wara' (kehati-hatian). Kejujuran mendatangkan ketenangan jiwa (thuma'ninah), sedangkan syubhat dan kedustaan mendatangkan keraguan dan kegelisahan batin."
        ),

        HaditsItem(
            id = "hadits_hasan_14",
            number = 14,
            title = "Menyayangi yang Muda dan Menghormati yang Tua",
            arabic = "لَيْسَ مِنَّا مَنْ لَمْ يَرْحَمْ صَغِيرَنَا وَيَعْرِفْ شَرَفَ كَبِيرِنَا",
            latin = "Laysa minnaa man lam yarham shaghiiranaa wa ya'rif syarafa kabiirinaa.",
            translation = "Bukan termasuk golongan kami orang yang tidak menyayangi yang lebih muda di antara kami dan tidak menghormati hak/keutamaan yang lebih tua di antara kami.",
            narrator = "HR. At-Tirmidzi (No. 1920) & Abu Dawud (No. 4943)",
            grade = HaditsGrade.HASAN,
            gradeExplanation = "Diriwayatkan dari sahabat Abdullah bin Amr r.a. Sanadnya hasan shahih menurut Imam At-Tirmidzi dan Syaikh Al-Albani dalam Shahih At-Tirmidzi.",
            topic = HaditsTopic.AKHLAK_ADAB,
            takhrijBook = "Jami' At-Tirmidzi, Kitab Al-Birr wash-Shilah & Sunan Abi Dawud",
            faedah = "Adab bermasyarakat: kaum tua membimbing kaum muda dengan penuh kasih sayang, sedangkan kaum muda menghormati para sesepuh dan orang tua dengan sopan santun."
        ),

        // ------------------ 3. HADITS DHA'IF (LEMAH) & EDUKASI ------------------
        HaditsItem(
            id = "hadits_dhaif_15",
            number = 15,
            title = "Tuntutlah Ilmu Walau ke Negeri Cina",
            arabic = "اُطْلُبُوا الْعِلْمَ وَلَوْ بِالصِّينِ",
            latin = "Uthlubul 'ilma walaw bish-Shiin.",
            translation = "Tuntutlah ilmu meskipun sampai ke negeri Cina.",
            narrator = "Riwayat Al-Uqaili dalam Adh-Dhu'afa' & Al-Baihaqi dalam Syu'abul Iman",
            grade = HaditsGrade.DHAIF,
            gradeExplanation = "Derajat: Dha'if Jiddan (Sangat Lemah) bahkan dinilai batil/palsu oleh para imam hadits seperti Ibnu Hibban, Ibnul Jauzi, dan Al-Albani. Perawinya bernama Abu 'Atikah Tharif bin Sulaiman berstatus matruk (ditinggalkan haditsnya).",
            topic = HaditsTopic.ILMU_DZIKIR,
            takhrijBook = "Al-Uqaili (Adh-Dhu'afa'), Al-Baihaqi (Syu'abul Iman), Silsilah Al-Ahadits Adh-Dha'ifah (No. 416)",
            faedah = "Kewajiban menuntut ilmu dalam Islam adalah mutlak, namun redaksi 'sampai ke negeri Cina' tidak shahih dari Nabi ﷺ.",
            clarification = "📌 Rujukan Shahih Pengganti: Gunakan hadits shahih riwayat Ibnu Majah (No. 224): 'Thalabul 'ilmi fariidhatun 'ala kulli muslim' (Menuntut ilmu adalah kewajiban bagi setiap muslim). Maknanya jauh lebih kuat dan sanadnya terpercaya."
        ),

        HaditsItem(
            id = "hadits_dhaif_16",
            number = 16,
            title = "Kebersihan Sebagian dari Iman (Lafadz Populer)",
            arabic = "النَّظَافَةُ مِنَ الإِيمَانِ",
            latin = "An-nadhaafatu minal iimaan.",
            translation = "Kebersihan itu sebagian dari iman.",
            narrator = "Riwayat Ath-Thabrani dalam Al-Ausath (No. 7311)",
            grade = HaditsGrade.DHAIF,
            gradeExplanation = "Derajat: Dha'if (Lemah). Sanadnya sangat lemah karena di dalamnya terdapat perawi bernama Abdul A'la bin Hilal dan Ahmad bin Khalad yang majhul dan dlaif. As-Sakhawi menyatakan sanadnya dha'if jiddan.",
            topic = HaditsTopic.SHOLAT_IBADAH,
            takhrijBook = "Al-Mu'jam Al-Ausath karya Ath-Thabrani, Al-Maqashid Al-Hasanah (No. 1260)",
            faedah = "Islam sangat menekankan kebersihan dan kesucian lahir batin, namun lafadz 'an-nadhafatu' ini tidak shahih sanadnya.",
            clarification = "📌 Rujukan Shahih Pengganti: Gunakan hadits shahih dalam Shahih Muslim (No. 223): 'At-thuhuuru syathrul iimaan' (Bersuci adalah separuh dari keimanan). Kata 'thaharah' (bersuci) jauh lebih luas dari sekadar 'nadhafah' (bersih), karena mencakup suci dari najis, hadats, dan kotoran batin."
        ),

        HaditsItem(
            id = "hadits_dhaif_17",
            number = 17,
            title = "Tidurnya Orang Berpuasa adalah Ibadah",
            arabic = "نَوْمُ الصَّائِمِ عِبَادَةٌ، وَصُمْتُهُ تَسْبِيحٌ، وَعَمَلُهُ مُضَاعَفٌ، وَدُعَاؤُهُ مُسْتَجَابٌ",
            latin = "Nawmush-shaa-imi 'ibaadah, wa shumtuhu tasbiih, wa 'amaluhu mudhaa'af, wa du'aa-uhu mustajaab.",
            translation = "Tidurnya orang yang berpuasa adalah ibadah, diamnya adalah tasbih, amalnya dilipatgandakan, dan doanya dikabulkan.",
            narrator = "Riwayat Al-Baihaqi dalam Syu'abul Iman (No. 3654)",
            grade = HaditsGrade.DHAIF,
            gradeExplanation = "Derajat: Dha'if (Lemah). Di dalam sanadnya terdapat perawi bernama Sulaiman bin Amr An-Nakha'i yang dinyatakan oleh para ulama hadits sebagai perawi sangat lemah, bahkan dituduh memalsukan riwayat.",
            topic = HaditsTopic.SHOLAT_IBADAH,
            takhrijBook = "Syu'abul Iman karya Al-Baihaqi, dinilai dha'if oleh Al-Iraqi dan Al-Albani",
            faedah = "Hadits ini sering dijadikan alasan bagi orang puasa untuk bermalas-malasan dan tidur sepanjang hari saat Ramadhan, padahal itu keliru.",
            clarification = "📌 Edukasi: Tidur orang berpuasa baru bernilai pahala jika diniatkan untuk mengistirahatkan badan agar kuat beribadah sholat tarawih, qiyamul lail, dan tadarus Al-Qur'an, bukan untuk menghabiskan waktu dengan tidur melalaikan sholat fardhu."
        ),

        HaditsItem(
            id = "hadits_dhaif_18",
            number = 18,
            title = "Surga di Bawah Telapak Kaki Ibu (Lafadz Populer)",
            arabic = "الْجَنَّةُ تَحْتَ أَقْدَامِ الأُمَّهَاتِ",
            latin = "Al-jannatu tahta aqdaamil ummahaat.",
            translation = "Surga itu berada di bawah telapak kaki ibu.",
            narrator = "Riwayat Al-Khatib Al-Baghdadi & Al-Qudha'i dalam Musnad Asy-Syihab",
            grade = HaditsGrade.DHAIF,
            gradeExplanation = "Derajat: Dha'if secara lafadz ini. Ibn Thahir Al-Maqdisi dan Syaikh Al-Albani (Dha'if Al-Jami' No. 2666) menjelaskan lafadz riwayat 'tahta aqdam' ini sanadnya munqathi' (terputus) dan perawinya dlaif.",
            topic = HaditsTopic.KELUARGA_ORANG_TUA,
            takhrijBook = "Musnad Asy-Syihab, Dha'if Al-Jami' As-Shaghir (No. 2666)",
            faedah = "Makna berbakti kepada ibu adalah wajib mutlak, namun para muhadditsin membedakan antara derajat teks lafadz dan makna kandungannya.",
            clarification = "📌 Rujukan Shahih Pengganti: Terdapat riwayat shahih dengan makna serupa dalam Sunan An-Nasa'i (No. 3104) dan Ahmad ketika Mu'awiyah bin Jahimah meminta izin berjihad, Nabi ﷺ bersabda: 'Fa-alzimha, fainnal jannata tahta rijlayha' (Tetaplah berbakti dan melayaninya, karena sesungguhnya surga ada di bawah kedua kakinya). Ini riwayat yang shahih!"
        ),

        // ------------------ 4. HADITS POPULER & KLARIFIKASI MAUDHU'/ATSAR ------------------
        HaditsItem(
            id = "hadits_populer_19",
            number = 19,
            title = "Cinta Tanah Air Sebagian dari Iman",
            arabic = "حُبُّ الْوَطَنِ مِنَ الإِيمَانِ",
            latin = "Hubbul wathani minal iimaan.",
            translation = "Mencintai tanah air adalah sebagian dari iman.",
            narrator = "Masyhur di lisan masyarakat (Bukan Hadits Nabi ﷺ)",
            grade = HaditsGrade.POPULER_KLARIFIKASI,
            gradeExplanation = "Status: Maudhu' (Palsu / Tidak ada asal sanadnya dari Rasulullah ﷺ). Dinyatakan oleh Imam Ash-Saghani dalam Al-Maudhu'at, As-Sakhawi dalam Al-Maqashid Al-Hasanah, dan Al-Albani dalam Silsilah Adh-Dha'ifah (No. 36).",
            topic = HaditsTopic.KLARIFIKASI_POPULER,
            takhrijBook = "Al-Maqashid Al-Hasanah (No. 386), Silsilah Al-Ahadits Adh-Dha'ifah wal Maudhu'ah (No. 36)",
            faedah = "Mencintai negeri tempat kelahiran adalah fitrah naluriah manusiawi yang wajar dan baik. Namun kita tidak boleh menisbatkan kalimat ini sebagai sabda Nabi Muhammad ﷺ karena beliau tidak pernah mengucapkannya.",
            clarification = "📌 Kaidah Ilmiah: Berbohong atas nama Rasulullah ﷺ memiliki ancaman sangat berat: 'Man kadzaba 'alayya muta'ammidan falyatabawwa' maq'adahu minan naar' (Barangsiapa berdusta atas namaku dengan sengaja, hendaklah menyiapkan tempat duduknya di neraka - HR. Bukhari)."
        ),

        HaditsItem(
            id = "hadits_populer_20",
            number = 20,
            title = "Bekerjalah untuk Duniamu Seakan Hidup Selamanya",
            arabic = "اِعْمَلْ لِدُنْيَاكَ كَأَنَّكَ تَعِيشُ أَبَدًا، وَاعْمَلْ لِآخِرَتِكَ كَأَنَّكَ تَمُوتُ غَدًا",
            latin = "I'mal lidunyaaka ka-annaka ta'iisyu abadaa, wa'mal li-aakhiratika ka-annaka tamuutu ghadaa.",
            translation = "Bekerjalah untuk duniamu seakan-akan kamu akan hidup selamanya, dan beramallah untuk akhiratmu seakan-akan kamu akan mati esok hari.",
            narrator = "Atsar Sahabat Abdullah bin Amr r.a. (Bukan Hadits Marfu' Nabi ﷺ)",
            grade = HaditsGrade.POPULER_KLARIFIKASI,
            gradeExplanation = "Status: Bukan Hadits Marfu' dari Nabi ﷺ, melainkan Mauquf / Atsar ucapan sahabat Abdullah bin Amr bin Al-Ash atau Ali bin Abi Thalib r.a. Diriwayatkan oleh Al-Baihaqi dalam As-Sunan Al-Kubra (No. 6516) secara mauquf.",
            topic = HaditsTopic.KLARIFIKASI_POPULER,
            takhrijBook = "As-Sunan Al-Kubra lil Baihaqi (No. 6516), Silsilah Al-Ahadits Adh-Dha'ifah (No. 8)",
            faedah = "Kalimat ini adalah nasihat bijak para sahabat agar bersikap seimbang: jangan terburu-buru rakus mengejar dunia (karena dunia bisa dikerjakan bertahap), tetapi segerakanlah amal akhirat karena ajal bisa datang esok hari.",
            clarification = "📌 Klarifikasi: Boleh dinukil sebagai kata-kata mutiara atau hikmah sahabat, namun jangan disebut sebagai 'Sabda Rasulullah ﷺ'."
        )
    )
}
