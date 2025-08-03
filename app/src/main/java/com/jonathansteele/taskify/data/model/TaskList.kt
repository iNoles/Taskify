package com.jonathansteele.taskify.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskList(
    val id: Long = 0L,
    val name: TaskListName, // Personal, Work, Shared
)
