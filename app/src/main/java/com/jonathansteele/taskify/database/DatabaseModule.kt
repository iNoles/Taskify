package com.jonathansteele.taskify.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

// DatabaseModule for Room Database and DAO
object DatabaseModule {
    fun provideDatabase(context: Context): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "todo")
            // Uncomment and add migrations if needed
            // .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .addCallback(RoomCallback)
            .build()
}

object RoomCallback : RoomDatabase.Callback() {
    private val taskListDao: TaskListDao by inject(TaskListDao::class.java)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Use Coroutine to insert data
        CoroutineScope(Dispatchers.IO).launch {
            taskListDao.insert(TaskList(uid = 0, name = "Personal"))
            taskListDao.insert(TaskList(uid = 1, name = "Work"))
        }
    }
}