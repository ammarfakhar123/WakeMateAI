package com.wakemate.ai.ui.screens.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils

@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = viewModel()
) {
    val alarms by viewModel.alarms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var alarmToEdit by remember { mutableStateOf<AlarmEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SMART ALARMS",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Wake Schedule",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Test Challenge button
                    OutlinedButton(
                        onClick = { viewModel.testChargingChallenge() },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Challenge", color = NeonPurple, fontSize = 12.sp)
                    }
                }
            }

            if (alarms.isEmpty()) {
                item {
                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.AlarmOff,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Alarms Created Yet",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the '+' button below to set your first wake-up alarm.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            } else {
                items(alarms) { alarm ->
                    AlarmItemCard(
                        alarm = alarm,
                        onToggle = { viewModel.toggleAlarm(alarm) },
                        onEdit = { alarmToEdit = alarm },
                        onDelete = { viewModel.deleteAlarm(alarm) },
                        onTestRing = { viewModel.testRingAlarm(alarm) }
                    )
                }
            }
        }

        // Floating Action Button to add alarm
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NeonCyan,
            contentColor = DarkBg,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Alarm", modifier = Modifier.size(28.dp))
        }
    }

    if (showAddDialog || alarmToEdit != null) {
        AddEditAlarmDialog(
            alarmToEdit = alarmToEdit,
            onDismiss = {
                showAddDialog = false
                alarmToEdit = null
            },
            onSave = { hour, minute, label, days, isVibrate, hasVerif, isChallenge ->
                if (alarmToEdit == null) {
                    viewModel.addAlarm(hour, minute, label, days, isVibrate, hasVerif, isChallenge)
                } else {
                    viewModel.updateAlarm(
                        alarmToEdit!!.copy(
                            hour = hour,
                            minute = minute,
                            label = label,
                            daysOfWeek = days,
                            isVibrate = isVibrate,
                            hasVerification = hasVerif,
                            isChargingChallenge = isChallenge
                        )
                    )
                }
                showAddDialog = false
                alarmToEdit = null
            }
        )
    }
}

@Composable
fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestRing: () -> Unit
) {
    val (timeStr, amPm) = DateTimeUtils.formatHourMinute(alarm.hour, alarm.minute)
    val daysStr = DateTimeUtils.getDaysString(alarm.daysOfWeek)

    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        glowAccent = if (alarm.isEnabled) NeonCyan else null
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & AM/PM
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeStr,
                        color = if (alarm.isEnabled) TextPrimary else TextMuted,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = amPm,
                        color = if (alarm.isEnabled) NeonCyan else TextMuted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Switch
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DarkBg,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkCardBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Label & Days
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = alarm.label,
                    color = if (alarm.isEnabled) TextPrimary else TextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = daysStr,
                    color = if (alarm.isEnabled) NeonPurple else TextMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges & Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (alarm.hasVerification) {
                        Surface(
                            color = NeonPurple.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "5m Verify 😴",
                                color = NeonPurple,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (alarm.isChargingChallenge) {
                        Surface(
                            color = NeonPink.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Charging 🔌",
                                color = NeonPink,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onTestRing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Test Ring",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Alarm",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
