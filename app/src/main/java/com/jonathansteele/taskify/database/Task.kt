package com.jonathansteele.taskify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jonathansteele.taskify.Priority

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    var name: String,
    var notes: String,
    var priority: Int = Priority.LOW.value,
    var completedDate: String = NOT_COMPLETED,
    var dueDate: String?,
    var hidden: Int,
    @ColumnInfo(index = true)
    var listId: Int,
) {
    companion object {
        const val NOT_COMPLETED = "0"
    }

    val isCompleted: Boolean
        get() = completedDate != NOT_COMPLETED

    fun withCompletion(isCompleted: Boolean): Task =
        this.copy(
            completedDate = if (isCompleted) System.currentTimeMillis().toString() else NOT_COMPLETED,
        )
}
