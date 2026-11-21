package com.wakemate.ai.worker

import android.content.Context
import androidx.work.*
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.util.NotificationHelper
import java.util.concurrent.TimeUnit

class TaskReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val UNIQUE_WORK_NAME = "WakeMateTaskReminderWorker"

        fun schedulePeriodicReminder(context: Context, intervalHours: Int = 6) {
            val validIntervalHours = if (intervalHours < 1) 1 else intervalHours
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(
                validIntervalHours.toLong(),
                TimeUnit.HOURS,
                15, // 15-minute flex interval
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancelPeriodicReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        val database = WakeMateDatabase.getDatabase(context)
        val pendingTasks = database.taskDao().getPendingTasksList()

        if (pendingTasks.isNotEmpty()) {
            // Find highest priority or most pressing pending task
            val sortedPending = pendingTasks.sortedWith(
                compareBy<com.wakemate.ai.data.local.entity.TaskEntity> {
                    when (it.priority) {
                        "HIGH" -> 0
                        "MEDIUM" -> 1
                        else -> 2
                    }
                }.thenBy { it.dueDateTimeMillis }
            )

            val targetTask = sortedPending.first()
            NotificationHelper.showTaskReminderNotification(
                context = context,
                taskId = targetTask.id,
                taskTitle = targetTask.title,
                priority = targetTask.priority
            )
        }

        return Result.success()
    }
}
