package com.campuslife.solutions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campuslife.solutions.navigation.Screen
import com.campuslife.solutions.ui.screens.*
import com.campuslife.solutions.ui.theme.CampusLifeSolutionsTheme
import com.campuslife.solutions.ui.theme.Navy
import com.campuslife.solutions.ui.theme.Periwinkle
import com.campuslife.solutions.ui.theme.White
import com.campuslife.solutions.ui.theme.RoyalBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusLifeSolutionsTheme {
                RequestPermissions()
                MainScreen()
            }
        }
    }
}

@Composable
fun RequestPermissions() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Calendar,
        Screen.Reminders,
        Screen.About
    )

    val showBottomBar = currentDestination?.route in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Navy,
                    contentColor = White
                ) {
                    bottomBarScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = White,
                                selectedTextColor = White,
                                unselectedIconColor = Periwinkle.copy(alpha = 0.6f),
                                unselectedTextColor = Periwinkle.copy(alpha = 0.6f),
                                indicatorColor = RoyalBlue
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddAssignment = { navController.navigate(Screen.AddEditAssignment.passId()) },
                    onAssignmentClick = { id -> navController.navigate(Screen.AssignmentDetail.passId(id)) }
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onAssignmentClick = { id -> navController.navigate(Screen.AssignmentDetail.passId(id)) }
                )
            }
            composable(Screen.Reminders.route) {
                RemindersScreen()
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
            composable(
                route = Screen.AddEditAssignment.route,
                arguments = listOf(navArgument("assignmentId") { 
                    type = NavType.StringType
                    nullable = true 
                    defaultValue = null
                })
            ) { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("assignmentId")
                val id = idString?.toIntOrNull()
                AddEditAssignmentScreen(
                    assignmentId = id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AssignmentDetail.route,
                arguments = listOf(navArgument("assignmentId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("assignmentId") ?: 0
                AssignmentDetailScreen(
                    assignmentId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Screen.AddEditAssignment.passId(id)) }
                )
            }
        }
    }
}
