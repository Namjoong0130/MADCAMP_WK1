package com.example.madcamp_1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardViewModel
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
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val schoolId = AuthManager.getSchoolId()
    val isPostech = schoolId.contains("postech", ignoreCase = true)

    val brandColor = if (isPostech) Color(0xFFE0224E) else Color(0xFF005EB8)
    val brandPastel = if (isPostech) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)

    // 시인성을 위해 선택되지 않은 상태의 색상을 훨씬 진한 회색으로 설정
    val unselectedColor = Color(0xFF424242)

    val dashboardViewModel: DashboardViewModel = viewModel()
    val showBottomBar = currentRoute != "write" && currentRoute?.startsWith("article") == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // NavigationBar 자체의 패딩을 제거하여 내부 아이템이 잘리지 않게 함
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp),
                    windowInsets = WindowInsets(0.dp)
                ) {
                    val navItems = listOf(
                        Triple("schedule", "스케줄", Icons.Default.DateRange),
                        Triple("dashboard", "게시판", Icons.Default.List),
                        Triple("select", "경기정보", Icons.Default.Info)
                    )

                    navItems.forEach { (route, label, icon) ->
                        val isSelected = if (route == "select") {
                            currentRoute == "select" || currentRoute?.startsWith("info") == true
                        } else {
                            currentDestination?.hierarchy?.any { it.route == route } == true
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateToTab(innerNavController, route) },
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (isSelected) brandColor else unselectedColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontFamily = UnivsFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) brandColor else unselectedColor
                                    )
                                }
                            },
                            // label 파라미터를 비우고 icon 파라미터 안에 Column으로 합쳐서
                            // 기본 NavigationBarItem의 높이 계산 오류(잘림 현상)를 방지합니다.
                            label = null,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = brandPastel
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(0.dp))
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "schedule",
            modifier = Modifier.padding(top = 20.dp, // 상단 패딩 강제 제거
                bottom = innerPadding.calculateBottomPadding(), // 하단바 높이만큼만 유지
                start = 0.dp,
                end = 0.dp)
        ) {
            composable("schedule") { ScheduleRoute() }
            composable("dashboard") {
                DashboardRoute(
                    viewModel = dashboardViewModel,
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
            composable("write") {
                WriteRoute(
                    dashboardViewModel = dashboardViewModel,
                    onBack = { innerNavController.popBackStack() }
                )
            }
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