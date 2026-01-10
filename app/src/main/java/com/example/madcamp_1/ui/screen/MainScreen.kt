package com.example.madcamp_1.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.madcamp_1.ui.screen.schedule.ScheduleRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.info.InfoRoute

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController() // 내부 탭 이동 전용

    Scaffold(
        bottomBar = {
            val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "schedule",
                    onClick = { innerNavController.navigate("schedule") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    label = { Text("스케줄") }
                )
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = { innerNavController.navigate("dashboard") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("게시판") }
                )
                NavigationBarItem(
                    selected = currentRoute == "info",
                    onClick = { innerNavController.navigate("info") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("경기정보") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = innerNavController,
            startDestination = "schedule",
            modifier = Modifier.padding(padding)
        ) {
            composable("schedule") { ScheduleRoute() }
            composable("dashboard") { DashboardRoute() }
            composable("info") { InfoRoute() }
        }
    }
}