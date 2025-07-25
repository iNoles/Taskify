package com.jonathansteele.taskify.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskList(
    val id: Int = 0,
    val name: TaskListName, // Personal, Work, Shared
    val ownerId: String,
)
