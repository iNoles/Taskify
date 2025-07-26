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
        startDestination = "login",
    ) {
        composable("home") {
            HomeScreen(
                onAddClick = { navController.navigate("add") },
                onEditClick = { taskId -> navController.navigate("edit/$taskId") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSwitchToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
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
