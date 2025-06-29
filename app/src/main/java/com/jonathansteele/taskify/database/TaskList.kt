package com.jonathansteele.taskify.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
)
