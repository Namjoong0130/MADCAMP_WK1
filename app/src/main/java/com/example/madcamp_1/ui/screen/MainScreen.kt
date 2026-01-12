package com.example.madcamp_1.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.madcamp_1.ui.screen.article.ArticleRoute
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 글쓰기 및 상세 페이지에서는 하단 바를 숨김
    val showBottomBar = currentRoute != "write" && currentRoute?.startsWith("article") == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "schedule",
                        label = { Text("스케줄", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "schedule") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        label = { Text("게시판", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        onClick = { navigateToTab(innerNavController, "dashboard") }
                    )
                    val isInfoSelected = currentRoute == "select" || currentRoute?.startsWith("info") == true
                    NavigationBarItem(
                        selected = isInfoSelected,
                        label = { Text("경기정보", fontFamily = UnivsFontFamily, fontWeight = FontWeight.Bold) },
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
                DashboardRoute(
                    onNavigateToWrite = { innerNavController.navigate("write") },
                    onNavigateToArticle = { postId -> innerNavController.navigate("article/$postId") }
                )
            }

            composable("select") { SelectRoute(navController = innerNavController) }

            composable(
                route = "info/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                InfoRoute(category = category, navController = innerNavController)
            }

            composable("write") { WriteRoute(onBack = { innerNavController.popBackStack() }) }

            // 게시글 상세 페이지 라우팅
            composable(
                route = "article/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                ArticleRoute(postId = postId, onBack = { innerNavController.popBackStack() })
            }
        }
    }
}

private fun navigateToTab(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}