package com.jonathansteele.taskify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jonathansteele.taskify.screen.AddNoteScreen
import com.jonathansteele.taskify.screen.HomeScreen
import com.jonathansteele.taskify.screen.LoginScreen
import com.jonathansteele.taskify.screen.RegisterScreen

@Composable
fun AppNavigation() {
    // Initialize NavController
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddClick = { navController.navigate(Screen.AddNote.route) },
                onEditClick = { taskId -> navController.navigate(Screen.EditNote(taskId)) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    // TODO: Add ForgotPasswordScreen
                },
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSwitchToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Screen.EditNote.ROUTE_BASE,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            AddNoteScreen(taskId = taskId) {
                navController.popBackStack()
            }
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen {
                navController.popBackStack()
            }
        }
    }
}
