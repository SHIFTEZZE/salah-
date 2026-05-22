package com.example.data.database

data class Hadith(
    val id: Int,
    val reference: String,
    val narrator: String,
    val textArabic: String,
    val textEnglish: String,
    val category: String,
    val description: String
)

object HadithData {
    val hadiths = listOf(
        Hadith(
            id = 1,
            reference = "Sahih al-Bukhari 1",
            narrator = "Umar bin Al-Khattab",
            textArabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            textEnglish = "The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended.",
            category = "Faith",
            description = "Actions are judged by intentions. Setting a pure, clean intention is the foundation of all acts of worship in Islam."
        ),
        Hadith(
            id = 2,
            reference = "Sahih al-Bukhari 528",
            narrator = "Abu Hurayrah",
            textArabic = "أَرَأَيْتُمْ لَوْ أَنَّ نَهْرًا بِبَابِ أَحَدِكُمْ يَغْتَسِلُ مِنْهُ كُلَّ يَوْمٍ خَمْسَ مَرَّاتٍ، هَلْ يَبْقَى مِنْ دَرَنِهِ شَيْءٌ؟ قَالُوا لاَ يَبْقَى مِنْ دَرَنِهِ شَيْءٌ‏.‏ قَالَ فَذَلِكَ مَثَلُ الصَّلَوَاتِ الْخَمْسِ يَمْحُو اللَّهُ بِهِنَّ الْخَطَايَا",
            textEnglish = "If there was a river at the door of anyone of you and he took a bath in it five times a day would any dirt remain on him? They replied, 'No dirt would remain.' The Prophet said, 'That is the example of the five compulsory prayers with which Allah blots out evil deeds.'",
            category = "Salah (Prayer)",
            description = "The beautiful purification effect of staying consistent on the five daily compulsory prayers."
        ),
        Hadith(
            id = 3,
            reference = "Sahih al-Bukhari 13",
            narrator = "Anas bin Malik",
            textArabic = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            textEnglish = "None of you will have faith until he wishes for his brother (or companion) what he likes for himself.",
            category = "Brotherhood",
            description = "The beautiful value of mutual love, companionate growth, and wishing noble achievements for your peer."
        ),
        Hadith(
            id = 4,
            reference = "Sahih al-Bukhari 5027",
            narrator = "Uthman bin Affan",
            textArabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            textEnglish = "The best among you are those who learn the Quran and teach it to others.",
            category = "Quran",
            description = "The highest level of spiritual distinction is linked to spending consistent daily time learning and reciting the Book of Allah."
        ),
        Hadith(
            id = 5,
            reference = "Sahih al-Bukhari 6032",
            narrator = "Aisha, Mother of the Believers",
            textArabic = "مَا زَالَ جِبْرِيلُ يُوصِينِي بِالْجَارِ حَتَّى ظَنَنْتُ أَنَّهُ سَيُوَرِّثُهُ",
            textEnglish = "Gabriel kept on recommending me about treating neighbors and companions so kindly and politely that I thought he would bestow upon them a share of inheritance.",
            category = "Good Manners",
            description = "The fundamental Islamic priority given to kind, polite relationships with people in our immediate circles."
        ),
        Hadith(
            id = 6,
            reference = "Sahih al-Bukhari 6464",
            narrator = "Ibn Umar",
            textArabic = "كُنْ فِي الدُّنْيَا كَأَنَّكَ غَرِيبٌ، أَوْ عَابِرُ سَبِيلٍ",
            textEnglish = "Be in this world as if you were a stranger or a traveler.",
            category = "Faith",
            description = "A peaceful call to avoid getting lost in excessive worldly distractions, focusing instead on personal spiritual preparation."
        ),
        Hadith(
            id = 7,
            reference = "Sahih al-Bukhari 6011",
            narrator = "Abu Hurayrah",
            textArabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            textEnglish = "Whoever believes in Allah and the Last Day should talk what is good or keep quiet.",
            category = "Good Manners",
            description = "Guarding one's speech is a key Islamic principle for visual peace and mutual safety from conflicts."
        ),
        Hadith(
            id = 8,
            reference = "Sahih al-Bukhari 8",
            narrator = "Abdullah bin Umar",
            textArabic = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالْحَجِّ، وَصَوْمِ رَمَضَانَ",
            textEnglish = "Islam is based on five pillars: to testify that none has the right to be worshipped but Allah and Muhammad is His Apostle; to offer the compulsory prayers dutifully; to pay Zakat; to perform Hajj; and to observe fast during Ramadan.",
            category = "Salah (Prayer)",
            description = "The foundational pillars of Islam, emphasizing pray (Salah) as the paramount daily connection."
        ),
        Hadith(
            id = 9,
            reference = "Sahih al-Bukhari 6019",
            narrator = "Nu'man bin Bashir",
            textArabic = "مَثَلُ الْمُؤْمِنِينَ فِي تَوَادِّهِمْ وَتَرَاحُمِهِمْ وَتَعَاطُفِهِمْ مَثَلُ الْجَسَدِ إِذَا اشْتَكَى مِنْهُ عُضْوٌ تَدَاعَى لَهُ سَائِرُ الْجَسَدِ بِالسَّهَرِ وَالْحُمَّى",
            textEnglish = "The believers in their mutual kindness, compassion and sympathy are just like one body. When any of the limbs suffers, the whole body responds to it with sleeplessness and fever.",
            category = "Brotherhood",
            description = "A beautiful metaphor of mutual deen accountability. Companion support forms the organic backbone of personal spiritual consistency."
        ),
        Hadith(
            id = 10,
            reference = "Sahih al-Bukhari 6465",
            narrator = "Abu Hurayrah",
            textArabic = "نِعْمَتَانِ مَغْبُونٌ فِيهِمَا كَثِيرٌ مِنَ النَّاسِ: الصِّحَّةُ وَالْفَرَاغُ",
            textEnglish = "Two blessings are many people deprived of: Health and free time.",
            category = "Faith",
            description = "A strong reminder to value your hours and health productively before you get busy or lose them."
        )
    )
}
