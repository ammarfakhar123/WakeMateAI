package com.wakemate.ai.ui.screens.alarm

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakemate.ai.service.ChargingChallengeService
import com.wakemate.ai.ui.components.ChargingAnimation
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.BatteryUtils
import kotlinx.coroutines.delay

class ChargingChallengeActivity : ComponentActivity() {

    private var alarmId: Long = -1
    private var isReceiverRegistered = false

    private val challengeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.wakemate.ai.CHALLENGE_COMPLETED") {
                // Service detected charging or completed -> close activity after brief confirmation
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndShowOverLockscreen()

        alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)

        val filter = IntentFilter("com.wakemate.ai.CHALLENGE_COMPLETED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(challengeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(challengeReceiver, filter)
        }
        isReceiverRegistered = true

        setContent {
            WakeMateAITheme {
                ChargingChallengeScreen(
                    onEmergencyOverride = {
                        val serviceIntent = Intent(this, ChargingChallengeService::class.java).apply {
                            action = ChargingChallengeService.ACTION_EMERGENCY_OVERRIDE
                        }
                        startService(serviceIntent)
                        finish()
                    }
                )
            }
        }
    }

    private fun turnScreenOnAndShowOverLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onDestroy() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(challengeReceiver)
                isReceiverRegistered = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }
}

@Composable
fun ChargingChallengeScreen(
    onEmergencyOverride: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isCharging by remember { mutableStateOf(BatteryUtils.isDeviceCharging(context)) }
    var batteryPercentage by remember { mutableIntStateOf(BatteryUtils.getBatteryPercentage(context)) }
    var plugType by remember { mutableStateOf(BatteryUtils.getChargingPlugType(context)) }
    var showEmergencyConfirm by remember { mutableStateOf(false) }

    // Live polling check for charger connection
    LaunchedEffect(Unit) {
        while (!isCharging) {
            delay(1000)
            val currentlyCharging = BatteryUtils.isDeviceCharging(context)
            batteryPercentage = BatteryUtils.getBatteryPercentage(context)
            plugType = BatteryUtils.getChargingPlugType(context)
            if (currentlyCharging) {
                isCharging = true
                ChargingChallengeService.onChargingDetected(context)
                delay(2000) // Show success state for 2 seconds
                (context as? ComponentActivity)?.finish()
                break
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Urgent Challenge Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = NeonPink.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚡ WAKE-UP CHALLENGE ACTIVATED",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isCharging) "Charger Connected!" else "Charging Challenge 🔌",
                    color = if (isCharging) NeonCyan else TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isCharging) {
                        "Good morning! Your alarm is dismissed. ⚡"
                    } else {
                        "Wake up! Connect your phone to the charger to stop this alarm!"
                    },
                    color = TextSecondary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Central Animated Charging Graphic
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ChargingAnimation(
                    isChargingDetected = isCharging,
                    size = 180.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeonCard(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    backgroundColor = DarkSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Battery Level: $batteryPercentage%",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isCharging) plugType else "Unplugged ❌",
                            color = if (isCharging) NeonCyan else NeonPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Normal dismiss and snooze buttons are intentionally DISABLED for this second alarm!
            // Instead, we show instructions and a safe Emergency Override control as required by Android guidelines.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isCharging) {
                    Surface(
                        color = NeonCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Challenge Passed! Have a productive day!",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Text(
                        text = "🔒 Snooze & Dismiss are locked until plugged in.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    // Android System Compliance & Emergency Override
                    TextButton(
                        onClick = { showEmergencyConfirm = true }
                    ) {
                        Icon(Icons.Filled.Emergency, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Emergency Override",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showEmergencyConfirm) {
        AlertDialog(
            onDismissRequest = { showEmergencyConfirm = false },
            title = { Text("Emergency Override", color = TextPrimary) },
            text = {
                Text(
                    "Are you experiencing an emergency? In compliance with Android safety guidelines, you can override this challenge to access your phone immediately.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEmergencyConfirm = false
                        onEmergencyOverride()
                    }
                ) {
                    Text("Confirm Override", color = NeonPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
