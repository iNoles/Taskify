package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskList
import com.jonathansteele.taskify.data.model.TaskListName

interface ITaskRepository {
    suspend fun getTasksList(): Result<List<TaskList>>

    suspend fun getTasksByList(listName: TaskListName): Result<List<Task>>

    suspend fun insertTask(task: Task): Result<Unit>

    suspend fun getTaskById(taskId: Long): Result<Task>

    suspend fun updateTask(task: Task): Result<Unit>

    suspend fun deleteTask(id: Long): Result<Unit>
}
