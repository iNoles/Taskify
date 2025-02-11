package com.jonathansteele.taskify

import com.jonathansteele.taskify.database.AppDatabase
import com.jonathansteele.taskify.database.DatabaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single { DatabaseModule.provideDatabase(androidContext()) }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().taskDao() }
    single { TaskRepository(get()) }
}