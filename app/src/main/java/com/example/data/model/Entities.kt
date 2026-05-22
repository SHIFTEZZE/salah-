package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_logs")
data class PrayerLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val prayerName: String, // Fajr, Dhuhr, Asr, Maghrib, Isha, Tahajjud, Duha, Sunnah, Ishraq
    val isCompleted: Boolean,
    val isNawafil: Boolean,
    val userId: Int // 1 = Me, 2 = Companion
)

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmark(
    @PrimaryKey val hadithId: Int,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_progress")
data class QuranProgress(
    @PrimaryKey val id: Int = 1,
    val lastReadSurahNumber: Int,
    val lastReadSurahName: String,
    val lastReadAyah: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
