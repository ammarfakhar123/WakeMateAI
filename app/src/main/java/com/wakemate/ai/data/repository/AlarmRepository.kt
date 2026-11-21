package com.wakemate.ai.data.repository

import com.wakemate.ai.data.local.dao.AlarmDao
import com.wakemate.ai.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()
    val enabledAlarms: Flow<List<AlarmEntity>> = alarmDao.getEnabledAlarms()

    suspend fun getEnabledAlarmsList(): List<AlarmEntity> = alarmDao.getEnabledAlarmsList()

    suspend fun getAlarmById(id: Long): AlarmEntity? = alarmDao.getAlarmById(id)

    suspend fun insertAlarm(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

    suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)

    suspend fun deleteAlarmById(id: Long) = alarmDao.deleteAlarmById(id)

    suspend fun setAlarmEnabled(id: Long, isEnabled: Boolean) = alarmDao.setAlarmEnabled(id, isEnabled)

    suspend fun setAlarmSnoozed(id: Long, isSnoozed: Boolean, snoozeTime: Long) =
        alarmDao.setAlarmSnoozed(id, isSnoozed, snoozeTime)
}
