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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.components.PriorityBadge
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAlarms: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToChat: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempNameInput by remember { mutableStateOf("") }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Apna Naam Likhein",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = tempNameInput,
                    onValueChange = { tempNameInput = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNameInput.isNotBlank()) {
                            viewModel.updateUserName(tempNameInput.trim())
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Personalized Greeting Header & Name Editor
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ASSALAM-O-ALAIKUM",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${state.userName} 👋",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Surface(
                    onClick = {
                        tempNameInput = state.userName
                        showEditNameDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "✏️", fontSize = 11.sp)
                        Text(
                            text = "Name",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Digital Clock & Date Card
        item {
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = DarkCardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.currentDateString.uppercase(),
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.currentTimeString,
                        color = TextPrimary,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Early wake-up = Barakah in your day ☀️",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 3. 3-Pillar Daily Inspiration Card (Ayat / Hadees / Motivation)
        item {
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = DarkCardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Category Selector & Navigation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tabs
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // 0: Ayat
                            Surface(
                                onClick = { viewModel.switchInspirationTab(0) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (state.activeInspirationTab == 0) NeonGreen else DarkSurfaceVariant
                            ) {
                                Text(
                                    text = "📖 Ayat",
                                    color = if (state.activeInspirationTab == 0) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // 1: Hadees
                            Surface(
                                onClick = { viewModel.switchInspirationTab(1) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (state.activeInspirationTab == 1) NeonAmber else DarkSurfaceVariant
                            ) {
                                Text(
                                    text = "📜 Hadees",
                                    color = if (state.activeInspirationTab == 1) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // 2: Grind
                            Surface(
                                onClick = { viewModel.switchInspirationTab(2) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (state.activeInspirationTab == 2) NeonPink else DarkSurfaceVariant
                            ) {
                                Text(
                                    text = "🔥 Grind",
                                    color = if (state.activeInspirationTab == 2) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Next / Prev buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { viewModel.prevInspiration() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text(text = "❮", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(
                                onClick = { viewModel.nextInspiration() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text(text = "❯", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content Switcher
                    when (state.activeInspirationTab) {
                        0 -> {
                            // Ayat
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = state.currentAyat.arabic,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    lineHeight = 26.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = state.currentAyat.urdu,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "\"${state.currentAyat.english}\"",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Text(
                                    text = state.currentAyat.reference,
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                        1 -> {
                            // Hadees
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = state.currentHadees.arabic,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = state.currentHadees.urdu,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "\"${state.currentHadees.english}\"",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Text(
                                    text = state.currentHadees.reference,
                                    color = NeonAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                        else -> {
                            // Grind & Comeback Motivation
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "🔥 ${state.currentMotivation.title.uppercase()}",
                                    color = NeonPink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.currentMotivation.urdu,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 19.sp
                                )
                                Text(
                                    text = "\"${state.currentMotivation.english}\"",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Stay relentless, ${state.userName} ⚡",
                                        color = NeonPink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tap ❯ for next",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { viewModel.nextInspiration() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Next Upcoming Alarm Card
        item {
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAlarms() },
                backgroundColor = DarkSurface,
                borderColor = DarkCardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⏰", fontSize = 16.sp)
                            Text(
                                text = "NEXT ALARM",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Text(
                                text = state.nextAlarmCountdown,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.nextAlarm != null) {
                        val alarm = state.nextAlarm!!
                        val timeFormatted = DateTimeUtils.formatTime(alarm.hour, alarm.minute)
                        Text(
                            text = timeFormatted,
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = alarm.label,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "--:--",
                            color = TextMuted,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "No alarms scheduled",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 5. Daily Task Completion Ring
        item {
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTasks() },
                backgroundColor = DarkSurface,
                borderColor = DarkCardBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Progress
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(68.dp)
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
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )

                        Text(
                            text = "${state.completedPercentage}%",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY GOALS",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.completedTasks.size} of ${state.totalTasksCount} tasks completed",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Auto reminder every 6 hours",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "View Tasks",
                        tint = TextSecondary
                    )
                }
            }
        }

        // 6. Pending Tasks Preview Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S PRIORITIES",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToTasks() }
                )
            }
        }

        // 7. Pending Tasks List Items
        if (state.pendingTasks.isEmpty()) {
            item {
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface,
                    borderColor = DarkCardBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All clear! No pending tasks.",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(state.pendingTasks.take(3)) { task ->
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface,
                    borderColor = DarkCardBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(1.5.dp, DarkCardBorder, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = task.category,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        PriorityBadge(priority = task.priority)
                    }
                }
            }
        }
    }
}
