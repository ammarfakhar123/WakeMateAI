package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.service.AlarmService
import com.wakemate.ai.service.ChargingChallengeService
import com.wakemate.ai.util.AlarmScheduler
import com.wakemate.ai.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WakeupVerificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_STOP_ALARM = "com.wakemate.ai.ACTION_STOP_ALARM"
        const val ACTION_TRIGGER_VERIFICATION = "com.wakemate.ai.ACTION_TRIGGER_VERIFICATION"
        const val ACTION_USER_AWAKE = "com.wakemate.ai.ACTION_USER_AWAKE"
        const val ACTION_USER_SLEEPY = "com.wakemate.ai.ACTION_USER_SLEEPY"
        const val BROADCAST_VERIFICATION_STARTED = "com.wakemate.ai.BROADCAST_VERIFICATION_STARTED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)
        val scheduler = AlarmScheduler(context)
        val database = WakeMateDatabase.getDatabase(context)

        when (intent.action) {
            ACTION_STOP_ALARM -> {
                // Stop first alarm service
                AlarmService.stop(context, alarmId)
                // Schedule 5-minute wake-up check
                scheduler.scheduleWakeupVerification(alarmId, delayMinutes = 5)
            }

            ACTION_TRIGGER_VERIFICATION -> {
                // 1. Show notification with quick replies
                NotificationHelper.showWakeupVerificationNotification(context, alarmId)

                // 2. Schedule 10-second response timeout
                scheduler.scheduleResponseTimeout(alarmId, timeoutSeconds = 10)

                // 3. Insert assistant prompt into chat
                CoroutineScope(Dispatchers.IO).launch {
                    database.chatMessageDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ASSISTANT",
                            text = "Hey! Are you awake? 😴 You have 10 seconds to confirm, or the Charging Challenge alarm will ring!",
                            quickReplies = listOf("Yes, I'm awake ✅", "No, still sleepy 😴"),
                            isVerificationPrompt = true
                        )
                    )
                }

                // 4. Send local broadcast for in-app circular countdown timer if active
                val notifyApp = Intent(BROADCAST_VERIFICATION_STARTED).apply {
                    setPackage(context.packageName)
                    putExtra("EXTRA_ALARM_ID", alarmId)
                    putExtra("TIMEOUT_SECONDS", 10)
                }
                context.sendBroadcast(notifyApp)
            }

            ACTION_USER_AWAKE -> {
                // Cancel timeout and notification
                scheduler.cancelResponseTimeout(alarmId)
                NotificationHelper.cancelNotification(context, NotificationHelper.NOTIFICATION_ID_VERIFY)

                CoroutineScope(Dispatchers.IO).launch {
                    database.chatMessageDao().insertMessage(
                        ChatMessageEntity(
                            sender = "USER",
                            text = "Yes, I'm awake ✅"
                        )
                    )
                    database.chatMessageDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ASSISTANT",
                            text = "Awesome! 🌟 Verification cleared. You're wide awake and ready to conquer your goals today!",
                            quickReplies = listOf("Show pending tasks 📋", "Add new task ➕")
                        )
                    )
                }
            }

            ACTION_USER_SLEEPY -> {
                // User admitted still sleepy -> trigger second alarm immediately!
                scheduler.cancelResponseTimeout(alarmId)
                NotificationHelper.cancelNotification(context, NotificationHelper.NOTIFICATION_ID_VERIFY)

                CoroutineScope(Dispatchers.IO).launch {
                    database.chatMessageDao().insertMessage(
                        ChatMessageEntity(
                            sender = "USER",
                            text = "No, still sleepy 😴"
                        )
                    )
                }

                // Start Charging Challenge!
                ChargingChallengeService.start(context, alarmId)
            }
        }
    }
}
