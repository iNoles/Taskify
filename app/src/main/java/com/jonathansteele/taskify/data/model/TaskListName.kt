package com.jonathansteele.taskify.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskListName(
    val id: Long,
) {
    Personal(1L),
    Work(2L),
    Shared(3L),
    ;

    companion object {
        fun fromId(id: Long): TaskListName? = entries.find { it.id == id }
    }
}
