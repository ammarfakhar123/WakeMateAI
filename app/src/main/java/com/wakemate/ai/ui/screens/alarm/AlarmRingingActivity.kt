package com.wakemate.ai.ui.screens.alarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakemate.ai.service.AlarmService
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils
import java.util.Calendar

class AlarmRingingActivity : ComponentActivity() {

    private var alarmId: Long = -1
    private var alarmLabel: String = "Wake Up!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndShowOverLockscreen()

        alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)
        alarmLabel = intent.getStringExtra("EXTRA_ALARM_LABEL") ?: "Wake Up!"

        setContent {
            WakeMateAITheme {
                AlarmRingingScreen(
                    alarmLabel = alarmLabel,
                    onStop = {
                        AlarmService.stop(this@AlarmRingingActivity, alarmId)
                        finish()
                    },
                    onSnooze = {
                        AlarmService.snooze(this@AlarmRingingActivity, alarmId, 10)
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
}

@Composable
fun AlarmRingingScreen(
    alarmLabel: String,
    onStop: () -> Unit,
    onSnooze: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val (timeStr, amPm) = DateTimeUtils.formatHourMinute(
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )

    // Pulsing animation for outer alarm ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

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
            Spacer(modifier = Modifier.height(30.dp))

            // Header info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = alarmLabel.uppercase(),
                    color = NeonPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Smart Wake Alarm",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            // Pulsing Time Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                // Outer animated aura
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        radius = (size.minDimension / 2f) * pulseScale
                    )
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.4f),
                        radius = (size.minDimension / 2f) * 0.85f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = timeStr,
                        color = TextPrimary,
                        fontSize = 58.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = amPm,
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Notice about 5-min wake-up verification
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ 5-Min Wake-Up Check will activate when stopped.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Buttons: Snooze & Stop
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "STOP ALARM",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Snooze (+10 min)",
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
