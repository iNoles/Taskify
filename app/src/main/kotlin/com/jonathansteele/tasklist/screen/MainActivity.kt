package com.jonathansteele.tasklist.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.navigation.AppNavigation
import com.jonathansteele.tasklist.theme.TaskListTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val databaseHelper: DatabaseHelper by inject()
            TaskListTheme {
                AppNavigation(databaseHelper)
            }
        }
    }
}