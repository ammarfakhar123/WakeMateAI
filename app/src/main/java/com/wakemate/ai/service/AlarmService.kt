package com.wakemate.ai.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.content.ContextCompat
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.util.AlarmScheduler
import com.wakemate.ai.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentAlarmId: Long = -1

    companion object {
        const val ACTION_START_ALARM = "ACTION_START_ALARM"
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "ACTION_SNOOZE_ALARM"

        fun start(context: Context, alarmId: Long, alarmLabel: String = "Alarm") {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_START_ALARM
                putExtra("EXTRA_ALARM_ID", alarmId)
                putExtra("EXTRA_ALARM_LABEL", alarmLabel)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_STOP_ALARM
                putExtra("EXTRA_ALARM_ID", alarmId)
            }
            context.startService(intent)
        }

        fun snooze(context: Context, alarmId: Long, snoozeMinutes: Int = 10) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra("EXTRA_ALARM_ID", alarmId)
                putExtra("EXTRA_SNOOZE_MINUTES", snoozeMinutes)
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "WakeMate:AlarmWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L) // 10 minutes max
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        val alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)
        val alarmLabel = intent.getStringExtra("EXTRA_ALARM_LABEL") ?: "Wake Up!"
        currentAlarmId = alarmId

        when (action) {
            ACTION_START_ALARM -> {
                startForeground(
                    NotificationHelper.NOTIFICATION_ID_ALARM,
                    NotificationHelper.buildAlarmNotification(this, alarmId, alarmLabel)
                )
                playAlarmSoundAndVibrate()
            }
            ACTION_STOP_ALARM -> {
                stopAlarmAndTriggerVerification(alarmId)
                stopSelf()
            }
            ACTION_SNOOZE_ALARM -> {
                val snoozeMinutes = intent.getIntExtra("EXTRA_SNOOZE_MINUTES", 10)
                handleSnooze(alarmId, snoozeMinutes)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun playAlarmSoundAndVibrate() {
        try {
            // Sound
            val alertUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alertUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }

            // Vibration
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            val pattern = longArrayOf(0, 600, 300, 600, 300, 800)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmAndTriggerVerification(alarmId: Long) {
        cleanUpMediaAndVibrator()

        // Trigger 5-minute wake-up verification
        val scheduler = AlarmScheduler(applicationContext)
        scheduler.scheduleWakeupVerification(alarmId, delayMinutes = 5)

        // Reset snooze status in database
        val database = WakeMateDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            database.alarmDao().setAlarmSnoozed(alarmId, false, 0L)
        }
    }

    private fun handleSnooze(alarmId: Long, snoozeMinutes: Int) {
        cleanUpMediaAndVibrator()
        val snoozeTimeMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        val database = WakeMateDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = database.alarmDao().getAlarmById(alarmId)
            if (alarm != null) {
                database.alarmDao().setAlarmSnoozed(alarmId, true, snoozeTimeMillis)
                val updated = alarm.copy(isSnoozed = true, snoozeTimeMillis = snoozeTimeMillis)
                AlarmScheduler(applicationContext).scheduleAlarm(updated)
            }
        }
    }

    private fun cleanUpMediaAndVibrator() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        cleanUpMediaAndVibrator()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
