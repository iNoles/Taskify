package com.jonathansteele.tasklist.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.tasklist.DatabaseHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sqliteDriverModule =
    module {
        single {
            AndroidSqliteDriver(Database.Schema, androidContext(), "notes.db")
        }
    }

val dataModule =
    module {
        single {
            DatabaseHelper(get())
        }
    }

val appModules =
    listOf(
        sqliteDriverModule,
        dataModule,
    )
