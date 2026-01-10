package com.example.madcamp_1.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.info.InfoRoute
import com.example.madcamp_1.ui.screen.schedule.ScheduleRoute
import com.example.madcamp_1.ui.screen.infoselect.SelectRoute
import com.example.madcamp_1.ui.screen.write.WriteRoute // 추가

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 글쓰기(write) 화면에서는 하단 탭을 숨깁니다.
    val showBottomBar = currentRoute != "write"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "schedule",
                        label = { Text("스케줄") },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "schedule") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        label = { Text("게시판") },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "dashboard") }
                    )
                    val isInfoSelected = currentRoute == "select" || currentRoute?.startsWith("info") == true
                    NavigationBarItem(
                        selected = isInfoSelected,
                        label = { Text("경기정보") },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "select") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "schedule",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("schedule") { ScheduleRoute() }

            composable("dashboard") {
                DashboardRoute(onNavigateToWrite = { innerNavController.navigate("write") })
            }

            composable("select") {
                SelectRoute(navController = innerNavController)
            }

            composable(
                route = "info/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                InfoRoute(category = category, navController = innerNavController)
            }

            // 글쓰기 화면 추가
            composable("write") {
                WriteRoute(onBack = { innerNavController.popBackStack() })
            }
        }
    }
}

private fun navigateToTab(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}