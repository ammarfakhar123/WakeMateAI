package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.service.AlarmService
import com.wakemate.ai.ui.screens.alarm.AlarmRingingActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)
        val alarmLabel = intent.getStringExtra("EXTRA_ALARM_LABEL") ?: "Wake Up!"

        // 1. Start foreground AlarmService (handles sound, vibration, wake lock)
        AlarmService.start(context, alarmId, alarmLabel)

        // 2. Launch full-screen AlarmRingingActivity
        val activityIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_LABEL", alarmLabel)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(activityIntent)
    }
}
