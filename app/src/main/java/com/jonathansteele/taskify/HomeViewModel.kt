package com.jonathansteele.taskify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathansteele.taskify.data.model.Task
import com.jonathansteele.taskify.data.model.TaskListName
import com.jonathansteele.taskify.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _tasksByList = MutableStateFlow<Map<TaskListName, List<Task>>>(emptyMap())
    val tasksByList: StateFlow<Map<TaskListName, List<Task>>> = _tasksByList

    fun loadTasksFor(listName: TaskListName) {
        viewModelScope.launch {
            val result = taskRepository.getTasksByList(listName)
            val updatedMap = _tasksByList.value.toMutableMap()
            updatedMap[listName] = result.getOrElse { emptyList() }
            _tasksByList.value = updatedMap
        }
    }

    fun onTaskChecked(
        task: Task,
        isChecked: Boolean,
    ) {
        viewModelScope.launch {
            taskRepository
                .updateTask(task.withCompletion(isChecked))
                .onFailure {
                    Log.e("HomeViewModel", "Failed to update task", it)
                }
        }
    }
}
