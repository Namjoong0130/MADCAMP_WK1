package com.example.madcamp_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madcamp_1.ui.screen.initial.InitRoute
import com.example.madcamp_1.ui.screen.login.LoginRoute
import com.example.madcamp_1.ui.screen.register.RegisterRoute
//import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "init"
    ) {
        // 1. 초기 화면 (3초 대기)
        composable("init") {
            InitRoute(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("init") { inclusive = true }
                    }
                }
            )
        }

        // 2. 로그인 화면
        composable("login") {
            LoginRoute(
                // 회원가입 버튼 클릭 시 'register' 주소로 이동
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 3. 회원가입 화면 (새로 추가/구체화된 부분)
        composable("register") {
            RegisterRoute(
                onBackToLogin = {
                    // '돌아가기' 혹은 '가입 완료' 시 현재 화면을 닫고 이전(로그인)으로 복귀
                    navController.popBackStack()
                }
            )
        }

        // 4. 메인 대시보드
        /*composable("dashboard") {
            DashboardRoute()
        }*/
    }
}