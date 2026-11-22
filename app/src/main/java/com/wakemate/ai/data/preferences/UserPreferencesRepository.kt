package com.wakemate.ai.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val userName: String = "Ammar",
    val alarmVolume: Float = 0.9f,
    val vibrationEnabled: Boolean = true,
    val verificationDelayMinutes: Int = 5,
    val responseTimeoutSeconds: Int = 10,
    val chargingChallengeEnabled: Boolean = true,
    val taskReminderIntervalHours: Int = 6,
    val language: String = "en", // "en", "ur", "roman_ur"
    val assistantPersonality: String = "FRIENDLY", // "FRIENDLY", "MOTIVATIONAL", "STRICT"
    val geminiApiKey: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val ALARM_VOLUME = floatPreferencesKey("alarm_volume")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val VERIFICATION_DELAY_MIN = intPreferencesKey("verification_delay_minutes")
        val RESPONSE_TIMEOUT_SEC = intPreferencesKey("response_timeout_seconds")
        val CHARGING_CHALLENGE_ENABLED = booleanPreferencesKey("charging_challenge_enabled")
        val TASK_REMINDER_HOURS = intPreferencesKey("task_reminder_interval_hours")
        val LANGUAGE = stringPreferencesKey("app_language")
        val PERSONALITY = stringPreferencesKey("assistant_personality")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                userName = preferences[Keys.USER_NAME] ?: "Ammar",
                alarmVolume = preferences[Keys.ALARM_VOLUME] ?: 0.9f,
                vibrationEnabled = preferences[Keys.VIBRATION_ENABLED] ?: true,
                verificationDelayMinutes = preferences[Keys.VERIFICATION_DELAY_MIN] ?: 5,
                responseTimeoutSeconds = preferences[Keys.RESPONSE_TIMEOUT_SEC] ?: 10,
                chargingChallengeEnabled = preferences[Keys.CHARGING_CHALLENGE_ENABLED] ?: true,
                taskReminderIntervalHours = preferences[Keys.TASK_REMINDER_HOURS] ?: 6,
                language = preferences[Keys.LANGUAGE] ?: "en",
                assistantPersonality = preferences[Keys.PERSONALITY] ?: "FRIENDLY",
                geminiApiKey = preferences[Keys.GEMINI_API_KEY] ?: ""
            )
        }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = name }
    }

    suspend fun updateAlarmVolume(volume: Float) {
        context.dataStore.edit { it[Keys.ALARM_VOLUME] = volume }
    }

    suspend fun updateVibration(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun updateVerificationDelay(minutes: Int) {
        context.dataStore.edit { it[Keys.VERIFICATION_DELAY_MIN] = minutes }
    }

    suspend fun updateResponseTimeout(seconds: Int) {
        context.dataStore.edit { it[Keys.RESPONSE_TIMEOUT_SEC] = seconds }
    }

    suspend fun updateChargingChallenge(enabled: Boolean) {
        context.dataStore.edit { it[Keys.CHARGING_CHALLENGE_ENABLED] = enabled }
    }

    suspend fun updateTaskReminderInterval(hours: Int) {
        context.dataStore.edit { it[Keys.TASK_REMINDER_HOURS] = hours }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun updatePersonality(personality: String) {
        context.dataStore.edit { it[Keys.PERSONALITY] = personality }
    }

    suspend fun updateGeminiApiKey(key: String) {
        context.dataStore.edit { it[Keys.GEMINI_API_KEY] = key }
    }
}
