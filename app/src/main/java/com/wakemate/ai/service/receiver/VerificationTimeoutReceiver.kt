package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.service.ChargingChallengeService
import com.wakemate.ai.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerificationTimeoutReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TIMEOUT_EXPIRED = "com.wakemate.ai.ACTION_TIMEOUT_EXPIRED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)

        // Cancel verification notification
        NotificationHelper.cancelNotification(context, NotificationHelper.NOTIFICATION_ID_VERIFY)

        // Log to chat
        val database = WakeMateDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            database.chatMessageDao().insertMessage(
                ChatMessageEntity(
                    sender = "ASSISTANT",
                    text = "⏰ 10 seconds expired with no response! Triggering Charging Challenge now!",
                    quickReplies = emptyList()
                )
            )
        }

        // Trigger Second Alarm: Charging Challenge!
        ChargingChallengeService.start(context, alarmId)
    }
}
