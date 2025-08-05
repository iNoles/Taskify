package com.jonathansteele.taskify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskListName
import com.jonathansteele.taskify.data.repository.IAuthRepository
import com.jonathansteele.taskify.data.repository.ITaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val iTaskRepository: ITaskRepository,
    private val iAuthRepository: IAuthRepository,
) : ViewModel() {
    private val _tasksByList = MutableStateFlow<Map<TaskListName, List<Task>>>(emptyMap())
    val tasksByList: StateFlow<Map<TaskListName, List<Task>>> = _tasksByList

    fun loadTasksFor(listName: TaskListName) {
        viewModelScope.launch {
            val result = iTaskRepository.getTasksByList(listName)
            result
                .onSuccess { tasks ->
                    val currentMap = _tasksByList.value
                    val currentTasks = currentMap[listName]

                    if (currentTasks != tasks) {
                        val updatedMap = currentMap.toMutableMap()
                        updatedMap[listName] = tasks
                        _tasksByList.value = updatedMap
                    }
                }.onFailure { throwable ->
                    Log.e("HomeViewModel", "Failed to load tasks for $listName", throwable)
                }
        }
    }

    fun onTaskChecked(
        task: Task,
        isChecked: Boolean,
    ) {
        if (task.isCompleted != isChecked) {
            viewModelScope.launch {
                iTaskRepository
                    .updateTask(task.withCompletion(isChecked))
                    .onSuccess {
                        TaskListName.fromId(task.listId)?.let { loadTasksFor(it) }
                            ?: Log.w("HomeViewModel", "Unknown listId ${task.listId} for task ${task.id}")
                    }.onFailure {
                        Log.e("HomeViewModel", "Failed to update task", it)
                    }
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val listEntry =
                _tasksByList.value.entries
                    .firstNotNullOfOrNull { (list, tasks) ->
                        tasks.find { it.id == taskId }?.let { list }
                    }

            iTaskRepository
                .deleteTask(taskId)
                .onSuccess {
                    listEntry?.let { loadTasksFor(it) }
                }.onFailure {
                    Log.e("HomeViewModel", "Failed to delete task", it)
                }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            iAuthRepository.signOut()
            onComplete()
        }
    }
}
