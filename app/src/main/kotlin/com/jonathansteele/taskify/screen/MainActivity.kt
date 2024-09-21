package com.jonathansteele.taskify.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jonathansteele.taskify.DatabaseHelper
import com.jonathansteele.taskify.navigation.AppNavigation
import com.jonathansteele.taskify.theme.TaskListTheme
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
