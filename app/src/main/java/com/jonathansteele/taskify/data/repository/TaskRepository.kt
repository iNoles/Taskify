package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskList
import com.jonathansteele.taskify.data.model.TaskListName
import com.jonathansteele.taskify.safeCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class TaskRepository(
    private val client: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    /*suspend fun insertTaskList(taskList: TaskList): Result<Unit> = safeCall {
        client.from("task_lists").insert(taskList)
        Unit
    }*/

    suspend fun getTasksList() =
        safeCall {
            val userId = authRepository.currentUserId() ?: throw Exception("Not signed in")
            client
                .from("task_lists")
                .select {
                    filter {
                        eq("owner_id", userId)
                    }
                }.decodeList<TaskList>()
        }

    suspend fun getTasksByList(listName: TaskListName) =
        safeCall {
            val userId = authRepository.currentUserId() ?: throw Exception("Not signed in")

            // Get the task list first
            val taskListId =
                client
                    .from("task_lists")
                    .select {
                        filter {
                            eq("owner_id", userId)
                            eq("name", listName.name)
                        }
                        limit(1)
                    }.decodeSingleOrNull<TaskList>()
                    ?.id ?: throw Exception("Task list not found")

            // Fetch tasks in that list
            client
                .from("tasks")
                .select {
                    filter { eq("list_id", taskListId) }
                }.decodeList<Task>()
        }

    suspend fun insertTask(task: Task) =
        safeCall {
            client.from("tasks").insert(task)
            Unit
        }

    suspend fun getTaskById(taskId: Int) =
        safeCall {
            client
                .from("tasks")
                .select {
                    filter { eq("id", taskId) }
                    limit(1)
                }.decodeSingleOrNull<Task>() ?: throw Exception("Task not found")
        }

    /*suspend fun getTasks(): Result<List<Task>> = safeCall {
        val userId = authRepository.currentUserId() ?: throw Exception("Not signed in")
        client.from("tasks")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Task>()
    }*/

    suspend fun updateTask(task: Task) =
        safeCall {
            client.from("tasks").update(task) {
                filter { eq("id", task.id) }
            }
            Unit
        }

    suspend fun deleteTask(id: Int) =
        safeCall {
            client.from("tasks").delete {
                filter { eq("id", id) }
            }
            Unit
        }
}
