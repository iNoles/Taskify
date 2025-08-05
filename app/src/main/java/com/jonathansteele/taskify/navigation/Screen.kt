package com.jonathansteele.taskify.navigation

sealed class Screen(
    val route: String,
) {
    object Login : Screen("login")

    object Register : Screen("register")

    object Home : Screen("home")

    object AddNote : Screen("add")

    data class EditNote(
        val taskId: Long,
    ) : Screen("edit/$taskId") {
        companion object {
            const val ROUTE_BASE = "edit/{taskId}"
        }
    }
}
