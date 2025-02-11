package com.jonathansteele.taskify.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskListDao {
    @Query("SELECT uid, name FROM tasklist")
    suspend fun getAll(): List<TaskList>

    @Insert
    suspend fun insert(taskList: TaskList)
}
