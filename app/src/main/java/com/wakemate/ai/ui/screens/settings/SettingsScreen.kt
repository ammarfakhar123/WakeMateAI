package com.wakemate.ai.ui.screens.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val prefs by viewModel.preferences.collectAsState()
    val context = LocalContext.current

    val alarmManager = remember { context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager }
    val canScheduleExact = remember(prefs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager != null) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "PREFERENCES",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "App Settings",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section 0: User Name Setting
        item {
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = DarkCardBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROFILE & NAME",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Name (Apna Naam):",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var nameInput by remember(prefs.userName) { mutableStateOf(prefs.userName) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    viewModel.updateUserName(nameInput.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 1: Android System Permissions & Exact Alarm Notice
        item {
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccent = if (canScheduleExact) NeonCyan else NeonPink,
                backgroundColor = DarkSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (canScheduleExact) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null,
                            tint = if (canScheduleExact) NeonCyan else NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canScheduleExact) "Exact Alarms Enabled" else "Exact Alarm Permission Required",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (canScheduleExact) {
                            "Android exact alarms are fully active. WakeMate will ring at the exact second even during Doze mode."
                        } else {
                            "Android 12+ requires exact alarm permission for reliable waking. Tap below to enable in Settings."
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    if (!canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Grant Permission", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section 2: Alarm & Challenge Settings
        item {
            Text(
                text = "ALARM & CHALLENGE",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Volume Slider
                    Text(
                        text = "Alarm Volume: ${(prefs.alarmVolume * 100).toInt()}%",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = prefs.alarmVolume,
                        onValueChange = { viewModel.updateAlarmVolume(it) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = DarkCardBorder
                        )
                    )

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Vibration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration", color = TextPrimary, fontSize = 14.sp)
                        Switch(
                            checked = prefs.vibrationEnabled,
                            onCheckedChange = { viewModel.updateVibration(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBg,
                                checkedTrackColor = NeonCyan
                            )
                        )
                    }

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Wake-Up Verification Delay (default 5 min)
                    Text(
                        text = "Wake-Up Verification Delay: ${prefs.verificationDelayMinutes} min",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val delayOptions = listOf(1, 5, 10, 15)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        delayOptions.forEach { d ->
                            val isSel = prefs.verificationDelayMinutes == d
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.updateVerificationDelay(d) },
                                color = if (isSel) NeonCyan else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (d == 1) "1m (Test)" else "${d}m",
                                    color = if (isSel) DarkBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Response Timeout (default 10s)
                    Text(
                        text = "Wake-Up Response Timeout: ${prefs.responseTimeoutSeconds} seconds",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val timeoutOptions = listOf(10, 15, 30, 60)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        timeoutOptions.forEach { s ->
                            val isSel = prefs.responseTimeoutSeconds == s
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.updateResponseTimeout(s) },
                                color = if (isSel) NeonPink else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${s}s",
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Charging Challenge Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Charging Challenge Alarm", color = TextPrimary, fontSize = 14.sp)
                            Text("Requires phone plugged into charger", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = prefs.chargingChallengeEnabled,
                            onCheckedChange = { viewModel.updateChargingChallenge(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBg,
                                checkedTrackColor = NeonPurple
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Task Reminders & Schedule
        item {
            Text(
                text = "TASK REMINDERS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Automatic Periodic Reminder: Every ${prefs.taskReminderIntervalHours} hours",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val intervalOptions = listOf(1, 3, 6, 12, 24)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        intervalOptions.forEach { h ->
                            val isSel = prefs.taskReminderIntervalHours == h
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.updateTaskReminderInterval(h) },
                                color = if (isSel) NeonCyan else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${h}h",
                                    color = if (isSel) DarkBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Assistant Personality & Language
        item {
            Text(
                text = "AI ASSISTANT",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Personality
                    Text("Personality Style", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    val personalities = listOf("FRIENDLY", "MOTIVATIONAL", "STRICT")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        personalities.forEach { p ->
                            val isSel = prefs.assistantPersonality.equals(p, ignoreCase = true)
                            Surface(
                                modifier = Modifier.clickable { viewModel.updatePersonality(p) },
                                color = if (isSel) NeonPurple else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = p,
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Language
                    Text("Communication Language", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    val languages = listOf("en" to "English", "roman_ur" to "Roman Urdu", "ur" to "اردو")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        languages.forEach { (code, name) ->
                            val isSel = prefs.language == code
                            Surface(
                                modifier = Modifier.clickable { viewModel.updateLanguage(code) },
                                color = if (isSel) NeonCyan else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSel) DarkBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Divider(color = DarkCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Optional Gemini API Key
                    var apiKeyInput by remember(prefs.geminiApiKey) { mutableStateOf(prefs.geminiApiKey) }
                    Text(
                        text = "Google Gemini API Key (Optional)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Offline NLP is always active. Providing a key unlocks advanced generative dialogue.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = {
                            apiKeyInput = it
                            viewModel.updateGeminiApiKey(it.trim())
                        },
                        placeholder = { Text("Paste AI Studio API Key...", color = TextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
