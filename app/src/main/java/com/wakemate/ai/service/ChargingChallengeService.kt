package com.wakemate.ai.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.content.ContextCompat
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.ui.screens.alarm.ChargingChallengeActivity
import com.wakemate.ai.util.BatteryUtils
import com.wakemate.ai.util.NotificationHelper
import kotlinx.coroutines.*

class ChargingChallengeService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentAlarmId: Long = -1
    private var isReceiverRegistered = false
    private var pollingJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    companion object {
        const val ACTION_START_CHALLENGE = "ACTION_START_CHALLENGE"
        const val ACTION_CHARGING_DETECTED = "ACTION_CHARGING_DETECTED"
        const val ACTION_EMERGENCY_OVERRIDE = "ACTION_EMERGENCY_OVERRIDE"

        fun start(context: Context, alarmId: Long) {
            val intent = Intent(context, ChargingChallengeService::class.java).apply {
                action = ACTION_START_CHALLENGE
                putExtra("EXTRA_ALARM_ID", alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun onChargingDetected(context: Context) {
            val intent = Intent(context, ChargingChallengeService::class.java).apply {
                action = ACTION_CHARGING_DETECTED
            }
            context.startService(intent)
        }
    }

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_POWER_CONNECTED ||
                (intent?.action == Intent.ACTION_BATTERY_CHANGED && BatteryUtils.isDeviceCharging(this@ChargingChallengeService))
            ) {
                handlePowerConnected()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "WakeMate:ChargingChallengeWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(15 * 60 * 1000L) // 15 min safety timeout
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(powerReceiver, filter)
        isReceiverRegistered = true

        startBatteryPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        currentAlarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)

        when (action) {
            ACTION_START_CHALLENGE -> {
                startForeground(
                    NotificationHelper.NOTIFICATION_ID_CHALLENGE,
                    NotificationHelper.buildChargingChallengeNotification(this, currentAlarmId)
                )

                // Launch Fullscreen Challenge Activity
                val challengeActivityIntent = Intent(this, ChargingChallengeActivity::class.java).apply {
                    putExtra("EXTRA_ALARM_ID", currentAlarmId)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(challengeActivityIntent)

                playIntenseAlarmAndVibrate()

                // Immediate check if already plugged in
                if (BatteryUtils.isDeviceCharging(this)) {
                    handlePowerConnected()
                }
            }
            ACTION_CHARGING_DETECTED -> {
                handlePowerConnected()
            }
            ACTION_EMERGENCY_OVERRIDE -> {
                // Safety override for emergency situations without bypassing OS controls
                stopAlarmAndComplete(wasEmergency = true)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    /**
     * Polls BatteryManager every 1.5 seconds in case system broadcasts are delayed.
     */
    private fun startBatteryPolling() {
        pollingJob = serviceScope.launch {
            while (isActive) {
                delay(1500)
                if (BatteryUtils.isDeviceCharging(this@ChargingChallengeService)) {
                    handlePowerConnected()
                    break
                }
            }
        }
    }

    private fun handlePowerConnected() {
        stopAlarmAndComplete(wasEmergency = false)
        stopSelf()
    }

    private fun playIntenseAlarmAndVibrate() {
        try {
            val alertUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

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

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            // Rapid, urgent vibration pulse
            val pattern = longArrayOf(0, 200, 100, 200, 100, 200, 100, 500)
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

    private fun stopAlarmAndComplete(wasEmergency: Boolean) {
        cleanUp()

        // Send local broadcast to ChargingChallengeActivity to update UI and finish
        val completionIntent = Intent("com.wakemate.ai.CHALLENGE_COMPLETED").apply {
            setPackage(packageName)
            putExtra("WAS_EMERGENCY", wasEmergency)
        }
        sendBroadcast(completionIntent)

        // Log to chat that charging challenge succeeded
        serviceScope.launch(Dispatchers.IO) {
            val database = WakeMateDatabase.getDatabase(applicationContext)
            val msgText = if (wasEmergency) {
                "Alarm stopped via emergency safety control. Please take care of yourself!"
            } else {
                "⚡ Charger connected! Charging challenge completed successfully! You are fully awake and ready for today."
            }
            database.chatMessageDao().insertMessage(
                ChatMessageEntity(
                    sender = "ASSISTANT",
                    text = msgText,
                    quickReplies = listOf("Show pending tasks 📋", "Add new task ➕")
                )
            )
        }
    }

    private fun cleanUp() {
        pollingJob?.cancel()
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
        cleanUp()
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(powerReceiver)
                isReceiverRegistered = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
