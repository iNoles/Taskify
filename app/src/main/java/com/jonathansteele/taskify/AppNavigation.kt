package com.jonathansteele.taskify

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    // Initialize NavController
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }
        composable("edit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            AddNoteScreen(taskId = taskId) {
                navController.popBackStack()
            }
        }
        composable("add") {
            AddNoteScreen {
                navController.popBackStack()
            }
        }
    }
}