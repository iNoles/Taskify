package com.jonathansteele.taskify

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jonathansteele.taskify.database.AppDatabase
import com.jonathansteele.taskify.database.Task
import com.jonathansteele.taskify.database.TaskDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var taskDao: TaskDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertTest() =
        runTest {
            var numItemsBeforeInsertion = taskDao.getAllTasksByListId(0).size

            taskDao.insertTask(Task(
                uid = 0,
                name = "Test",
                notes = "This is test",
                listId = 0,
                priority = Priority.LOW.value,
                dueDate = "",
                hidden = 0,
            ))

            taskDao.insertTask(Task(
                uid = 1,
                name = "Interview",
                notes = "Preparing for Interview",
                listId = 0,
                priority = Priority.HIGH.value,
                dueDate = "",
                hidden = 0,
            ))

            var numItemsAfterInsertion = taskDao.getAllTasksByListId(0).size

            assertEquals(0, numItemsBeforeInsertion)
            assertEquals(2, numItemsAfterInsertion)
        }
}
