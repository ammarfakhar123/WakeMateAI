package com.wakemate.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateTimeMillis: Long = System.currentTimeMillis() + (6 * 3600 * 1000), // Default 6 hrs
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val isCompleted: Boolean = false,
    val category: String = "General", // Study, Work, Health, Personal, General
    val reminderIntervalHours: Int = 6, // 1, 3, 6, 12, 24
    val repeatOption: String = "NONE", // NONE, DAILY, WEEKLY
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
