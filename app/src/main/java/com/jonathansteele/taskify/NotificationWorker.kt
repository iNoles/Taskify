package com.jonathansteele.taskify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    override fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", -1)
        val taskName = inputData.getString("TASK_NAME") ?: return Result.failure()

        triggerNotification(taskId, taskName)

        return Result.success()
    }

    private fun triggerNotification(
        taskId: Int,
        taskName: String,
    ) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if needed
        createNotificationChannel(notificationManager)

        val notification =
            NotificationCompat
                .Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Task Due Soon")
                .setContentText("Task: $taskName is due soon!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        // Use taskId as unique notification id
        notificationManager.notify(taskId, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Task Notifications",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications for due tasks"
            }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "TASKS_CHANNEL"
    }
}

fun scheduleNotification(
    context: Context,
    taskId: Int,
    dueDate: String,
    taskName: String,
) {
    val currentTime = System.currentTimeMillis()
    val dueTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDate)?.time ?: return

    if (dueTime > currentTime) {
        val delay = dueTime - currentTime

        val workRequest =
            OneTimeWorkRequest
                .Builder(NotificationWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "TASK_ID" to taskId,
                        "TASK_NAME" to taskName,
                    ),
                ).build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "notify_task_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }
}
