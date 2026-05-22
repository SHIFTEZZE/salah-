package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.Hadith
import com.example.data.database.HadithData
import com.example.data.database.QuranData
import com.example.data.model.PrayerLog
import com.example.data.model.QuranProgress
import com.example.data.repository.SalahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SalahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalahRepository
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Tracks current date filtered on screen
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Notification Prefs
    private val _notificationSetting = MutableStateFlow("short_adhan")
    val notificationSetting: StateFlow<String> = _notificationSetting.asStateFlow()

    // Companion Profile Info
    private val _companionName = MutableStateFlow("Salma")
    val companionName: StateFlow<String> = _companionName.asStateFlow()

    // Sound alert trigger message for previews
    private val _customSoundEvent = MutableStateFlow<String?>(null)
    val customSoundEvent: StateFlow<String?> = _customSoundEvent.asStateFlow()

    // Main lists
    val allLogs: StateFlow<List<PrayerLog>>

    // Bookmarks and Progress
    val bookmarkedHadiths: StateFlow<Set<Int>>
    val quranProgress: StateFlow<QuranProgress?>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SalahRepository(database.deenDao())

        allLogs = repository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedHadiths = repository.bookmarkedHadiths
            .combine(MutableStateFlow(emptySet<Int>())) { bookmarks, _ ->
                bookmarks.map { it.hadithId }.toSet()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet()
            )

        quranProgress = repository.quranProgress.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            // Load saved settings
            _notificationSetting.value = repository.getSettingValue("notification_setting", "short_adhan")
            _companionName.value = repository.getSettingValue("companion_name", "Salma")

            // Prepopulate database with historical entries for beautiful first launch UI if empty
            prepopulateDbIfEmpty()
        }
    }

    // Date Utilities
    fun getTodayDateString(): String {
        return sdf.format(Date())
    }

    fun changeSelectedDate(daysOffset: Int) {
        val calendar = Calendar.getInstance()
        val currentSelected = sdf.parse(_selectedDate.value) ?: Date()
        calendar.time = currentSelected
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
        _selectedDate.value = sdf.format(calendar.time)
    }

    fun setSelectedDate(dateString: String) {
        _selectedDate.value = dateString
    }

    // Toggle Prayer
    fun togglePrayer(userId: Int, date: String, prayerName: String, isCompleted: Boolean, isNawafil: Boolean) {
        viewModelScope.launch {
            repository.logPrayer(userId, date, prayerName, isCompleted, isNawafil)
            // If completed Fajr/Duhr, we can simulate an adhan sound demonstration
            if (isCompleted && userId == 1 && !isNawafil) {
                triggerAdhanSimulation(prayerName)
            }
        }
    }

    // Hadith Toggle
    fun toggleHadithBookmark(hadithId: Int) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = bookmarkedHadiths.value.contains(hadithId)
            repository.setHadithBookmark(hadithId, !isCurrentlyBookmarked)
        }
    }

    // Save reader progress
    fun updateQuranProgress(surahNumber: Int, surahName: String, ayahNumber: Int) {
        viewModelScope.launch {
            repository.saveQuranProgress(surahNumber, surahName, ayahNumber)
        }
    }

    // Update settings
    fun updateCompanionName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotEmpty()) {
            _companionName.value = trimmed
            viewModelScope.launch {
                repository.saveSetting("companion_name", trimmed)
            }
        }
    }

    fun updateNotificationSetting(newPref: String) {
        _notificationSetting.value = newPref
        viewModelScope.launch {
            repository.saveSetting("notification_setting", newPref)
        }
    }

    // Trigger dummy sound audio feedback
    private fun triggerAdhanSimulation(prayerName: String) {
        val message = when (_notificationSetting.value) {
            "full_adhan" -> "🕌 Playing Full Adhan audio for $prayerName prayer..."
            "short_adhan" -> "🎵 Playing Short Takbeer reminder for $prayerName..."
            "vibrate" -> "📳 Device Vibrating for $prayerName reminder..."
            else -> "🔔 Silent banner: Time for $prayerName!"
        }
        _customSoundEvent.value = message
    }

    fun dismissSoundEvent() {
        _customSoundEvent.value = null
    }

    // STREAK CALCULATION
    // Dynamic consecutive days where all 5 fard prayers are logged as complete
    fun calculateStreak(userId: Int): Int {
        val logs = allLogs.value.filter { it.userId == userId && !it.isNawafil && it.isCompleted }
        val logsByDate = logs.groupBy { it.date }

        var streak = 0
        val calendar = Calendar.getInstance()
        // Start evaluating from today
        var checkDateStr = sdf.format(calendar.time)

        // If today is not fully completed yet, we can check if yesterday is completed.
        // If yesterday is indeed completed, yesterday keeps the streak alive until today finishes.
        val todayFardCompletedCount = logsByDate[checkDateStr]?.size ?: 0
        if (todayFardCompletedCount == 5) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDateStr = sdf.format(calendar.time)
        } else if (todayFardCompletedCount > 0) {
            // Begun today but not completed yet. Let's see if yesterday has 5
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDateStr = sdf.format(calendar.time)
        } else {
            // Today has nothing yet. Let's start check from yesterday
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDateStr = sdf.format(calendar.time)
        }

        while (true) {
            val fardCompleted = logsByDate[checkDateStr]?.size ?: 0
            if (fardCompleted == 5) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDateStr = sdf.format(calendar.time)
            } else {
                break
            }
        }
        return streak
    }

    // Statistics for the last 7 days
    fun getFardCompletionRate(userId: Int): Float {
        val fardLogs = allLogs.value.filter { it.userId == userId && !it.isNawafil && it.isCompleted }
        if (fardLogs.isEmpty()) return 0f
        // Total possible fard over last 7 days = 35 prayers
        val totalPossible = 35f
        val completedCount = fardLogs.size.coerceAtMost(35)
        return (completedCount / totalPossible) * 100
    }

    fun getNawafilCount(userId: Int): Int {
        return allLogs.value.filter { it.userId == userId && it.isNawafil && it.isCompleted }.size
    }

    // Database pre-population with pretty records
    private suspend fun prepopulateDbIfEmpty() {
        val currentLogs = repository.allLogs.first()
        if (currentLogs.isEmpty()) {
            val calendar = Calendar.getInstance()
            val fardNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

            // Simulate the last 6 days of complete prayers for Me to build a beautiful 5+ day streak
            for (i in 1..6) {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val dateStr = sdf.format(calendar.time)

                // Log all fards as completed
                for (fard in fardNames) {
                    repository.logPrayer(userId = 1, date = dateStr, prayerName = fard, isCompleted = true, isNawafil = false)
                }
                // Random some Nawafil
                if (i % 2 == 0) {
                    repository.logPrayer(userId = 1, date = dateStr, prayerName = "Sunnah", isCompleted = true, isNawafil = true)
                }
                if (i % 3 == 0) {
                    repository.logPrayer(userId = 1, date = dateStr, prayerName = "Tahajjud", isCompleted = true, isNawafil = true)
                }
            }

            // Simulate 8 days of perfect logs for the Companion(Salma) to represent a stunning 8-day streak!
            calendar.time = Date()
            for (i in 1..8) {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val dateStr = sdf.format(calendar.time)

                for (fard in fardNames) {
                    repository.logPrayer(userId = 2, date = dateStr, prayerName = fard, isCompleted = true, isNawafil = false)
                }
                if (i % 2 != 0) {
                    repository.logPrayer(userId = 2, date = dateStr, prayerName = "Duha", isCompleted = true, isNawafil = true)
                }
            }

            // Populate some initial progress
            repository.saveQuranProgress(112, "Al-Ikhlas", 4)
        }
    }
}
