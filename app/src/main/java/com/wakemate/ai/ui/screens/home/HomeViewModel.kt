package com.wakemate.ai.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.data.repository.DailyAyat
import com.wakemate.ai.data.repository.DailyHadees
import com.wakemate.ai.data.repository.DailyMotivation
import com.wakemate.ai.data.repository.InspirationRepository
import com.wakemate.ai.util.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val userName: String = "Ammar",
    val currentTimeString: String = "",
    val currentDateString: String = "",
    val nextAlarm: AlarmEntity? = null,
    val nextAlarmCountdown: String = "No alarms set",
    val pendingTasks: List<TaskEntity> = emptyList(),
    val completedTasks: List<TaskEntity> = emptyList(),
    val totalTasksCount: Int = 0,
    val completedPercentage: Int = 0,
    val activeInspirationTab: Int = 0, // 0: Ayat, 1: Hadees, 2: Motivation
    val inspirationOffset: Int = 0,
    val currentAyat: DailyAyat = InspirationRepository.getTodayAyat(),
    val currentHadees: DailyHadees = InspirationRepository.getTodayHadees(),
    val currentMotivation: DailyMotivation = InspirationRepository.getTodayMotivation(),
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

                val calendar = Calendar.getInstance()
                val timeStr = DateTimeUtils.formatTime(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
                val dateStr = DateTimeUtils.formatDate(calendar.timeInMillis)

                val offset = _uiState.value.inspirationOffset
                _uiState.value.copy(
                    userName = prefs.userName,
                    currentTimeString = timeStr,
                    currentDateString = dateStr,
                    nextAlarm = nextAlarm,
                    nextAlarmCountdown = countdown,
                    pendingTasks = pending,
                    completedTasks = completed,
                    totalTasksCount = total,
                    completedPercentage = percent,
                    currentAyat = InspirationRepository.getTodayAyat(offset),
                    currentHadees = InspirationRepository.getTodayHadees(offset),
                    currentMotivation = InspirationRepository.getTodayMotivation(offset)
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun switchInspirationTab(tabIndex: Int) {
        _uiState.update { it.copy(activeInspirationTab = tabIndex) }
    }

    fun nextInspiration() {
        _uiState.update { current ->
            val newOffset = current.inspirationOffset + 1
            current.copy(
                inspirationOffset = newOffset,
                currentAyat = InspirationRepository.getTodayAyat(newOffset),
                currentHadees = InspirationRepository.getTodayHadees(newOffset),
                currentMotivation = InspirationRepository.getTodayMotivation(newOffset)
            )
        }
    }

    fun prevInspiration() {
        _uiState.update { current ->
            val newOffset = if (current.inspirationOffset > 0) current.inspirationOffset - 1 else 0
            current.copy(
                inspirationOffset = newOffset,
                currentAyat = InspirationRepository.getTodayAyat(newOffset),
                currentHadees = InspirationRepository.getTodayHadees(newOffset),
                currentMotivation = InspirationRepository.getTodayMotivation(newOffset)
            )
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            prefsRepo.updateUserName(name)
        }
    }

    private fun findNextAlarm(alarms: List<AlarmEntity>): AlarmEntity? {
        if (alarms.isEmpty()) return null
        return alarms.minByOrNull { alarm ->
            DateTimeUtils.calculateNextTriggerMillis(alarm.hour, alarm.minute, alarm.daysOfWeek)
        }
    }
}
