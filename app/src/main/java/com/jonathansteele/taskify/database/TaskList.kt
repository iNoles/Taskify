package com.jonathansteele.taskify.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    @PrimaryKey val uid: Int,
    val name: String,
)
