package com.example.madcamp_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madcamp_1.ui.screen.MainScreen
import com.example.madcamp_1.ui.screen.initial.InitRoute
import com.example.madcamp_1.ui.screen.login.LoginRoute
import com.example.madcamp_1.ui.screen.register.RegisterRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.info.InfoRoute

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    @Composable
    fun NavGraph() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "init"
        ) {
            composable("init") {
                InitRoute(onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("init") { inclusive = true } }
                })
            }

            composable("login") {
                LoginRoute(
                    onNavigateToRegister = { navController.navigate("register") },
                    onLoginSuccess = {
                        navController.navigate("main_screen") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterRoute(onBackToLogin = { navController.popBackStack() })
            }

            // ★ 중요: 로그인 이후의 화면은 오직 이 하나로 통합니다.
            // 이 안에서 schedule, dashboard, info가 하단 탭과 함께 돌아갑니다.
            composable("main_screen") {
                MainScreen()
            }

            // 아래 dashboard와 info는 삭제해야 합니다!
            // 여기에 있으면 하단 탭 바(MainScreen의 Scaffold) 밖으로 나가버립니다.
        }
    }
}