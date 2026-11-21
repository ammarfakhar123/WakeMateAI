package com.wakemate.ai.ui.screens.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.service.AlarmService
import com.wakemate.ai.service.ChargingChallengeService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WakeMateApp
    private val alarmRepo = app.alarmRepository
    private val scheduler = app.alarmScheduler

    val alarms: StateFlow<List<AlarmEntity>> = alarmRepo.allAlarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAlarm(
        hour: Int,
        minute: Int,
        label: String,
        daysOfWeek: List<Int>,
        isVibrate: Boolean,
        hasVerification: Boolean,
        isChargingChallenge: Boolean
    ) {
        viewModelScope.launch {
            val alarm = AlarmEntity(
                hour = hour,
                minute = minute,
                label = label.ifBlank { "Alarm" },
                isEnabled = true,
                daysOfWeek = daysOfWeek,
                isVibrate = isVibrate,
                hasVerification = hasVerification,
                isChargingChallenge = isChargingChallenge
            )
            val id = alarmRepo.insertAlarm(alarm)
            scheduler.scheduleAlarm(alarm.copy(id = id))
        }
    }

    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmRepo.updateAlarm(alarm)
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
            } else {
                scheduler.cancelAlarm(alarm.id)
            }
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val newEnabled = !alarm.isEnabled
            alarmRepo.setAlarmEnabled(alarm.id, newEnabled)
            if (newEnabled) {
                scheduler.scheduleAlarm(alarm.copy(isEnabled = true, isSnoozed = false))
            } else {
                scheduler.cancelAlarm(alarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            scheduler.cancelAlarm(alarm.id)
            alarmRepo.deleteAlarm(alarm)
        }
    }

    /**
     * Test function: Immediately rings the first alarm to verify audio and UI.
     */
    fun testRingAlarm(alarm: AlarmEntity) {
        AlarmService.start(getApplication(), alarm.id, alarm.label)
    }

    /**
     * Test function: Immediately tests the second charging challenge alarm.
     */
    fun testChargingChallenge(alarmId: Long = 9999L) {
        ChargingChallengeService.start(getApplication(), alarmId)
    }
}
