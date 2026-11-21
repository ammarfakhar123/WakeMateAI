package com.wakemate.ai.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*

@Composable
fun AddEditTaskDialog(
    taskToEdit: TaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        dueMillis: Long,
        priority: String,
        category: String,
        reminderInterval: Int,
        repeatOption: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: "MEDIUM") }
    var category by remember { mutableStateOf(taskToEdit?.category ?: "Study") }
    var reminderInterval by remember { mutableIntStateOf(taskToEdit?.reminderIntervalHours ?: 6) }
    var repeatOption by remember { mutableStateOf(taskToEdit?.repeatOption ?: "NONE") }
    var hoursDueFromNow by remember { mutableIntStateOf(6) }

    val categories = listOf("Study", "Work", "Health", "Personal", "General")
    val priorities = listOf("LOW", "MEDIUM", "HIGH")
    val intervals = listOf(1, 3, 6, 12, 24)
    val repeats = listOf("NONE", "DAILY", "WEEKLY")

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
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (taskToEdit == null) "Create New Task" else "Edit Task",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title (e.g. Study programming)") },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    maxLines = 3,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Category Chips
                Text(
                    text = "Category",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = category.equals(cat, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { category = cat },
                            color = if (isSelected) NeonPurple else DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Priority
                Text(
                    text = "Priority Level",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { p ->
                        val isSelected = priority == p
                        val color = when (p) {
                            "HIGH" -> NeonPink
                            "MEDIUM" -> NeonAmber
                            else -> NeonCyan
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) color else DarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { priority = p },
                            color = if (isSelected) color.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = p,
                                    color = if (isSelected) color else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Due in (Hours from now)
                Text(
                    text = "Due In: $hoursDueFromNow hours",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = hoursDueFromNow.toFloat(),
                    onValueChange = { hoursDueFromNow = it.toInt() },
                    valueRange = 1f..48f,
                    steps = 47,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = DarkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Reminder Interval
                Text(
                    text = "Reminder Interval",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(intervals) { interval ->
                        val isSelected = reminderInterval == interval
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { reminderInterval = interval },
                            color = if (isSelected) NeonCyan else DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Every ${interval}h",
                                color = if (isSelected) DarkBg else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Actions
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
                            val due = System.currentTimeMillis() + (hoursDueFromNow * 3600 * 1000L)
                            onSave(
                                title,
                                description,
                                due,
                                priority,
                                category,
                                reminderInterval,
                                repeatOption
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Task", color = DarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
