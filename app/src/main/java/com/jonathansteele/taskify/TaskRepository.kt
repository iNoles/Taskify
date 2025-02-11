package com.jonathansteele.taskify

import com.jonathansteele.taskify.database.Task
import com.jonathansteele.taskify.database.TaskDao

class TaskRepository(
    val taskDao: TaskDao,
) {
    suspend fun getTaskById(uint: Int): Task = taskDao.getTaskById(uint)

    suspend fun getAllTasksByListId(listId: Int): List<Task> = taskDao.getAllTasksByListId(listId)

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}
