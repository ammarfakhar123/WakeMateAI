package com.wakemate.ai

import android.app.Application
import android.util.Log
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.data.preferences.UserPreferencesRepository
import com.wakemate.ai.data.repository.AlarmRepository
import com.wakemate.ai.data.repository.AssistantNluEngine
import com.wakemate.ai.data.repository.ChatRepository
import com.wakemate.ai.data.repository.GeminiService
import com.wakemate.ai.data.repository.TaskRepository
import com.wakemate.ai.util.AlarmScheduler
import com.wakemate.ai.util.NotificationHelper
import com.wakemate.ai.worker.TaskReminderWorker

class WakeMateApp : Application() {

    val database: WakeMateDatabase by lazy { WakeMateDatabase.getDatabase(this) }
    val alarmRepository: AlarmRepository by lazy { AlarmRepository(database.alarmDao()) }
    val taskRepository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
    val chatRepository: ChatRepository by lazy { ChatRepository(database.chatMessageDao()) }
    val userPreferencesRepository: UserPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val alarmScheduler: AlarmScheduler by lazy { AlarmScheduler(this) }
    val assistantNluEngine: AssistantNluEngine by lazy { AssistantNluEngine() }
    val geminiService: GeminiService by lazy { GeminiService() }

    companion object {
        lateinit var instance: WakeMateApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Safe uncaught exception logger
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("WakeMateApp", "CRITICAL UNCAUGHT CRASH: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // 1. Initialize notification channels safely
        try {
            NotificationHelper.createNotificationChannels(this)
        } catch (e: Throwable) {
            Log.e("WakeMateApp", "Failed to create notification channels", e)
        }

        // 2. Initialize background task reminders safely
        try {
            TaskReminderWorker.schedulePeriodicReminder(this, intervalHours = 6)
        } catch (e: Throwable) {
            Log.e("WakeMateApp", "Failed to schedule background reminder", e)
        }
    }
}
