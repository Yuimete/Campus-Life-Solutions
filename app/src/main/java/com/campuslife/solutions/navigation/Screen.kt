package com.campuslife.solutions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Reminders : Screen("reminders", "Reminders", Icons.Default.Notifications)
    object About : Screen("about", "About", Icons.Default.Info)
    object AddEditAssignment : Screen("add_edit?assignmentId={assignmentId}", "Assignment") {
        fun passId(id: Int? = null): String = "add_edit?assignmentId=$id"
    }
    object AssignmentDetail : Screen("detail/{assignmentId}", "Details") {
        fun passId(id: Int): String = "detail/$id"
    }
    object Splash : Screen("splash", "Splash")
}
