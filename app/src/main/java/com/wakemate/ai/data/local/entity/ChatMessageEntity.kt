package com.wakemate.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "ASSISTANT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val quickReplies: List<String> = emptyList(),
    val associatedTaskId: Long? = null,
    val isVerificationPrompt: Boolean = false
)
