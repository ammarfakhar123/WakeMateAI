package com.wakemate.ai.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.components.PriorityBadge
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils

@Composable
fun HomeScreen(
    onNavigateToAlarms: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToChat: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Digital Clock & Date Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.currentDateString.uppercase(),
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.currentTimeString,
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                )
            }
        }

        // 2. AI Assistant Motivation Card
        item {
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                glowAccent = NeonPurple,
                backgroundColor = DarkSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = DarkBg,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WakeMate Assistant",
                            color = NeonPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.motivationalMessage,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // 3. Next Alarm Card
        item {
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAlarms() },
                glowAccent = if (state.nextAlarm?.isEnabled == true) NeonCyan else null
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Alarm,
                                contentDescription = "Next Alarm",
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NEXT ALARM",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        }

                        state.nextAlarm?.let { alarm ->
                            Switch(
                                checked = alarm.isEnabled,
                                onCheckedChange = { viewModel.toggleAlarm(alarm) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBg,
                                    checkedTrackColor = NeonCyan,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkCardBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.nextAlarm != null) {
                        val (timeStr, amPm) = DateTimeUtils.formatHourMinute(
                            state.nextAlarm!!.hour,
                            state.nextAlarm!!.minute
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = timeStr,
                                color = TextPrimary,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = amPm,
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = state.nextAlarm!!.label,
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = state.nextAlarmCountdown,
                                color = NeonPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Text(
                            text = "No active alarms",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 4. Daily Progress & Tasks Summary Card
        item {
            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Progress
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        val animatedPercent by animateFloatAsState(
                            targetValue = state.completedPercentage / 100f,
                            animationSpec = tween(1000),
                            label = "progress"
                        )

                        CircularProgressIndicator(
                            progress = animatedPercent,
                            modifier = Modifier.fillMaxSize(),
                            color = NeonCyan,
                            trackColor = DarkCardBorder,
                            strokeWidth = 7.dp,
                            strokeCap = StrokeCap.Round
                        )

                        Text(
                            text = "${state.completedPercentage}%",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY PRODUCTIVITY",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.completedTasks.size} of ${state.totalTasksCount} tasks completed",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Next: ${state.nextReminderInfo}",
                            color = NeonPurple,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Quick Pending Tasks Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENDING TASKS",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "See All (${state.pendingTasks.size})",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToTasks() }
                )
            }
        }

        if (state.pendingTasks.isEmpty()) {
            item {
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All tasks completed! Enjoy your day 🎯",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(state.pendingTasks.take(3)) { task ->
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 14.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskComplete(task) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonCyan,
                                checkmarkColor = DarkBg,
                                uncheckedColor = DarkCardBorder
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = DateTimeUtils.formatDateTime(task.dueDateTimeMillis),
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        PriorityBadge(priority = task.priority)
                    }
                }
            }
        }

        // 6. Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat AI", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToTasks,
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Task", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
