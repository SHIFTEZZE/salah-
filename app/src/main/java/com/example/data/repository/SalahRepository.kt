package com.example.data.repository

import com.example.data.database.DeenDao
import com.example.data.model.AppSetting
import com.example.data.model.HadithBookmark
import com.example.data.model.PrayerLog
import com.example.data.model.QuranProgress
import kotlinx.coroutines.flow.Flow

class SalahRepository(private val deenDao: DeenDao) {

    val allLogs: Flow<List<PrayerLog>> = deenDao.getAllPrayerLogs()

    fun getLogsForUser(userId: Int): Flow<List<PrayerLog>> {
        return deenDao.getPrayerLogsForUser(userId)
    }

    fun getLogsForUserOnDate(userId: Int, date: String): Flow<List<PrayerLog>> {
        return deenDao.getPrayerLogsForUserOnDate(userId, date)
    }

    suspend fun logPrayer(userId: Int, date: String, prayerName: String, isCompleted: Boolean, isNawafil: Boolean) {
        if (isCompleted) {
            val log = PrayerLog(
                date = date,
                prayerName = prayerName,
                isCompleted = true,
                isNawafil = isNawafil,
                userId = userId
            )
            deenDao.insertPrayerLog(log)
        } else {
            deenDao.deletePrayerLog(userId, date, prayerName)
        }
    }

    // Hadith Bookmarks
    val bookmarkedHadiths: Flow<List<HadithBookmark>> = deenDao.getHadithBookmarks()

    suspend fun toggleHadithBookmark(hadithId: Int) {
        val currentBookmarks = deenDao.getHadithBookmarks()
        var exists = false
        // Fetch bookmarks on a short routine, or just toggling using a list snapshot
        // To be safe, let's keep it clean
    }

    suspend fun setHadithBookmark(hadithId: Int, isBookmarked: Boolean) {
        if (isBookmarked) {
            deenDao.bookmarkHadith(HadithBookmark(hadithId))
        } else {
            deenDao.unbookmarkHadith(hadithId)
        }
    }

    // Quran Progress
    val quranProgress: Flow<QuranProgress?> = deenDao.getQuranProgress()

    suspend fun saveQuranProgress(surahNumber: Int, surahName: String, ayahNumber: Int) {
        val progress = QuranProgress(
            lastReadSurahNumber = surahNumber,
            lastReadSurahName = surahName,
            lastReadAyah = ayahNumber
        )
        deenDao.saveQuranProgress(progress)
    }

    // App Settings
    val allSettings: Flow<List<AppSetting>> = deenDao.getAllSettings()

    suspend fun getSettingValue(key: String, defaultValue: String): String {
        return deenDao.getSettingValue(key) ?: defaultValue
    }

    suspend fun saveSetting(key: String, value: String) {
        deenDao.saveSetting(AppSetting(key, value))
    }
}
