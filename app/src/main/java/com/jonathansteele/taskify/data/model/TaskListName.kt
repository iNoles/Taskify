package com.jonathansteele.taskify.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskListName {
    Personal,
    Work,
    Shared,
}
