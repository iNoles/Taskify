package com.jonathansteele.tasklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.jonathansteele.tasklist.ui.theme.TaskListTheme
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskListTheme {
                NavHostScreen()
            }
        }
    }
}

@Composable
fun NavHostScreen() {
    val navController = rememberNavController<Screen>(
        startDestination = Screen.Task
    )

    NavBackHandler(navController)
    NavHost(navController) { screen ->
        when (screen) {
            is Screen.Task -> TaskListScreen(navController)
            is Screen.Add -> AddNoteScreen(screen) {
                navController.pop()
            }
        }
    }
}
