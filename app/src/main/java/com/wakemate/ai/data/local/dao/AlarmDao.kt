package com.wakemate.ai.data.local.dao

import androidx.room.*
import com.wakemate.ai.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getEnabledAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    suspend fun getEnabledAlarmsList(): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Long)

    @Query("UPDATE alarms SET isEnabled = :isEnabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean)

    @Query("UPDATE alarms SET isSnoozed = :isSnoozed, snoozeTimeMillis = :snoozeTime WHERE id = :alarmId")
    suspend fun setAlarmSnoozed(alarmId: Long, isSnoozed: Boolean, snoozeTime: Long)
}
