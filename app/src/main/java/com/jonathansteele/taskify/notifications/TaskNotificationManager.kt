package com.jonathansteele.taskify.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object TaskNotificationManager {
    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm"

    fun schedule(
        context: Context,
        taskId: Long,
        taskName: String,
        dueDate: String,
    ) {
        val dueTime = parseDueTime(dueDate) ?: return
        val delay = dueTime - System.currentTimeMillis()

        if (delay <= 0) return // Skip past-due tasks

        val inputData =
            workDataOf(
                "TASK_ID" to taskId,
                "TASK_NAME" to taskName,
            )

        val workRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            getWorkName(taskId),
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    fun cancel(
        context: Context,
        taskId: Long,
    ) {
        WorkManager.getInstance(context).cancelUniqueWork(getWorkName(taskId))
    }

    private fun getWorkName(taskId: Long) = "notify_task_$taskId"

    private fun parseDueTime(dueDate: String): Long? =
        try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.parse(dueDate)?.time
        } catch (e: Exception) {
            null
        }
}
