package com.wakemate.ai.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.util.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val currentTimeString: String = "",
    val currentDateString: String = "",
    val nextAlarm: AlarmEntity? = null,
    val nextAlarmCountdown: String = "No alarms set",
    val pendingTasks: List<TaskEntity> = emptyList(),
    val completedTasks: List<TaskEntity> = emptyList(),
    val totalTasksCount: Int = 0,
    val completedPercentage: Int = 0,
    val motivationalMessage: String = "Welcome to WakeMate AI! Let's conquer the day.",
    val nextReminderInfo: String = "Scheduled every 6 hours"
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WakeMateApp
    private val alarmRepo = app.alarmRepository
    private val taskRepo = app.taskRepository
    private val prefsRepo = app.userPreferencesRepository
    private val alarmScheduler = app.alarmScheduler

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Observe alarms and calculate next upcoming alarm
        viewModelScope.launch {
            combine(
                alarmRepo.allAlarms,
                taskRepo.allTasks,
                prefsRepo.userPreferencesFlow
            ) { alarms, tasks, prefs ->
                val enabledAlarms = alarms.filter { it.isEnabled }
                val nextAlarm = findNextAlarm(enabledAlarms)
                val countdown = if (nextAlarm != null) {
                    val trigger = DateTimeUtils.calculateNextTriggerMillis(
                        nextAlarm.hour,
                        nextAlarm.minute,
                        nextAlarm.daysOfWeek
                    )
                    DateTimeUtils.getTimeRemainingString(trigger)
                } else {
                    "No alarms set"
                }

                val pending = tasks.filter { !it.isCompleted }
                val completed = tasks.filter { it.isCompleted }
                val total = tasks.size
                val percent = if (total > 0) ((completed.size.toFloat() / total) * 100).toInt() else 0

                val motivation = generateMotivationalMessage(
                    personality = prefs.assistantPersonality,
                    pendingCount = pending.size,
                    language = prefs.language
                )

                val reminderInfo = "Scheduled every ${prefs.taskReminderIntervalHours} hours"

                val now = System.currentTimeMillis()
                val calendar = Calendar.getInstance()
                val timeStr = DateTimeUtils.formatTime(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
                val dateStr = DateTimeUtils.formatDate(now)

                HomeUiState(
                    currentTimeString = timeStr,
                    currentDateString = dateStr,
                    nextAlarm = nextAlarm,
                    nextAlarmCountdown = countdown,
                    pendingTasks = pending,
                    completedTasks = completed,
                    totalTasksCount = total,
                    completedPercentage = percent,
                    motivationalMessage = motivation,
                    nextReminderInfo = reminderInfo
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun findNextAlarm(enabledAlarms: List<AlarmEntity>): AlarmEntity? {
        if (enabledAlarms.isEmpty()) return null
        return enabledAlarms.minByOrNull {
            DateTimeUtils.calculateNextTriggerMillis(it.hour, it.minute, it.daysOfWeek)
        }
    }

    private fun generateMotivationalMessage(
        personality: String,
        pendingCount: Int,
        language: String
    ): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }

        if (language == "roman_ur") {
            return when {
                pendingCount == 0 -> "Sub set hai! Koi pending task nahi hai. Mazay karo! 🚀"
                pendingCount <= 2 -> "Aap ke paas $pendingCount tasks hain. Thori mehnat aur sab complete! 💪"
                else -> "Aaj $pendingCount tasks karne hain! Time zaya mat karo, lag jao kaam pe! ⚡"
            }
        }

        return when (personality) {
            "MOTIVATIONAL" -> "$timeGreeting! 🌟 You have $pendingCount pending mission(s). Every step forward counts!"
            "STRICT" -> "Attention! 🎯 $pendingCount task(s) waiting. No excuses, make progress now!"
            else -> "$timeGreeting! ☀️ You have $pendingCount task(s) planned today. How can WakeMate help?"
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val newEnabled = !alarm.isEnabled
            alarmRepo.setAlarmEnabled(alarm.id, newEnabled)
            if (newEnabled) {
                alarmScheduler.scheduleAlarm(alarm.copy(isEnabled = true))
            } else {
                alarmScheduler.cancelAlarm(alarm.id)
            }
        }
    }

    fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch {
            taskRepo.setTaskCompleted(task.id, !task.isCompleted)
        }
    }
}
