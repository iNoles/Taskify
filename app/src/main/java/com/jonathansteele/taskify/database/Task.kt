package com.jonathansteele.taskify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jonathansteele.taskify.Priority

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    var name: String,
    var notes: String,
    var priority: Int = Priority.LOW.value,
    var completedDate: String = "0",
    var dueDate: String?,
    var hidden: Int,
    @ColumnInfo(index = true)
    var listId: Int
)
