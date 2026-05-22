package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppSetting
import com.example.data.model.HadithBookmark
import com.example.data.model.PrayerLog
import com.example.data.model.QuranProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface DeenDao {
    // Prayer Logs
    @Query("SELECT * FROM prayer_logs ORDER BY date DESC")
    fun getAllPrayerLogs(): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE userId = :userId ORDER BY date DESC")
    fun getPrayerLogsForUser(userId: Int): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE userId = :userId AND date = :date")
    suspend fun getPrayerLogsForUserOnDateDirect(userId: Int, date: String): List<PrayerLog>

    @Query("SELECT * FROM prayer_logs WHERE userId = :userId AND date = :date")
    fun getPrayerLogsForUserOnDate(userId: Int, date: String): Flow<List<PrayerLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerLog(log: PrayerLog)

    @Query("DELETE FROM prayer_logs WHERE userId = :userId AND date = :date AND prayerName = :prayerName")
    suspend fun deletePrayerLog(userId: Int, date: String, prayerName: String)

    // Hadith Bookmarks
    @Query("SELECT * FROM hadith_bookmarks")
    fun getHadithBookmarks(): Flow<List<HadithBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkHadith(bookmark: HadithBookmark)

    @Query("DELETE FROM hadith_bookmarks WHERE hadithId = :hadithId")
    suspend fun unbookmarkHadith(hadithId: Int)

    // Quran Progress
    @Query("SELECT * FROM quran_progress WHERE id = 1")
    fun getQuranProgress(): Flow<QuranProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuranProgress(progress: QuranProgress)

    // App Settings
    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSetting>>

    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSetting)
}

@Database(
    entities = [
        PrayerLog::class,
        HadithBookmark::class,
        QuranProgress::class,
        AppSetting::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deenDao(): DeenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shared_deen_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
