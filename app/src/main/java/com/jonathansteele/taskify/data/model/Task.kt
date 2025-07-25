package com.jonathansteele.taskify.data.model

@kotlinx.serialization.Serializable
data class Task(
    val id: Int = 0,
    var name: String,
    var notes: String,
    var priority: Int,
    var completedDate: String = NOT_COMPLETED,
    var dueDate: String? = null,
    var hidden: Int,
    var listId: Int,
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
