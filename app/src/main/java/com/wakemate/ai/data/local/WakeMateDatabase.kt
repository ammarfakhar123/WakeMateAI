package com.wakemate.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wakemate.ai.data.local.dao.AlarmDao
import com.wakemate.ai.data.local.dao.ChatMessageDao
import com.wakemate.ai.data.local.dao.TaskDao
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AlarmEntity::class, TaskEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WakeMateDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun taskDao(): TaskDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: WakeMateDatabase? = null

        fun getDatabase(context: Context): WakeMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WakeMateDatabase::class.java,
                    "wakemate_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDefaultData(database)
                    }
                }
            }

            private suspend fun seedDefaultData(database: WakeMateDatabase) {
                val taskDao = database.taskDao()
                val chatDao = database.chatMessageDao()
                val alarmDao = database.alarmDao()

                // Default Alarms
                alarmDao.insertAlarm(
                    AlarmEntity(
                        hour = 7,
                        minute = 0,
                        label = "Morning Wake Up ☀️",
                        isEnabled = true,
                        daysOfWeek = listOf(2, 3, 4, 5, 6), // Mon-Fri
                        isVibrate = true,
                        hasVerification = true,
                        isChargingChallenge = true
                    )
                )

                // 5 Example default tasks
                val now = System.currentTimeMillis()
                val hourMillis = 3600 * 1000L
                taskDao.insertTask(
                    TaskEntity(
                        title = "Study programming",
                        description = "Practice Kotlin and Jetpack Compose state management",
                        dueDateTimeMillis = now + (3 * hourMillis),
                        priority = "HIGH",
                        category = "Study",
                        reminderIntervalHours = 3
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Exercise & Stretching",
                        description = "30-minute cardio and morning workout routine",
                        dueDateTimeMillis = now + (6 * hourMillis),
                        priority = "MEDIUM",
                        category = "Health",
                        reminderIntervalHours = 6
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Complete assignments",
                        description = "Finish university mobile application design report",
                        dueDateTimeMillis = now + (12 * hourMillis),
                        priority = "HIGH",
                        category = "Study",
                        reminderIntervalHours = 6
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Drink water (2 Liters)",
                        description = "Stay hydrated throughout the day",
                        dueDateTimeMillis = now + (2 * hourMillis),
                        priority = "LOW",
                        category = "Health",
                        reminderIntervalHours = 1
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Work on personal projects",
                        description = "Build innovative AI features for portfolio apps",
                        dueDateTimeMillis = now + (24 * hourMillis),
                        priority = "MEDIUM",
                        category = "Personal",
                        reminderIntervalHours = 12
                    )
                )

                // Welcome message in assistant chat
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "ASSISTANT",
                        text = "Good morning! ☀️ I am WakeMate, your AI companion. I'm here to ensure you wake up on time, stay focused on your goals, and manage your day effortlessly. How can I help you today?\n\nTip: You can talk to me in English, Roman Urdu (e.g. 'Mujhe 6 ghante baad study yaad dilana'), or Urdu!",
                        quickReplies = listOf(
                            "Yes, I'm awake ✅",
                            "Show pending tasks 📋",
                            "What's my next task? 🎯"
                        )
                    )
                )
            }
        }
    }
}
