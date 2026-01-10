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
import com.example.madcamp_1.ui.screen.infoselect.SelectRoute // SelectRoute 임포트

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                // 스케줄 탭
                NavigationBarItem(
                    selected = currentRoute == "schedule",
                    label = { Text("스케줄") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    onClick = { navigateToTab(innerNavController, "schedule") }
                )
                // 게시판 탭
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    label = { Text("게시판") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    onClick = { navigateToTab(innerNavController, "dashboard") }
                )
                // 경기정보 탭 (클릭 시 select로 이동)
                // 현재 route가 'select'이거나 'info/...'인 경우 모두 하이라이트 되도록 설정
                val isInfoSelected = currentRoute == "select" || currentRoute?.startsWith("info") == true
                NavigationBarItem(
                    selected = isInfoSelected,
                    label = { Text("경기정보") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    onClick = { navigateToTab(innerNavController, "select") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "schedule",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("schedule") {
                ScheduleRoute()
            }
            composable("dashboard") {
                DashboardRoute()
            }

            // [추가] 종목 선택 화면
            composable("select") {
                SelectRoute(navController = innerNavController)
            }

            // [수정] 상세 경기 정보 화면 ({category} 인자를 받음)
            composable(
                route = "info/{category}",
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                InfoRoute(
                    category = category,
                    navController = innerNavController
                )
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