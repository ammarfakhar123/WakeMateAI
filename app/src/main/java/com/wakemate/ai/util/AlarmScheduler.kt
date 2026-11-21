package com.wakemate.ai.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.service.receiver.AlarmReceiver
import com.wakemate.ai.service.receiver.VerificationTimeoutReceiver
import com.wakemate.ai.service.receiver.WakeupVerificationReceiver

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Checks if exact alarms are allowed on Android 12+ (API 31+).
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Schedules the main alarm.
     */
    fun scheduleAlarm(alarm: AlarmEntity) {
        val triggerMillis = if (alarm.isSnoozed && alarm.snoozeTimeMillis > System.currentTimeMillis()) {
            alarm.snoozeTimeMillis
        } else {
            DateTimeUtils.calculateNextTriggerMillis(alarm.hour, alarm.minute, alarm.daysOfWeek)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarm.id)
            putExtra("EXTRA_ALARM_LABEL", alarm.label)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactOrFallback(triggerMillis, pendingIntent)
    }

    /**
     * Cancels the scheduled alarm.
     */
    fun cancelAlarm(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Schedules the 5-minute wake-up verification alarm after the user stops the first alarm.
     */
    fun scheduleWakeupVerification(alarmId: Long, delayMinutes: Int = 5) {
        val triggerMillis = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        val intent = Intent(context, WakeupVerificationReceiver::class.java).apply {
            action = WakeupVerificationReceiver.ACTION_TRIGGER_VERIFICATION
            putExtra("EXTRA_ALARM_ID", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 50000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactOrFallback(triggerMillis, pendingIntent)
    }

    /**
     * Cancels pending 5-minute wake-up verification.
     */
    fun cancelWakeupVerification(alarmId: Long) {
        val intent = Intent(context, WakeupVerificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 50000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Schedules the 10-second response timeout once verification is delivered.
     */
    fun scheduleResponseTimeout(alarmId: Long, timeoutSeconds: Int = 10) {
        val triggerMillis = System.currentTimeMillis() + (timeoutSeconds * 1000L)

        val intent = Intent(context, VerificationTimeoutReceiver::class.java).apply {
            action = VerificationTimeoutReceiver.ACTION_TIMEOUT_EXPIRED
            putExtra("EXTRA_ALARM_ID", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 60000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactOrFallback(triggerMillis, pendingIntent)
    }

    /**
     * Cancels the response timeout when the user confirms awake.
     */
    fun cancelResponseTimeout(alarmId: Long) {
        val intent = Intent(context, VerificationTimeoutReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 60000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleExactOrFallback(triggerMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In case exact alarm permission was revoked
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }
}
