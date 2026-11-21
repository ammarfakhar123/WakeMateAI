package com.wakemate.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Int> = emptyList(), // 1=Sun, 2=Mon, ..., 7=Sat
    val isVibrate: Boolean = true,
    val ringtoneTitle: String = "Default Alarm",
    val ringtoneUri: String = "",
    val hasVerification: Boolean = true, // 5-minute wake-up check
    val isChargingChallenge: Boolean = true, // Second alarm triggers charging challenge
    val isSnoozed: Boolean = false,
    val snoozeTimeMillis: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
