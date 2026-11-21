package com.wakemate.ai.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.service.ChargingChallengeService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessageEntity> = emptyList(),
    val quickReplies: List<String> = listOf("Yes, I'm awake ✅", "Show pending tasks 📋", "Add new task ➕"),
    val isVerificationActive: Boolean = false,
    val remainingCountdownSeconds: Int = 10,
    val isAiThinking: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WakeMateApp
    private val chatRepo = app.chatRepository
    private val taskRepo = app.taskRepository
    private val prefsRepo = app.userPreferencesRepository
    private val nluEngine = app.assistantNluEngine
    private val geminiService = app.geminiService
    private val scheduler = app.alarmScheduler

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var verificationAlarmId: Long = -1

    init {
        viewModelScope.launch {
            chatRepo.allMessages.collect { list ->
                val latest = list.lastOrNull()
                val isVerificationPrompt = latest?.isVerificationPrompt == true
                val replies = if (latest?.quickReplies?.isNotEmpty() == true) {
                    latest.quickReplies
                } else {
                    listOf("Yes, I'm awake ✅", "Show pending tasks 📋", "Add new task ➕")
                }

                _uiState.update {
                    it.copy(
                        messages = list,
                        quickReplies = replies
                    )
                }

                if (isVerificationPrompt && !_uiState.value.isVerificationActive) {
                    startInAppCountdown(alarmId = 1)
                }
            }
        }
    }

    fun startInAppCountdown(alarmId: Long, durationSec: Int = 10) {
        verificationAlarmId = alarmId
        countdownJob?.cancel()
        _uiState.update {
            it.copy(
                isVerificationActive = true,
                remainingCountdownSeconds = durationSec
            )
        }

        countdownJob = viewModelScope.launch {
            for (sec in durationSec downTo 0) {
                _uiState.update { it.copy(remainingCountdownSeconds = sec) }
                if (sec == 0) {
                    onCountdownExpired()
                    break
                }
                delay(1000)
            }
        }
    }

    private fun onCountdownExpired() {
        if (!_uiState.value.isVerificationActive) return
        _uiState.update { it.copy(isVerificationActive = false) }

        viewModelScope.launch {
            chatRepo.insertMessage(
                ChatMessageEntity(
                    sender = "ASSISTANT",
                    text = "⏰ 10-second timeout expired! You did not confirm you were awake. Starting Charging Challenge now! 🔌"
                )
            )
            // Trigger Second Alarm
            ChargingChallengeService.start(getApplication(), verificationAlarmId)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val trimmed = userText.trim()
        viewModelScope.launch {
            // 1. Insert User Message
            chatRepo.insertMessage(
                ChatMessageEntity(
                    sender = "USER",
                    text = trimmed
                )
            )

            // If verification was active, user responded!
            if (_uiState.value.isVerificationActive) {
                countdownJob?.cancel()
                _uiState.update { it.copy(isVerificationActive = false) }
                scheduler.cancelResponseTimeout(verificationAlarmId)
            }

            _uiState.update { it.copy(isAiThinking = true) }

            // 2. Fetch context
            val pendingTasks = taskRepo.getPendingTasksList()
            val prefs = prefsRepo.userPreferencesFlow.first()

            // 3. Process with offline NLU Engine first
            val nluResult = nluEngine.processMessage(
                userMessage = trimmed,
                pendingTasks = pendingTasks,
                personality = prefs.assistantPersonality,
                languagePreference = prefs.language
            )

            // If task was detected, insert into Room Database
            if (nluResult.createdTask != null) {
                taskRepo.insertTask(nluResult.createdTask)
            }

            // If user admitted sleepy during verification -> trigger charging challenge
            if (nluResult.isSleepyReported) {
                ChargingChallengeService.start(getApplication(), verificationAlarmId)
            }

            // Optional: If Gemini API key is configured and message is general, try Gemini
            var finalReply = nluResult.replyText
            if (prefs.geminiApiKey.isNotBlank() && nluResult.createdTask == null && !nluResult.isAwakeConfirmed) {
                val taskSummary = pendingTasks.take(5).joinToString(", ") { it.title }
                val aiResponse = geminiService.getAiResponse(
                    prompt = trimmed,
                    apiKey = prefs.geminiApiKey,
                    personality = prefs.assistantPersonality,
                    pendingTaskSummary = taskSummary.ifEmpty { "None" }
                )
                if (!aiResponse.isNullOrBlank()) {
                    finalReply = aiResponse
                }
            }

            _uiState.update { it.copy(isAiThinking = false) }

            // 4. Insert Assistant Message
            chatRepo.insertMessage(
                ChatMessageEntity(
                    sender = "ASSISTANT",
                    text = finalReply,
                    quickReplies = nluResult.quickReplies,
                    associatedTaskId = nluResult.createdTask?.id
                )
            )
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepo.clearHistory()
        }
    }
}
