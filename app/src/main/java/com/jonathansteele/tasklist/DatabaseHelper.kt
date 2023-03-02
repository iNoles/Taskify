package com.jonathansteele.tasklist

import android.content.Context
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.Task
import com.jonathansteele.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * DataHelper
 * This class act like a helper between application and sql-delight
 */
class DatabaseHelper(context: Context) {
    private val driver = AndroidSqliteDriver(Database.Schema, context, "notes.db")
    private val database = Database(driver)

    fun getAllPages(): List<TaskList> =
        database.listQueries.selectAllTasks().executeAsList()

    fun getAllTasksBySpecificPageId(page: Int): Flow<List<Task>> =
        database.taskQueries.getallTasksByListId(page.toLong()).asFlow().mapToList(Dispatchers.IO)

    suspend fun insertTask(name: String, notes: String, listId: Long, id: Long?) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = id,
                listId = listId,
                name = name,
                notes = notes,
                completedDate = "0",
                hidden = 0L
            )
        }
    }

    suspend fun insertTask(task: Task, completedDate: String) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = task.id,
                listId = task.listId,
                name = task.name,
                notes = task.notes,
                completedDate = completedDate,
                hidden = 0L
            )
        }
    }

    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            database.taskQueries.deleteTaskById(id)
        }
    }
}