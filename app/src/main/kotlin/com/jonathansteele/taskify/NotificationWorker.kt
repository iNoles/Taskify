package com.jonathansteele.taskify

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
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
        val taskName = inputData.getString("TASK_NAME") ?: return Result.failure()

        // Trigger local notification here
        triggerNotification(taskName)

        return Result.success()
    }

    private fun triggerNotification(taskName: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification =
            NotificationCompat
                .Builder(applicationContext, "TASKS_CHANNEL")
                .setContentTitle("Task Due Soon")
                .setContentText("Task: $taskName is due soon!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        notificationManager.notify(1, notification)
    }
}

fun scheduleNotification(
    context: Context,
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
                .setInputData(workDataOf("TASK_NAME" to taskName))
                .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
