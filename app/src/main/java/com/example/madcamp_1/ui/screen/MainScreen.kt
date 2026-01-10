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
import androidx.navigation.compose.*
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.info.InfoRoute
import com.example.madcamp_1.ui.screen.schedule.ScheduleRoute

@Composable
fun MainScreen() {
    // 탭 이동을 관리할 내부 전용 조종사
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 현재 어떤 탭에 있는지 감시하여 하단 바의 선택 상태를 업데이트
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
                // 경기정보 탭
                NavigationBarItem(
                    selected = currentRoute == "info",
                    label = { Text("경기정보") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    onClick = { navigateToTab(innerNavController, "info") }
                )
            }
        }
    ) { innerPadding ->
        // 하단 바를 제외한 나머지 영역(innerPadding)에 화면을 갈아 끼웁니다.
        NavHost(
            navController = innerNavController,
            startDestination = "schedule", // 로그인 후 첫 탭은 스케줄
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("schedule") {
                ScheduleRoute()
            }
            composable("dashboard") {
                DashboardRoute()
            }
            composable("info") {
                // onNavigateToDetail은 일단 빈 함수로 넘겨 빨간 줄 방지
                InfoRoute(onNavigateToDetail = {})
            }
        }
    }
}

/**
 * 하단 탭 이동 시 상태 보존을 위한 헬퍼 함수
 */
private fun navigateToTab(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        // 탭 전환 시 스택이 무한히 쌓이지 않도록 시작 지점 위를 다 비움
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // 같은 탭을 여러 번 눌러도 새로 생성하지 않음
        launchSingleTop = true
        // 이전에 보던 상태(스크롤 위치 등)를 복구함
        restoreState = true
    }
}