package com.jonathansteele.taskify.data.repository

import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskList
import com.jonathansteele.taskify.data.model.TaskListName
import com.jonathansteele.taskify.safeCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class TaskRepository(
    private val client: SupabaseClient,
) {
    suspend fun getTasksList() =
        safeCall {
            client.from("task_lists").select().decodeList<TaskList>()
        }

    suspend fun getTasksByList(listName: TaskListName) =
        safeCall {
            // Get the task list first
            val taskListId =
                client
                    .from("task_lists")
                    .select {
                        filter {
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

    suspend fun getTaskById(taskId: Long) =
        safeCall {
            client
                .from("tasks")
                .select {
                    filter { eq("id", taskId) }
                    limit(1)
                }.decodeSingleOrNull<Task>() ?: throw Exception("Task not found")
        }

    suspend fun updateTask(task: Task) =
        safeCall {
            client.from("tasks").update(task) {
                filter { eq("id", task.id) }
            }
            Unit
        }

    suspend fun deleteTask(id: Long) =
        safeCall {
            client.from("tasks").delete {
                filter { eq("id", id) }
            }
            Unit
        }
}
