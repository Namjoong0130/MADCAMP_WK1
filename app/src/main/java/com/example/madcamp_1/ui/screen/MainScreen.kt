package com.example.madcamp_1.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
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
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.madcamp_1.data.utils.AuthManager
import com.example.madcamp_1.ui.screen.article.ArticleRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardViewModel
import com.example.madcamp_1.ui.screen.info.InfoRoute
import com.example.madcamp_1.ui.screen.infoselect.SelectRoute
import com.example.madcamp_1.ui.screen.schedule.ScheduleRoute
import com.example.madcamp_1.ui.screen.write.WriteRoute
import com.example.madcamp_1.ui.theme.UnivsFontFamily

@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val schoolId = AuthManager.getSchoolId()
    val isPostech = schoolId.contains("postech", ignoreCase = true)

    val brandColor = if (isPostech) Color(0xFFE0224E) else Color(0xFF005EB8)
    val brandPastel = if (isPostech) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
    val unselectedColor = Color(0xFF424242)

    // 탭/글쓰기/상세에서 같은 VM을 공유하기 위해 MainScreen에서 1회 생성
    val dashboardViewModel: DashboardViewModel = viewModel()

    // write, article에서는 bottomBar 숨김
    val showBottomBar = currentRoute != "write" && (currentRoute?.startsWith("article") != true)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
                            currentRoute == route
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
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontFamily = UnivsFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) brandColor else unselectedColor
                                    )
                                }
                            },
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("schedule") { ScheduleRoute() }

            composable("dashboard") {
                DashboardRoute(
                    onNavigateToWrite = { innerNavController.navigate("write") },
                    onNavigateToArticle = { postId ->
                        val encoded = Uri.encode(postId)
                        innerNavController.navigate("article/$encoded")
                    },
                    dashboardViewModel = dashboardViewModel
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
                    onBack = { innerNavController.popBackStack() },
                    dashboardViewModel = dashboardViewModel
                )
            }

            composable(
                route = "article/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId").orEmpty()
                ArticleRoute(
                    postId = postId,
                    onBack = { innerNavController.popBackStack() }
                )
            }
        }
    }
}

private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
