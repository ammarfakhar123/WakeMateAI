package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.data.local.WakeMateDatabase
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MARK_TASK_COMPLETE = "com.wakemate.ai.ACTION_MARK_TASK_COMPLETE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_MARK_TASK_COMPLETE) {
            val taskId = intent.getLongExtra("EXTRA_TASK_ID", -1L)
            if (taskId != -1L) {
                // Cancel notification
                NotificationHelper.cancelNotification(
                    context,
                    (NotificationHelper.NOTIFICATION_ID_TASK_BASE + taskId).toInt()
                )

                val database = WakeMateDatabase.getDatabase(context)
                CoroutineScope(Dispatchers.IO).launch {
                    val task = database.taskDao().getTaskById(taskId)
                    if (task != null) {
                        database.taskDao().setTaskCompleted(taskId, true, System.currentTimeMillis())
                        database.chatMessageDao().insertMessage(
                            ChatMessageEntity(
                                sender = "ASSISTANT",
                                text = "🎉 Great job! You marked '${task.title}' as completed directly from notifications. Keep up the momentum!",
                                quickReplies = listOf("Show pending tasks 📋", "Add new task ➕")
                            )
                        )
                    }
                }
            }
        }
    }
}
