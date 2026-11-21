package com.wakemate.ai

import android.app.Application
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

    lateinit var database: WakeMateDatabase
        private set

    lateinit var alarmRepository: AlarmRepository
        private set

    lateinit var taskRepository: TaskRepository
        private set

    lateinit var chatRepository: ChatRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    lateinit var assistantNluEngine: AssistantNluEngine
        private set

    lateinit var geminiService: GeminiService
        private set

    companion object {
        lateinit var instance: WakeMateApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Initialize notification channels
        NotificationHelper.createNotificationChannels(this)

        // 2. Initialize database & repositories
        database = WakeMateDatabase.getDatabase(this)
        alarmRepository = AlarmRepository(database.alarmDao())
        taskRepository = TaskRepository(database.taskDao())
        chatRepository = ChatRepository(database.chatMessageDao())
        userPreferencesRepository = UserPreferencesRepository(this)
        alarmScheduler = AlarmScheduler(this)
        assistantNluEngine = AssistantNluEngine()
        geminiService = GeminiService()

        // 3. Initialize background task reminders (every 6 hours)
        TaskReminderWorker.schedulePeriodicReminder(this, intervalHours = 6)
    }
}
