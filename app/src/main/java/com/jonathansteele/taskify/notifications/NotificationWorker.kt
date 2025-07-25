package com.jonathansteele.taskify.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jonathansteele.taskify.R

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    override fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", 0)
        val taskName = inputData.getString("TASK_NAME") ?: return Result.failure()

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "TASK_NOTIFY"

        // Create channel once
        if (manager.getNotificationChannel(channelId) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }

        val notification =
            NotificationCompat
                .Builder(applicationContext, channelId)
                .setContentTitle("Reminder")
                .setContentText("Task due: $taskName")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        manager.notify(taskId, notification)
        return Result.success()
    }
}
