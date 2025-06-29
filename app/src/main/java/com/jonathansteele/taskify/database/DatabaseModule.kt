package com.jonathansteele.taskify.database

import android.content.Context
import androidx.room.Room

// DatabaseModule for Room Database and DAO
object DatabaseModule {
    fun provideDatabase(context: Context): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "todo")
            // Uncomment and add migrations if needed
            // .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration(false)
            .build()
}
