package com.wakemate.ai.data.repository

import java.util.Calendar

data class DailyAyat(
    val arabic: String,
    val urdu: String,
    val english: String,
    val reference: String
)

data class DailyHadees(
    val arabic: String,
    val urdu: String,
    val english: String,
    val reference: String
)

data class DailyMotivation(
    val title: String,
    val urdu: String,
    val english: String
)

object InspirationRepository {

    val AYATS: List<DailyAyat> = listOf(
        DailyAyat(
            arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            urdu = "پس یقیناً مشکل کے ساتھ آسانی ہے، بے شک مشکل کے ساتھ آسانی ہے۔",
            english = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
            reference = "Surah Al-Inshirah (94:5-6)"
        ),
        DailyAyat(
            arabic = "وَتَوَكَّلْ عَلَى اللَّهِ ۚ وَكَفَىٰ بِاللَّهِ وَكِيلًا",
            urdu = "اور اللہ پر بھروسہ رکھو، اور اللہ ہی کارساز کے طور پر کافی ہے۔",
            english = "And rely upon Allah; and sufficient is Allah as Disposer of affairs.",
            reference = "Surah Al-Ahzab (33:3)"
        ),
        DailyAyat(
            arabic = "لَا تَحْزَنْ إِنَّ اللَّهَ مَعَنَا",
            urdu = "غم نہ کرو، بے شک اللہ ہمارے ساتھ ہے۔",
            english = "Do not grieve; indeed Allah is with us.",
            reference = "Surah At-Tawbah (9:40)"
        ),
        DailyAyat(
            arabic = "وَأَن لَّيْسَ لِلْإِنسَانِ إِلَّا مَا سَعَىٰ",
            urdu = "اور یہ کہ انسان کے لیے وہی کچھ ہے جس کی اس نے کوشش کی۔",
            english = "And that there is not for man except that [good] for which he strives.",
            reference = "Surah An-Najm (53:39)"
        ),
        DailyAyat(
            arabic = "إِنَّ اللَّهَ لَا يُضِيعُ أَجْرَ الْمُحْسِنِينَ",
            urdu = "بے شک اللہ نیکوکاروں کا اجر ضائع نہیں کرتا۔",
            english = "Indeed, Allah does not allow to be lost the reward of the doers of good.",
            reference = "Surah At-Tawbah (9:120)"
        ),
        DailyAyat(
            arabic = "وَاصْبِرْ لِحُكْمِ رَبِّكَ فَإِنَّكَ بِأَعْيُنِنَا",
            urdu = "اور اپنے رب کے حکم پر صبر کرو، بے شک تم ہماری آنکھوں کے سامنے ہو۔",
            english = "And be patient for the decision of your Lord, for indeed, you are in Our eyes.",
            reference = "Surah At-Tur (52:48)"
        ),
        DailyAyat(
            arabic = "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
            urdu = "اللہ کسی جان پر اس کی طاقت سے زیادہ بوجھ نہیں ڈالتا۔",
            english = "Allah does not burden a soul beyond that it can bear.",
            reference = "Surah Al-Baqarah (2:286)"
        ),
        DailyAyat(
            arabic = "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ",
            urdu = "اور جو اللہ سے ڈرے گا، اللہ اس کے لیے نکلنے کا راستہ بنا دے گا اور اسے وہاں سے رزق دے گا جہاں اس کا گمان بھی نہ ہو۔",
            english = "And whoever fears Allah - He will make for him a way out and will provide for him from where he does not expect.",
            reference = "Surah At-Talaq (65:2-3)"
        ),
        DailyAyat(
            arabic = "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ",
            urdu = "اور عنقریب تمہارا رب تمہیں اتنا دے گا کہ تم خوش ہو جاؤ گے۔",
            english = "And your Lord is going to give you, and you will be satisfied.",
            reference = "Surah Ad-Duha (93:5)"
        ),
        DailyAyat(
            arabic = "إِنَّ مَعِيَ رَبِّي سَيَهْدِينِ",
            urdu = "بے شک میرا رب میرے ساتھ ہے، وہ مجھے ضرور راستہ دکھائے گا۔",
            english = "Indeed, with me is my Lord; He will guide me.",
            reference = "Surah Ash-Shu'ara (26:62)"
        ),
        DailyAyat(
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            urdu = "خبردار! اللہ کے ذکر ہی سے دلوں کو اطمینان حاصل ہوتا ہے۔",
            english = "Unquestionably, by the remembrance of Allah hearts are assured.",
            reference = "Surah Ar-Ra'd (13:28)"
        ),
        DailyAyat(
            arabic = "وَقُل رَّبِّ زِدْنِي عِلْمًا",
            urdu = "اور دعا کرو: اے میرے رب! میرے علم میں اضافہ فرما۔",
            english = "And say, 'My Lord, increase me in knowledge.'",
            reference = "Surah Taha (20:114)"
        )
    )

    val HADEES: List<DailyHadees> = listOf(
        DailyHadees(
            arabic = "اللَّهُمَّ بَارِكْ لِأُمَّتِي فِي بُكُورِهَا",
            urdu = "اے اللہ! میری امت کے لیے صبح کے وقت (early morning) میں برکت عطا فرما۔",
            english = "O Allah, bless my nation in their early mornings.",
            reference = "Sunan Abi Dawud: 2606"
        ),
        DailyHadees(
            arabic = "الْمُؤْمِنُ الْقَوِيُّ خَيْرٌ وَأَحَبُّ إِلَى اللَّهِ مِنَ الْمُؤْمِنِ الضَّعِيفِ",
            urdu = "طاقتور مومن کمزور مومن سے بہتر اور اللہ کو زیادہ محبوب ہے، اور دونوں میں بھلائی ہے۔",
            english = "The strong believer is better and more beloved to Allah than the weak believer.",
            reference = "Sahih Muslim: 2664"
        ),
        DailyHadees(
            arabic = "احْرِصْ عَلَى مَا يَنْفَعُكَ، وَاسْتَعِنْ بِاللَّهِ وَلَا تَعْجَزْ",
            urdu = "جو چیز تمہیں فائدہ دے اس کی خواہش اور کوشش کرو، اللہ سے مدد مانگو اور ہرگز ہمت نہ ہارو۔",
            english = "Strive for that which will benefit you, seek help from Allah, and do not be discouraged.",
            reference = "Sahih Muslim: 2664"
        ),
        DailyHadees(
            arabic = "مَا أَكَلَ أَحَدٌ طَعَامًا قَطُّ خَيْرًا مِنْ أَنْ يَأْكُلَ مِنْ عَمَلِ يَدِهِ",
            urdu = "کسی انسان نے کبھی اس کھانے سے بہتر نہیں کھایا جو اس نے اپنے ہاتھوں کی محنت سے کھایا ہو۔",
            english = "No one has ever eaten better food than what is earned by the work of their own hands.",
            reference = "Sahih Bukhari: 2072"
        ),
        DailyHadees(
            arabic = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            urdu = "علم و ہنر حاصل کرنا ہر مسلمان پر فرض ہے۔",
            english = "Seeking knowledge is an obligation upon every Muslim.",
            reference = "Sunan Ibn Majah: 224"
        ),
        DailyHadees(
            arabic = "إِنَّ اللَّهَ يُحِبُّ إِذَا عَمِلَ أَحَدُكُمْ عَمَلًا أَنْ يُتْقِنَهُ",
            urdu = "بے شک اللہ پسند کرتا ہے کہ جب تم میں سے کوئی کام کرے تو اسے مکمل مہارت اور اتقان سے کرے۔",
            english = "Indeed, Allah loves that when one of you does a deed, he does it with perfection.",
            reference = "Shu'ab al-Iman: 4929"
        ),
        DailyHadees(
            arabic = "عَجَبًا لِأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ",
            urdu = "مومن کا معاملہ بھی خوب ہے، اس کے ہر کام میں خیر ہے: آسانی پر شکر کرتا ہے، اور مشکل پر صبر۔",
            english = "Strange are the ways of a believer for there is good in every affair of his.",
            reference = "Sahih Muslim: 2999"
        ),
        DailyHadees(
            arabic = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ طَرِيقًا إِلَى الْجَنَّةِ",
            urdu = "جو شخص علم کی تلاش میں راستے پر چلتا ہے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔",
            english = "Whoever treads a path in search of knowledge, Allah will make easy for him the path to Paradise.",
            reference = "Sahih Muslim: 2699"
        ),
        DailyHadees(
            arabic = "الْيَدُ الْعُلْيَا خَيْرٌ مِنَ الْيَدِ السُّفْلَى",
            urdu = "اوپر والا ہاتھ (دینے اور محنت کرنے والا) نیچے والے ہاتھ سے بہتر ہے۔",
            english = "The upper hand (giving and striving) is better than the lower hand.",
            reference = "Sahih Bukhari: 1429"
        ),
        DailyHadees(
            arabic = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا",
            urdu = "جہاں کہیں بھی رہو اللہ سے ڈرو، اور برائی کے بعد نیکی کرو جو اسے مٹا دے۔",
            english = "Fear Allah wherever you may be, and follow up an evil deed with a good one which will wipe it out.",
            reference = "Jami` at-Tirmidhi: 1987"
        )
    )

    val MOTIVATION: List<DailyMotivation> = listOf(
        DailyMotivation(
            title = "Unstoppable Comeback",
            urdu = "Agar kisi ne tumhara haath chhor diya hai to shukr ada karo—unho ne tumhe sabat kar diya ke tum akele bhi toofan ho! Aansuon ko apni aag banao aur itni mehnat karo ke dunya dekhe.",
            english = "They left you at your lowest because they couldn't see your destiny. Now grind in complete silence until your success becomes undeniable."
        ),
        DailyMotivation(
            title = "I CAN DO IT",
            urdu = "Haar maan-na mere khoon mein nahi! Jab tak saans hai, tab tak ladai jaari hai. Jo log kehte the tum nahi kar sakte, unko zuban se nahi apne results se jawab do.",
            english = "Giving up is not an option. Turn self-doubt into relentless fuel. Work so hard that your results silence every doubter."
        ),
        DailyMotivation(
            title = "Silent Grind, Loud Success",
            urdu = "Khamoshi se itni sakht mehnat karo ke tumhari kamyabi unke dilon par tamacha ban kar bajay jinhon ne tumhe nakara samjha tha.",
            english = "Work in silence, let your success make the noise. Do not explain, do not react—just outwork everyone in the room."
        ),
        DailyMotivation(
            title = "Rise Above Heartbreak",
            urdu = "Log aate hain, jaate hain. Lekin tumhara maqsad, tumhari izzat aur tumhara mustaqbil hamesha tumhare sath rahega. Utho, apna qad khud uncha karo!",
            english = "People will walk in and out of your life. But your purpose, your self-respect, and your future belong only to you. Rise up and conquer."
        ),
        DailyMotivation(
            title = "Diamond Pressure",
            urdu = "Toota hua heera jab aag aur dabao se guzarta hai to uski qeemat arbon mein hoti hai. Yeh mushkil waqt tumhe todne nahi, tarashne aaya hai!",
            english = "Diamonds are made under crushing pressure. This difficult chapter isn't here to break you—it's here to build you into an unbreakable force."
        ),
        DailyMotivation(
            title = "No Safety Net",
            urdu = "Akele chalne se mat daro! Sher hamesha akele shikar karta hai. Apne dam par khare ho jao, kisi ke sahare ki zaroorat nahi.",
            english = "Do not fear walking alone. The lion hunts alone, not in herds. Stand on your own two feet and claim your territory."
        ),
        DailyMotivation(
            title = "Wake Up & Dominate",
            urdu = "Subah ka waqt kamzoron ke sone ka hai aur sheron ke tayari karne ka. Utho, apna naya din shuru karo aur har lamhe ko qeemti banao!",
            english = "Early mornings are when champions are built. Wake up with purpose, execute with precision, and leave zero excuses."
        ),
        DailyMotivation(
            title = "They Will Regret",
            urdu = "Unko ehsas hoga ke unho ne heera kho diya pathar ke pichhe. Lekin tab tak tum itni unchai par ho ge jahan unki aawaz bhi nahi pohanch sakegi.",
            english = "They will realize what they lost, but by then you will be so far ahead that looking back won't even cross your mind."
        )
    )

    fun getTodayAyat(offset: Int = 0): DailyAyat {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + offset
        val index = Math.abs(dayOfYear % AYATS.size)
        return AYATS[index]
    }

    fun getTodayHadees(offset: Int = 0): DailyHadees {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + offset
        val index = Math.abs(dayOfYear % HADEES.size)
        return HADEES[index]
    }

    fun getTodayMotivation(offset: Int = 0): DailyMotivation {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + offset
        val index = Math.abs(dayOfYear % MOTIVATION.size)
        return MOTIVATION[index]
    }
}
