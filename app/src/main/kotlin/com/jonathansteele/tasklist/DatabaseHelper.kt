package com.jonathansteele.tasklist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * com.jonathansteele.tasklist.DatabaseHelper
 * This class act like a helper between application and sqldelight
 */
class DatabaseHelper(androidSqliteDriver: AndroidSqliteDriver) {
    private val database = Database(androidSqliteDriver)

    fun getAllPages() =
        database.listQueries
            .selectAllTasks()
            .executeAsList()

    fun getAllTasksBySpecificPageId(page: Int) =
        database.taskQueries.getAllTasksByListId(page.toLong()).asFlow()
            .mapToList(Dispatchers.IO)

    fun getTopTaskNames() =
        database.taskQueries
            .GetTop3TasksName()
            .executeAsList()

    suspend fun insertTask(
        name: String,
        notes: String,
        listId: Long,
        priority: Priority,
        id: Long? = null,
        hidden: Long,
    ) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = id,
                listId = listId,
                name = name,
                notes = notes,
                completedDate = "0",
                priority = priority.name,
                hidden = hidden,
            )
        }
    }

    suspend fun insertTask(
        task: Task,
        completedDate: String,
    ) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = task.id,
                listId = task.listId,
                name = task.name,
                notes = task.notes,
                completedDate = completedDate,
                priority = task.priority,
                hidden = task.hidden,
            )
        }
    }

    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            database.taskQueries.deleteTaskById(id)
        }
    }
}
