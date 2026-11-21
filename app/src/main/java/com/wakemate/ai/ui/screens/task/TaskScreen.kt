package com.wakemate.ai.ui.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.components.PriorityBadge
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

    val categories = listOf("ALL", "Study", "Work", "Health", "Personal")

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "TASK MANAGER",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "To-Do & Reminders",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Status Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(TaskFilter.ALL, TaskFilter.PENDING, TaskFilter.COMPLETED).forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setFilter(filter) },
                            color = if (isSelected) NeonCyan else DarkSurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = filter.name,
                                    color = if (isSelected) DarkBg else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Surface(
                            modifier = Modifier.clickable { viewModel.setCategory(cat) },
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
            }

            // Tasks List
            if (tasks.isEmpty()) {
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
                                    imageVector = Icons.Filled.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Tasks Found",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap '+' to create a new task with automatic reminders.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            } else {
                items(tasks) { task ->
                    TaskItemCard(
                        task = task,
                        onToggle = { viewModel.toggleTaskComplete(task) },
                        onEdit = { taskToEdit = task },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        // FAB to add task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NeonCyan,
            contentColor = DarkBg,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task", modifier = Modifier.size(28.dp))
        }
    }

    if (showAddDialog || taskToEdit != null) {
        AddEditTaskDialog(
            taskToEdit = taskToEdit,
            onDismiss = {
                showAddDialog = false
                taskToEdit = null
            },
            onSave = { title, desc, due, priority, cat, interval, repeat ->
                if (taskToEdit == null) {
                    viewModel.addTask(title, desc, due, priority, cat, interval, repeat)
                } else {
                    viewModel.updateTask(
                        taskToEdit!!.copy(
                            title = title,
                            description = desc,
                            dueDateTimeMillis = due,
                            priority = priority,
                            category = cat,
                            reminderIntervalHours = interval,
                            repeatOption = repeat
                        )
                    )
                }
                showAddDialog = false
                taskToEdit = null
            }
        )
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        cornerRadius = 16.dp,
        glowAccent = if (!task.isCompleted && task.priority == "HIGH") NeonPink else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
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
                    color = if (task.isCompleted) TextMuted else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = task.category,
                            color = NeonPurple,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = DateTimeUtils.formatDateTime(task.dueDateTimeMillis),
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Every ${task.reminderIntervalHours}h",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                PriorityBadge(priority = task.priority)
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
