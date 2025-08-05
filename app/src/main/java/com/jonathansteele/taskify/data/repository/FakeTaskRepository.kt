package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.data.model.Priority
import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskList
import com.jonathansteele.taskify.data.model.TaskListName
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeTaskRepository : ITaskRepository {
    private val mutex = Mutex()

    private val taskLists =
        mutableListOf(
            TaskList(id = 1L, name = TaskListName.Personal),
            TaskList(id = 2L, name = TaskListName.Work),
            TaskList(id = 3L, name = TaskListName.Shared),
        )

    private val tasks =
        mutableListOf(
            Task(id = 1L, listId = 1L, name = "Call mom", notes = "Check in with mom", hidden = 0, priority = Priority.HIGH),
            Task(
                id = 2L,
                listId = 1L,
                name = "Read book",
                notes = "Finish reading Compose tutorial",
                hidden = 0,
                priority = Priority.MEDIUM,
            ),
            Task(id = 3L, listId = 2L, name = "Project meeting", notes = "Discuss project roadmap", hidden = 0, priority = Priority.HIGH),
            Task(id = 4L, listId = 3L, name = "Buy milk", notes = "2 liters of milk", hidden = 0, priority = Priority.MEDIUM),
        )

    override suspend fun getTasksList(): Result<List<TaskList>> = Result.success(taskLists.toList())

    override suspend fun getTasksByList(listName: TaskListName): Result<List<Task>> {
        val taskListId =
            taskLists.firstOrNull { it.name == listName }?.id
                ?: return Result.failure(Exception("Task list not found"))
        return Result.success(tasks.filter { it.listId == taskListId })
    }

    override suspend fun insertTask(task: Task): Result<Unit> {
        mutex.withLock {
            val newId = (tasks.maxOfOrNull { it.id } ?: 0L) + 1L
            tasks.add(task.copy(id = newId))
        }
        return Result.success(Unit)
    }

    override suspend fun getTaskById(taskId: Long): Result<Task> {
        val task = tasks.find { it.id == taskId }
        return if (task != null) Result.success(task) else Result.failure(Exception("Task not found"))
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        mutex.withLock {
            val index = tasks.indexOfFirst { it.id == task.id }
            return if (index != -1) {
                tasks[index] = task
                Result.success(Unit)
            } else {
                Result.failure(Exception("Task not found"))
            }
        }
    }

    override suspend fun deleteTask(id: Long): Result<Unit> {
        mutex.withLock {
            val removed = tasks.removeIf { it.id == id }
            return if (removed) Result.success(Unit) else Result.failure(Exception("Task not found"))
        }
    }
}
