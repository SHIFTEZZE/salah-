package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.Hadith
import com.example.data.database.HadithData
import com.example.data.database.QuranData
import com.example.data.database.Surah
import com.example.data.model.PrayerLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalahViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainAppScreen(viewModel: SalahViewModel) {
    val context = LocalContext.current
    val currentNavigation = remember { mutableStateOf("my_page") }

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val notificationSetting by viewModel.notificationSetting.collectAsStateWithLifecycle()
    val companionName by viewModel.companionName.collectAsStateWithLifecycle()
    val customSoundEvent by viewModel.customSoundEvent.collectAsStateWithLifecycle()

    val allLogs by viewModel.allLogs.collectAsStateWithLifecycle()
    val bookmarkedHadiths by viewModel.bookmarkedHadiths.collectAsStateWithLifecycle()
    val quranProgress by viewModel.quranProgress.collectAsStateWithLifecycle()

    // Filtered lists for fards and nawafils
    val todayMyLogs = allLogs.filter { it.userId == 1 && it.date == selectedDate }
    val todayCompanionLogs = allLogs.filter { it.userId == 2 && it.date == selectedDate }

    // Active screen for reading a Surah (null if none)
    val activeReadingSurah = remember { mutableStateOf<Surah?>(null) }

    // Display Adhan demo pop-ups or Toast if sound events are triggered
    LaunchedEffect(customSoundEvent) {
        customSoundEvent?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.dismissSoundEvent()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = currentNavigation.value == "my_page",
                    onClick = {
                        currentNavigation.value = "my_page"
                        activeReadingSurah.value = null
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "My Page") },
                    label = { Text("My Deen") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText,
                        indicatorColor = DeepEmerald
                    ),
                    modifier = Modifier.testTag("nav_my_page")
                )
                NavigationBarItem(
                    selected = currentNavigation.value == "companion",
                    onClick = {
                        currentNavigation.value = "companion"
                        activeReadingSurah.value = null
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = "Companion") },
                    label = { Text(companionName) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText,
                        indicatorColor = DeepEmerald
                    ),
                    modifier = Modifier.testTag("nav_companion")
                )
                NavigationBarItem(
                    selected = currentNavigation.value == "quran",
                    onClick = { currentNavigation.value = "quran" },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Quran") },
                    label = { Text("Quran") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText,
                        indicatorColor = DeepEmerald
                    ),
                    modifier = Modifier.testTag("nav_quran")
                )
                NavigationBarItem(
                    selected = currentNavigation.value == "hadith",
                    onClick = {
                        currentNavigation.value = "hadith"
                        activeReadingSurah.value = null
                    },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Hadith") },
                    label = { Text("Hadith") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        unselectedIconColor = MutedText,
                        unselectedTextColor = MutedText,
                        indicatorColor = DeepEmerald
                    ),
                    modifier = Modifier.testTag("nav_hadith")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = DeepDarkSlate
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Islamic Banner Header (Common between tabs)
                DeenHeader(
                    currentScreen = currentNavigation.value,
                    selectedDate = selectedDate,
                    myStreak = viewModel.calculateStreak(userId = 1),
                    partnerStreak = viewModel.calculateStreak(userId = 2),
                    partnerName = companionName,
                    onPrevDate = { viewModel.changeSelectedDate(-1) },
                    onNextDate = { viewModel.changeSelectedDate(1) },
                    onTodayDate = { viewModel.setSelectedDate(viewModel.getTodayDateString()) }
                )

                // Main Navigation Screen Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (currentNavigation.value) {
                        "my_page" -> {
                            MyDeenTab(
                                viewModel = viewModel,
                                selectedDate = selectedDate,
                                todayLogs = todayMyLogs,
                                notificationSetting = notificationSetting,
                                quranProgressText = quranProgress?.let { "Surah ${it.lastReadSurahName} (Ayah ${it.lastReadAyah})" } ?: "No progress recorded yet"
                            )
                        }
                        "companion" -> {
                            CompanionTab(
                                viewModel = viewModel,
                                selectedDate = selectedDate,
                                companionLogs = todayCompanionLogs,
                                companionName = companionName
                            )
                        }
                        "quran" -> {
                            AnimatedContent(
                                targetState = activeReadingSurah.value,
                                label = "QuranSwap"
                            ) { surah ->
                                if (surah == null) {
                                    QuranReaderTab(
                                        onSurahSelected = { activeReadingSurah.value = it },
                                        savedProgress = quranProgress
                                    )
                                } else {
                                    QuranTextDetailScreen(
                                        surah = surah,
                                        onBackPress = { activeReadingSurah.value = null },
                                        onMarkAsRead = { ayahNum ->
                                            viewModel.updateQuranProgress(surah.number, surah.nameEnglish, ayahNum)
                                            Toast.makeText(context, "Progress saved at Ayah $ayahNum!", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                        "hadith" -> {
                            HadithTab(
                                bookmarkedIdList = bookmarkedHadiths,
                                onBookmarkToggle = { id -> viewModel.toggleHadithBookmark(id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-COMPONENT: ISLAMIC COMMON HEADER
// ==========================================
@Composable
fun DeenHeader(
    currentScreen: String,
    selectedDate: String,
    myStreak: Int,
    partnerStreak: Int,
    partnerName: String,
    onPrevDate: () -> Unit,
    onNextDate: () -> Unit,
    onTodayDate: () -> Unit
) {
    val hijriDate = remember(selectedDate) {
        val suffix = selectedDate.takeLast(2).toIntOrNull() ?: 22
        val day = (suffix + 13) % 30 + 1
        val monthStr = if (day > 15) "Dhu al-Hijjah" else "Dhu al-Qi'dah"
        "$day $monthStr, 1447 AH"
    }

    val formattedGregorianDate = remember(selectedDate) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(selectedDate)
            if (parsedDate != null) {
                val outputFormat = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())
                outputFormat.format(parsedDate)
            } else {
                "Peace be upon you"
            }
        } catch (e: Exception) {
            "Peace be upon you"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formattedGregorianDate.uppercase(Locale.getDefault()),
                        fontSize = 11.sp,
                        color = MutedText,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Peace be upon you",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = GoldAccent
                    )
                }

                // Decorative Minimal Badge Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, DeepEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◈",
                        color = EmeraldPrimary,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection Header Switcher (Visible when tracking has active context)
            if (currentScreen == "my_page" || currentScreen == "companion") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardSurface)
                        .border(1.dp, DeepEmerald, RoundedCornerShape(24.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPrevDate, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ChevronLeft, "Previous Date", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onTodayDate() }
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(
                            text = if (selectedDate == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) "Today" else selectedDate,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "🌙 $hijriDate",
                            fontSize = 9.sp,
                            color = MutedText,
                            letterSpacing = 0.2.sp
                        )
                    }

                    IconButton(onClick = onNextDate, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ChevronRight, "Next Date", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: MY PAGE (PERSONAL DASHBOARD)
// ==========================================
@Composable
fun MyDeenTab(
    viewModel: SalahViewModel,
    selectedDate: String,
    todayLogs: List<PrayerLog>,
    notificationSetting: String,
    quranProgressText: String
) {
    val context = LocalContext.current
    val modifier = Modifier
    val fardList = listOf(
        "Fajr" to "05:15 AM",
        "Dhuhr" to "12:45 PM",
        "Asr" to "04:30 PM",
        "Maghrib" to "07:12 PM",
        "Isha" to "08:45 PM"
    )
    val nawafilList = listOf("Tahajjud", "Duha", "Sunnah", "Ishraq")

    var showSettingsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Clean Minimalism Stats Grid Row (Streak & Compulsory rates)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Streak Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, DeepEmerald),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CURRENT STREAK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${viewModel.calculateStreak(userId = 1)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Days",
                                fontSize = 11.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                // Card 2: Progress Card
                val completedFards = todayLogs.filter { !it.isNawafil && it.isCompleted }.size
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, DeepEmerald),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DAILY SANCTUARY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$completedFards",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "/ 5 Done",
                                fontSize = 11.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Deep Spiritual Daily Progress Card
        item {
            val completedFards = todayLogs.filter { !it.isNawafil && it.isCompleted }.size
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DeepEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Salah",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DeepEmerald)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$completedFards / 5 Done",
                                fontSize = 10.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    // Clean, beautifully proportional minimalist progress indicator bar
                    LinearProgressIndicator(
                        progress = { completedFards / 5f },
                        color = EmeraldPrimary,
                        trackColor = DeepEmerald,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        // Fard Prayers Tracking Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "OBLIGATORY DEEN",
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                fardList.forEach { (name, time) ->
                    val isChecked = todayLogs.any { it.prayerName == name && it.isCompleted }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardSurface)
                            .border(
                                width = 1.dp,
                                color = if (isChecked) EmeraldPrimary.copy(alpha = 0.4f) else DeepEmerald,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                viewModel.togglePrayer(
                                    userId = 1,
                                    date = selectedDate,
                                    prayerName = name,
                                    isCompleted = !isChecked,
                                    isNawafil = false
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isChecked) EmeraldPrimary else DeepEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.Check else Icons.Outlined.Mosque,
                                    contentDescription = name,
                                    tint = if (isChecked) Color.Black else MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isChecked) GoldAccent else MutedText,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Time: $time",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }

                        // Checkbox
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                viewModel.togglePrayer(
                                    userId = 1,
                                    date = selectedDate,
                                    prayerName = name,
                                    isCompleted = !isChecked,
                                    isNawafil = false
                                )
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldPrimary,
                                uncheckedColor = MutedText.copy(alpha = 0.5f),
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("prayer_check_$name")
                        )
                    }
                }
            }
        }

        // Nawafil Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Nawafil (Voluntary Prayers)",
                    fontWeight = FontWeight.Bold,
                    color = SoftGold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    nawafilList.forEach { name ->
                        val isChecked = todayLogs.any { it.prayerName == name && it.isCompleted }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isChecked) DeepEmerald else CardSurface)
                                .border(
                                    1.dp,
                                    if (isChecked) GoldAccent else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.togglePrayer(
                                        userId = 1,
                                        date = selectedDate,
                                        prayerName = name,
                                        isCompleted = !isChecked,
                                        isNawafil = true
                                    )
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = name,
                                    tint = if (isChecked) GoldAccent else MutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    color = if (isChecked) Color.White else MutedText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quran resume quick link
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DeepEmerald)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MenuBook, "Quran Progress", tint = GoldAccent)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Quran Progress",
                            color = SoftGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = quranProgressText,
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // Fast Islamic Notification Setting Preferences
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, DeepEmerald, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Prayer Alerts & Adhan",
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent,
                            fontSize = 14.sp
                        )
                        val summary = when (notificationSetting) {
                            "full_adhan" -> "Full beautiful custom adhan audio demonstration"
                            "short_adhan" -> "Short Takbeer alert reminder"
                            "vibrate" -> "Pure device vibrating pulse"
                            else -> "Silent notification overlay"
                        }
                        Text(text = summary, fontSize = 11.sp, color = MutedText)
                    }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("notification_settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, "Set alerts preference", tint = EmeraldPrimary)
                    }
                }
            }
        }

        // Spiritual Wisdom Quote of the Day (Styled from the Clean Minimalism Design HTML)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DeepEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "“",
                        color = EmeraldPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                    Column {
                        Text(
                            text = "He who follows a path in quest of knowledge, Allah will make the path of Jannah easy for him.",
                            color = MutedText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "SAHIH MUSLIM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, DeepEmerald),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Customize Alerts & Adhan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )

                    val modes = listOf(
                        "full_adhan" to "🕌 Full Beautiful Adhan Audio",
                        "short_adhan" to "🎵 Short Takbeer Alarm",
                        "vibrate" to "📳 Vibration Pulse Only",
                        "silent" to "🔕 Silent Banner Display"
                    )

                    modes.forEach { (mode, title) ->
                        val isSelected = notificationSetting == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DeepEmerald else Color.Transparent)
                                .clickable {
                                    viewModel.updateNotificationSetting(mode)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(title, color = if (isSelected) Color.White else MutedText, fontSize = 14.sp)
                            if (isSelected) {
                                Icon(Icons.Default.Check, "Selected", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Button(
                        onClick = { showSettingsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Preferences", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 2: COMPANION TAB (ACCOUNTABILITY SANDBOX)
// ==========================================
@Composable
fun CompanionTab(
    viewModel: SalahViewModel,
    selectedDate: String,
    companionLogs: List<PrayerLog>,
    companionName: String
) {
    val context = LocalContext.current
    var showEditNameDialog by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf(companionName) }

    // Toggle sandbox mode allowing testing of partner checkoffs locally!
    var sandboxLogMode by remember { mutableStateOf(false) }

    val fards = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Companion info summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DeepEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(DeepEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, companionName, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = companionName,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 17.sp,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "Your deen companion in accountability",
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }

                        IconButton(
                            onClick = { showEditNameDialog = true },
                            modifier = Modifier.testTag("edit_companion_name_btn")
                        ) {
                            Icon(Icons.Default.Edit, "Edit Connected Name", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val completedComp = companionLogs.filter { !it.isNawafil && it.isCompleted }.size
                    Text(
                        text = "$companionName's Completed Prayers: $completedComp of 5 Fards",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { completedComp / 5f },
                        color = EmeraldPrimary,
                        trackColor = DeepEmerald,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        // Companion Prayer Completion Indicators
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DeepEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$companionName's Salah Status",
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent,
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepEmerald)
                                .clickable { sandboxLogMode = !sandboxLogMode }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (sandboxLogMode) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Sandbox control",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sandboxLogMode) "Sandbox: Editable" else "Sandbox Mode",
                                fontSize = 9.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    fards.forEach { name ->
                        val isChecked = companionLogs.any { it.prayerName == name && it.isCompleted }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardSurface)
                                .border(1.dp, if (isChecked) EmeraldPrimary.copy(alpha = 0.3f) else DeepEmerald, RoundedCornerShape(16.dp))
                                .clickable(enabled = sandboxLogMode) {
                                    viewModel.togglePrayer(
                                        userId = 2,
                                        date = selectedDate,
                                        prayerName = name,
                                        isCompleted = !isChecked,
                                        isNawafil = false
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChecked) EmeraldPrimary else MutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    name,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isChecked) GoldAccent else MutedText,
                                    fontSize = 14.sp
                                )
                            }

                            if (sandboxLogMode) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        viewModel.togglePrayer(
                                            userId = 2,
                                            date = selectedDate,
                                            prayerName = name,
                                            isCompleted = !isChecked,
                                            isNawafil = false
                                        )
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = EmeraldPrimary,
                                        checkmarkColor = Color.Black
                                    )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isChecked) SoftMint.copy(alpha = 0.2f) else CardSurface)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isChecked) "Completed" else "Incomplete",
                                        fontSize = 11.sp,
                                        color = if (isChecked) SoftMint else MutedText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mutual Encouragement / Nudge accountability features
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DeepEmerald)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encourage & Motivate $companionName",
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keep companion consistently on path without public social media pressure.",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "👉 Gentled Nudge dispatched to $companionName's device!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nudge_companion_btn")
                        ) {
                            Icon(Icons.Default.Notifications, "Nudge", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Nudge", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val famousHadith = HadithData.hadiths.random()
                                Toast.makeText(context, "📜 Shared Hadith with $companionName: \"${famousHadith.textEnglish}\"", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("share_hadith_btn")
                        ) {
                            Icon(Icons.Default.Share, "Share", tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Random Hadith", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showEditNameDialog) {
        Dialog(onDismissRequest = { showEditNameDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, DeepEmerald),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Define Deen Companion Name",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DeepDarkSlate,
                            unfocusedContainerColor = DeepDarkSlate,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = EmeraldPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("companion_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showEditNameDialog = false }) {
                            Text("Cancel", color = MutedText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateCompanionName(inputName)
                                showEditNameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Save Connected Partner", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 3: QURAN READER TAB
// ==========================================
@Composable
fun QuranReaderTab(
    onSurahSelected: (Surah) -> Unit,
    savedProgress: com.example.data.model.QuranProgress?
) {
    val surahs = QuranData.surahs
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            surahs
        } else {
            surahs.filter {
                it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                        it.nameTranslation.contains(searchQuery, ignoreCase = true) ||
                        it.nameArabic.contains(searchQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Quran Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, DeepEmerald)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Noble Quran Reader",
                    color = GoldAccent,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Review authentic Islamic surahs verse-by-verse with English meaning & bookmark checkpoints.",
                    fontSize = 12.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Light,
                    lineHeight = 16.sp
                )

                savedProgress?.let { progress ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepEmerald)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bookmark, "Checkpoints", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Last resume: Surah ${progress.lastReadSurahName} (Ayah ${progress.lastReadAyah})",
                            fontSize = 11.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Surahs...", color = MutedText, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = EmeraldPrimary, modifier = Modifier.size(18.dp)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = EmeraldPrimary,
                unfocusedIndicatorColor = DeepEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quran_search_bar")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Surah List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredSurahs) { surah ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .border(1.dp, DeepEmerald, RoundedCornerShape(16.dp))
                        .clickable { onSurahSelected(surah) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                surah.number.toString(),
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = surah.nameEnglish,
                                fontWeight = FontWeight.Medium,
                                color = GoldAccent,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${surah.nameTranslation} • ${surah.ayahs.size} Verses",
                                color = MutedText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }

                    Text(
                        text = surah.nameArabic,
                        color = EmeraldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            if (filteredSurahs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SentimentDissatisfied, "No surahs found", tint = MutedText, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Surahs match your query", color = MutedText, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-SCREEN: QURAN VERSE READER LAYOUT
// ==========================================
@Composable
fun QuranTextDetailScreen(
    surah: Surah,
    onBackPress: () -> Unit,
    onMarkAsRead: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Simple back navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(Icons.Default.ArrowBack, "Back", tint = EmeraldPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = surah.nameEnglish,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "${surah.nameTranslation} • ${surah.ayahs.size} Verses",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }

        // Bismillah Header (Except for selections which have Al-Kursi, etc.)
        if (surah.number != 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    color = SoftGold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Quran Scrollable Verses list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(surah.ayahs) { ayah ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ayah Number Indicator
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(DeepEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ayah.number.toString(),
                                    fontSize = 10.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Quick Bookmark Button for this Ayah
                            IconButton(
                                onClick = { onMarkAsRead(ayah.number) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark at Ayah ${ayah.number}",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Arabic text
                        Text(
                            text = ayah.textArabic,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // English meaning
                        Text(
                            text = ayah.textEnglish,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 4: HADITH TAB (BUKHARI SYSTEM)
// ==========================================
@Composable
fun HadithTab(
    bookmarkedIdList: Set<Int>,
    onBookmarkToggle: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?> (null) }
    var showBookmarksOnly by remember { mutableStateOf(false) }

    val categories = listOf("Faith", "Salah (Prayer)", "Brotherhood", "Quran", "Good Manners")

    val filteredHadiths = remember(searchQuery, selectedCategory, showBookmarksOnly, bookmarkedIdList) {
        HadithData.hadiths.filter { hadith ->
            val matchesSearch = if (searchQuery.trim().isEmpty()) {
                true
            } else {
                hadith.textEnglish.contains(searchQuery, ignoreCase = true) ||
                        hadith.narrator.contains(searchQuery, ignoreCase = true) ||
                        hadith.textArabic.contains(searchQuery) ||
                        hadith.reference.contains(searchQuery, ignoreCase = true)
            }

            val matchesCategory = if (selectedCategory == null) {
                true
            } else {
                hadith.category == selectedCategory
            }

            val matchesBookmark = if (showBookmarksOnly) {
                bookmarkedIdList.contains(hadith.id)
            } else {
                true
            }

            matchesSearch && matchesCategory && matchesBookmark
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Noble Hadith banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sahih al-Bukhari Database",
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Lightweight searchable database of authentic Prophetic narrations on deen and brotherhood.",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar & Filter options
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Bukhari Hadiths...", color = MutedText, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = MutedText) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = EmeraldPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hadith_search_bar")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category pills filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectedCategory == null) EmeraldPrimary else CardSurface)
                    .clickable { selectedCategory = null }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "All Categories",
                    color = if (selectedCategory == null) Color.Black else MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) EmeraldPrimary else CardSurface)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        cat,
                        color = if (isSelected) Color.Black else MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bookmark filtering trigger switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Show bookmarked hadiths only",
                color = SoftGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = showBookmarksOnly,
                onCheckedChange = { showBookmarksOnly = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EmeraldPrimary,
                    checkedTrackColor = DeepEmerald,
                    uncheckedThumbColor = MutedText,
                    uncheckedTrackColor = CardSurface
                ),
                modifier = Modifier.testTag("hadith_bookmark_switch")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable hadiths list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredHadiths) { hadith ->
                val isBookmarked = bookmarkedIdList.contains(hadith.id)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = hadith.reference,
                                    fontSize = 11.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Narrated by ${hadith.narrator}",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }

                            // Star bookmark item icon
                            IconButton(onClick = { onBookmarkToggle(hadith.id) }) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Toggle Bookmark",
                                    tint = if (isBookmarked) GoldAccent else MutedText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Arabic narration
                        Text(
                            text = hadith.textArabic,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // English narration
                        Text(
                            text = "\"${hadith.textEnglish}\"",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider(color = DeepDarkSlate, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category & Description badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = hadith.description,
                                fontSize = 10.sp,
                                color = MutedText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepEmerald)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = hadith.category,
                                    fontSize = 10.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (filteredHadiths.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (showBookmarksOnly) Icons.Default.StarBorder else Icons.Default.Search,
                            contentDescription = "No results",
                            tint = MutedText,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (showBookmarksOnly) "No bookmarked hadiths found in database" else "No Hadiths match your filters",
                            color = MutedText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
