package com.jonathansteele.tasklist.navigation

import androidx.compose.runtime.Composable
import com.jonathansteele.tasklist.DatabaseHelper
import com.jonathansteele.tasklist.screen.AddNoteScreen
import com.jonathansteele.tasklist.screen.TaskListScreen
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun AppNavigation(databaseHelper: DatabaseHelper) {
    val navController =
        rememberNavController<Screen>(
            startDestination = Screen.Task,
        )

    NavBackHandler(navController)
    NavHost(navController) { screen ->
        when (screen) {
            is Screen.Task -> TaskListScreen(navController, databaseHelper)
            is Screen.Add ->
                AddNoteScreen(databaseHelper) {
                    navController.pop()
                }
        }
    }
}
