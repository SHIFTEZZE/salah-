package com.example.data.database

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val ayahs: List<Ayah>
)

data class Ayah(
    val number: Int,
    val textArabic: String,
    val textEnglish: String
)

object QuranData {
    val surahs = listOf(
        Surah(
            number = 1,
            nameArabic = "الفاتحة",
            nameEnglish = "Al-Fatiha",
            nameTranslation = "The Opening",
            ayahs = listOf(
                Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
                Ayah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -"),
                Ayah(3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,"),
                Ayah(4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense."),
                Ayah(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help."),
                Ayah(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -"),
                Ayah(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have earned [Your] anger or of those who are astray.")
            )
        ),
        Surah(
            number = 2,
            nameArabic = "آية الكرسي",
            nameEnglish = "Ayat Al-Kursi",
            nameTranslation = "The Throne Verse",
            ayahs = listOf(
                Ayah(255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.")
            )
        ),
        Surah(
            number = 67,
            nameArabic = "الملك",
            nameEnglish = "Al-Mulk",
            nameTranslation = "The Sovereignty (Selections)",
            ayahs = listOf(
                Ayah(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
                Ayah(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -"),
                Ayah(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "Who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency. So return your vision [to the sky]; do you see any breaks?"),
                Ayah(4, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Then return your vision twice again. [Your] vision will return to you humbled while it is fatigued.")
            )
        ),
        Surah(
            number = 108,
            nameArabic = "الكوثر",
            nameEnglish = "Al-Kauthar",
            nameTranslation = "The Abundance",
            ayahs = listOf(
                Ayah(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kauthar."),
                Ayah(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone]."),
                Ayah(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.")
            )
        ),
        Surah(
            number = 112,
            nameArabic = "الإخلاص",
            nameEnglish = "Al-Ikhlas",
            nameTranslation = "The Sincerity",
            ayahs = listOf(
                Ayah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, \"He is Allah, [who is] One,"),
                Ayah(2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge."),
                Ayah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,"),
                Ayah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.\"")
            )
        ),
        Surah(
            number = 113,
            nameArabic = "الفلق",
            nameEnglish = "Al-Falaq",
            nameTranslation = "The Daybreak",
            ayahs = listOf(
                Ayah(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak"),
                Ayah(2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created"),
                Ayah(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles"),
                Ayah(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots"),
                Ayah(5, "مِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"")
            )
        ),
        Surah(
            number = 114,
            nameArabic = "الناس",
            nameEnglish = "An-Nas",
            nameTranslation = "Mankind",
            ayahs = listOf(
                Ayah(1, "قُل_ْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,"),
                Ayah(2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,"),
                Ayah(3, "إِلَٰهِ النَّاسِ", "The God of mankind,"),
                Ayah(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -"),
                Ayah(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -"),
                Ayah(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"")
            )
        )
    )
}
