package com.jonathansteele.taskify

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    // Initialize NavController
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(
                onAddClick = { navController.navigate("add") },
                onEditClick = { taskId -> navController.navigate("edit/$taskId") },
            )
        }

        composable(
            route = "edit/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType }),
        ) { backStackEntry ->
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
