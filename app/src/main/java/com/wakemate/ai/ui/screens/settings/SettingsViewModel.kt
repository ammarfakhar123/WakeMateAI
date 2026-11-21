package com.wakemate.ai.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.preferences.UserPreferences
import com.wakemate.ai.worker.TaskReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WakeMateApp
    private val prefsRepo = app.userPreferencesRepository

    val preferences: StateFlow<UserPreferences> = prefsRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateAlarmVolume(volume: Float) {
        viewModelScope.launch {
            prefsRepo.updateAlarmVolume(volume)
        }
    }

    fun updateVibration(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.updateVibration(enabled)
        }
    }

    fun updateVerificationDelay(minutes: Int) {
        viewModelScope.launch {
            prefsRepo.updateVerificationDelay(minutes)
        }
    }

    fun updateResponseTimeout(seconds: Int) {
        viewModelScope.launch {
            prefsRepo.updateResponseTimeout(seconds)
        }
    }

    fun updateChargingChallenge(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.updateChargingChallenge(enabled)
        }
    }

    fun updateTaskReminderInterval(hours: Int) {
        viewModelScope.launch {
            prefsRepo.updateTaskReminderInterval(hours)
            TaskReminderWorker.schedulePeriodicReminder(getApplication(), hours)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            prefsRepo.updateLanguage(language)
        }
    }

    fun updatePersonality(personality: String) {
        viewModelScope.launch {
            prefsRepo.updatePersonality(personality)
        }
    }

    fun updateGeminiApiKey(key: String) {
        viewModelScope.launch {
            prefsRepo.updateGeminiApiKey(key)
        }
    }
}
