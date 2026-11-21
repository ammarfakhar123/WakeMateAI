package com.wakemate.ai.data.repository

import com.wakemate.ai.data.local.dao.TaskDao
import com.wakemate.ai.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    val completedTasks: Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    val pendingCount: Flow<Int> = taskDao.getPendingCount()
    val completedCount: Flow<Int> = taskDao.getCompletedCount()

    suspend fun getPendingTasksList(): List<TaskEntity> = taskDao.getPendingTasksList()

    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(id, isCompleted, completedAt)
    }
}
