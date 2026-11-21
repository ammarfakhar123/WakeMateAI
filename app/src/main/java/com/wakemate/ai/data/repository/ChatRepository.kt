package com.wakemate.ai.data.repository

import com.wakemate.ai.data.local.dao.ChatMessageDao
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatMessageDao: ChatMessageDao) {
    val allMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()
    val latestMessage: Flow<ChatMessageEntity?> = chatMessageDao.getLatestMessage()

    suspend fun insertMessage(message: ChatMessageEntity): Long = chatMessageDao.insertMessage(message)

    suspend fun clearHistory() = chatMessageDao.clearHistory()
}
