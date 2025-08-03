package com.jonathansteele.taskify.data.model

@kotlinx.serialization.Serializable
data class Task(
    val id: Long = 0L,
    var name: String,
    var notes: String,
    var priority: Priority,
    var completedDate: String = NOT_COMPLETED,
    var hidden: Int,
    var listId: Long,
    var userId: String? = null,
    var sharedWith: List<String> = emptyList(),
) {
    companion object {
        const val NOT_COMPLETED = "0"
    }

    val isCompleted: Boolean
        get() = completedDate != NOT_COMPLETED

    fun withCompletion(isCompleted: Boolean): Task =
        copy(
            completedDate = if (isCompleted) System.currentTimeMillis().toString() else NOT_COMPLETED,
        )
}
