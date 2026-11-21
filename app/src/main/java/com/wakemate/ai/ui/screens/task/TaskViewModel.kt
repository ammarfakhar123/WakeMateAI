package com.wakemate.ai.ui.screens.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakemate.ai.WakeMateApp
import com.wakemate.ai.data.local.entity.TaskEntity
import com.wakemate.ai.worker.TaskReminderWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WakeMateApp
    private val taskRepo = app.taskRepository
    private val prefsRepo = app.userPreferencesRepository

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        taskRepo.allTasks,
        _selectedFilter,
        _selectedCategory
    ) { tasks, filter, category ->
        tasks.filter { task ->
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }
            val matchesCategory = if (category == "ALL") true else task.category.equals(category, ignoreCase = true)
            matchesFilter && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addTask(
        title: String,
        description: String,
        dueDateTimeMillis: Long,
        priority: String,
        category: String,
        reminderIntervalHours: Int,
        repeatOption: String
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.ifBlank { "New Task" },
                description = description,
                dueDateTimeMillis = dueDateTimeMillis,
                priority = priority,
                category = category,
                reminderIntervalHours = reminderIntervalHours,
                repeatOption = repeatOption
            )
            taskRepo.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepo.updateTask(task)
        }
    }

    fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch {
            taskRepo.setTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepo.deleteTask(task)
        }
    }
}
