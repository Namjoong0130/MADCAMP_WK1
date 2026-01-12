package com.example.madcamp_1.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.info.InfoRoute
import com.example.madcamp_1.ui.screen.schedule.ScheduleRoute
import com.example.madcamp_1.ui.screen.infoselect.SelectRoute
import com.example.madcamp_1.ui.screen.write.WriteRoute
// 폰트가 정의된 테마 패키지를 임포트하세요
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "write"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    // --- 스케줄 탭 ---
                    NavigationBarItem(
                        selected = currentRoute == "schedule",
                        label = {
                            Text(
                                text = "스케줄",
                                fontFamily = UnivsFontFamily, // 폰트 직접 지정
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "schedule") }
                    )

                    // --- 게시판 탭 ---
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        label = {
                            Text(
                                text = "게시판",
                                fontFamily = UnivsFontFamily, // 폰트 직접 지정
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "dashboard") }
                    )

                    // --- 경기정보 탭 ---
                    val isInfoSelected = currentRoute == "select" || currentRoute?.startsWith("info") == true
                    NavigationBarItem(
                        selected = isInfoSelected,
                        label = {
                            Text(
                                text = "경기정보",
                                fontFamily = UnivsFontFamily, // 폰트 직접 지정
                                fontWeight = FontWeight.Bold
                            )
                        },
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