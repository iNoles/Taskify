package com.jonathansteele.taskify.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT * 
        FROM Task
        WHERE uid = :uid AND hidden = 0
    """,
    )
    suspend fun getTaskById(uid: Int): Task

    @Query(
        """
        SELECT * 
        FROM Task
        WHERE listId = :listId AND hidden = 0
        ORDER BY priority ASC, 
            dueDate IS NOT NULL DESC,
            dueDate ASC
    """,
    )
    fun getAllTasksByListIdFlow(listId: Int): Flow<List<Task>>

    @Query(
        """
        SELECT name
        FROM Task
        WHERE completedDate = '0'
        ORDER BY priority ASC, 
            dueDate IS NOT NULL DESC,
            dueDate ASC
        LIMIT 3
    """,
    )
    suspend fun getTop3TasksName(): List<String>

    @Insert
    suspend fun insertTask(task: Task)

    @Query("DELETE FROM Task WHERE uid = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Update(onConflict = REPLACE)
    suspend fun updateTask(task: Task)
}
