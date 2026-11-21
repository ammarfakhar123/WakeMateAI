package com.wakemate.ai.ui.screens.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wakemate.ai.data.local.entity.AlarmEntity
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*
import java.util.Calendar

@Composable
fun AddEditAlarmDialog(
    alarmToEdit: AlarmEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        hour: Int,
        minute: Int,
        label: String,
        days: List<Int>,
        isVibrate: Boolean,
        hasVerification: Boolean,
        isChargingChallenge: Boolean
    ) -> Unit
) {
    val initialCal = Calendar.getInstance()
    var hour by remember { mutableIntStateOf(alarmToEdit?.hour ?: initialCal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(alarmToEdit?.minute ?: initialCal.get(Calendar.MINUTE)) }
    var label by remember { mutableStateOf(alarmToEdit?.label ?: "Morning Wake Up") }
    var selectedDays by remember { mutableStateOf(alarmToEdit?.daysOfWeek?.toSet() ?: emptySet()) }
    var isVibrate by remember { mutableStateOf(alarmToEdit?.isVibrate ?: true) }
    var hasVerification by remember { mutableStateOf(alarmToEdit?.hasVerification ?: true) }
    var isChargingChallenge by remember { mutableStateOf(alarmToEdit?.isChargingChallenge ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        NeonCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            cornerRadius = 24.dp,
            backgroundColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (alarmToEdit == null) "Set New Alarm" else "Edit Alarm",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Time Spinners / Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Box
                    TimeNumberBox(
                        value = hour,
                        range = 0..23,
                        onValueChange = { hour = it }
                    )

                    Text(
                        text = ":",
                        color = NeonCyan,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute Box
                    TimeNumberBox(
                        value = minute,
                        range = 0..59,
                        onValueChange = { minute = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alarm Label
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Alarm Label") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Days Selection
                Text(
                    text = "Repeat Days",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val daysMap = listOf(
                    1 to "Sun", 2 to "Mon", 3 to "Tue", 4 to "Wed", 5 to "Thu", 6 to "Fri", 7 to "Sat"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysMap.forEach { (dayNum, dayLabel) ->
                        val isSelected = selectedDays.contains(dayNum)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkCardBorder,
                                    CircleShape
                                )
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        selectedDays - dayNum
                                    } else {
                                        selectedDays + dayNum
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayLabel.take(1),
                                color = if (isSelected) DarkBg else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5-Min Wake-Up Verification Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "5-Min Wake-Up Check 😴",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Verifies you are awake 5m after stop",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = hasVerification,
                        onCheckedChange = { hasVerification = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBg,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCardBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Charging Challenge Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Charging Challenge 🔌",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Requires plugging phone to stop 2nd alarm",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isChargingChallenge,
                        onCheckedChange = { isChargingChallenge = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBg,
                            checkedTrackColor = NeonPurple,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCardBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                hour,
                                minute,
                                label,
                                selectedDays.toList(),
                                isVibrate,
                                hasVerification,
                                isChargingChallenge
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Alarm", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeNumberBox(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                val next = if (value + 1 > range.last) range.first else value + 1
                onValueChange(next)
            }
        ) {
            Text("▲", color = NeonCyan, fontSize = 16.sp)
        }

        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 54.dp)
                .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = {
                val prev = if (value - 1 < range.first) range.last else value - 1
                onValueChange(prev)
            }
        ) {
            Text("▼", color = NeonCyan, fontSize = 16.sp)
        }
    }
}
