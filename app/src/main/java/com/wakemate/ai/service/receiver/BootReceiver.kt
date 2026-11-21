package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.util.AlarmScheduler
import com.wakemate.ai.worker.TaskReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val scheduler = AlarmScheduler(context)
            val database = WakeMateDatabase.getDatabase(context)

            // Reschedule all active alarms
            CoroutineScope(Dispatchers.IO).launch {
                val enabledAlarms = database.alarmDao().getEnabledAlarmsList()
                enabledAlarms.forEach { alarm ->
                    scheduler.scheduleAlarm(alarm)
                }
            }

            // Ensure task reminder worker is active
            TaskReminderWorker.schedulePeriodicReminder(context, intervalHours = 6)
        }
    }
}
