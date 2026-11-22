package com.wakemate.ai.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wakemate.ai.MainActivity
import com.wakemate.ai.R
import com.wakemate.ai.service.receiver.TaskActionReceiver
import com.wakemate.ai.service.receiver.WakeupVerificationReceiver
import com.wakemate.ai.ui.screens.alarm.AlarmRingingActivity
import com.wakemate.ai.ui.screens.alarm.ChargingChallengeActivity

object NotificationHelper {

    const val CHANNEL_ALARM = "channel_alarm_high"
    const val CHANNEL_VERIFY = "channel_wakeup_verify"
    const val CHANNEL_CHALLENGE = "channel_charging_challenge"
    const val CHANNEL_TASK = "channel_task_reminders"

    const val NOTIFICATION_ID_ALARM = 1001
    const val NOTIFICATION_ID_VERIFY = 1002
    const val NOTIFICATION_ID_CHALLENGE = 1003
    const val NOTIFICATION_ID_TASK_BASE = 2000

    fun createNotificationChannels(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

                val alarmSoundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                // 1. Alarm Channel
                val alarmChannel = NotificationChannel(
                    CHANNEL_ALARM,
                    "Smart Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "High priority smart alarms that wake you up"
                    alarmSoundUri?.let { setSound(it, audioAttributes) }
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

                // 2. Wake-up Verification Channel
                val verifyChannel = NotificationChannel(
                    CHANNEL_VERIFY,
                    "Wake-Up Verification",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Checks if you are truly awake 5 minutes after alarm dismiss"
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

                // 3. Charging Challenge Channel
                val challengeChannel = NotificationChannel(
                    CHANNEL_CHALLENGE,
                    "Charging Challenge Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Urgent alarm that requires plugging in your charger to stop"
                    alarmSoundUri?.let { setSound(it, audioAttributes) }
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 150, 300, 150, 600)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

                // 4. Task Reminders Channel
                val taskChannel = NotificationChannel(
                    CHANNEL_TASK,
                    "To-Do & Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Periodic reminders for pending tasks and goals"
                    enableVibration(true)
                }

                notificationManager.createNotificationChannels(
                    listOf(alarmChannel, verifyChannel, challengeChannel, taskChannel)
                )
            }
        } catch (e: Throwable) {
            android.util.Log.e("NotificationHelper", "Failed to create notification channels", e)
        }
    }

    /**
     * Builds foreground notification for the first active alarm.
     */
    fun buildAlarmNotification(
        context: Context,
        alarmId: Long,
        alarmLabel: String
    ): Notification {
        val fullScreenIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, WakeupVerificationReceiver::class.java).apply {
            action = WakeupVerificationReceiver.ACTION_STOP_ALARM
            putExtra("EXTRA_ALARM_ID", alarmId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 10).toInt(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ALARM)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ Alarm: $alarmLabel")
            .setContentText("Wake up! Tap to stop or snooze.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Stop Alarm", stopPendingIntent)
            .build()
    }

    /**
     * Builds Wake-Up Verification notification sent exactly 5 minutes after stop.
     */
    fun showWakeupVerificationNotification(context: Context, alarmId: Long) {
        val chatIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("NAVIGATE_TO", "chat")
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("IS_VERIFICATION_FLOW", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val chatPendingIntent = PendingIntent.getActivity(
            context,
            1005,
            chatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: "Yes, I'm awake ✅"
        val awakeIntent = Intent(context, WakeupVerificationReceiver::class.java).apply {
            action = WakeupVerificationReceiver.ACTION_USER_AWAKE
            putExtra("EXTRA_ALARM_ID", alarmId)
        }
        val awakePendingIntent = PendingIntent.getBroadcast(
            context,
            1006,
            awakeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: "No, still sleepy 😴"
        val sleepyIntent = Intent(context, WakeupVerificationReceiver::class.java).apply {
            action = WakeupVerificationReceiver.ACTION_USER_SLEEPY
            putExtra("EXTRA_ALARM_ID", alarmId)
        }
        val sleepyPendingIntent = PendingIntent.getBroadcast(
            context,
            1007,
            sleepyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_VERIFY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hey! Are you awake? 😴")
            .setContentText("Confirm in 10 seconds or the charging challenge alarm rings!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(chatPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Yes, I'm awake ✅", awakePendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Still sleepy 😴", sleepyPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_VERIFY, notification)
    }

    /**
     * Builds notification for Charging Challenge.
     */
    fun buildChargingChallengeNotification(context: Context, alarmId: Long): Notification {
        val challengeIntent = Intent(context, ChargingChallengeActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1008,
            challengeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_CHALLENGE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔌 Charging Challenge Alarm!")
            .setContentText("Connect phone to charger to stop this alarm!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    /**
     * Shows a Task Reminder notification with quick complete action.
     */
    fun showTaskReminderNotification(context: Context, taskId: Long, taskTitle: String, priority: String) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("NAVIGATE_TO", "tasks")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            (taskId + 200).toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, TaskActionReceiver::class.java).apply {
            action = TaskActionReceiver.ACTION_MARK_TASK_COMPLETE
            putExtra("EXTRA_TASK_ID", taskId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + 300).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Reminder: $taskTitle 💻")
            .setContentText("Priority: $priority • Tap to review or mark done.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Mark Done ✅", completePendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((NOTIFICATION_ID_TASK_BASE + taskId).toInt(), notification)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
