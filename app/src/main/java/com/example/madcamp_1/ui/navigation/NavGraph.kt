package com.example.madcamp_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madcamp_1.ui.screen.MainScreen
import com.example.madcamp_1.ui.screen.initial.InitRoute
import com.example.madcamp_1.ui.screen.login.LoginRoute
import com.example.madcamp_1.ui.screen.register.RegisterRoute

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // 최상위 NavHost: 앱의 큰 상태(인증 전/후)를 전환합니다.
    NavHost(
        navController = navController,
        startDestination = "init" // 앱을 켜면 무조건 init부터 시작
    ) {
        // 1. 초기 스플래시 화면 (3초 대기 후 이동)
        composable("init") {
            InitRoute(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        // 뒤로가기 시 다시 init으로 오지 않도록 스택에서 제거
                        popUpTo("init") { inclusive = true }
                    }
                }
            )
        }

        // 2. 로그인 화면
        composable("login") {
            LoginRoute(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    // 로그인 성공 시 'main_screen'으로 이동하고 로그인 화면은 제거
                    navController.navigate("main_screen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 3. 회원가입 화면
        composable("register") {
            RegisterRoute(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 4. 메인 화면 (로그인 후의 모든 화면을 담는 컨테이너)
        composable("main_screen") {
            MainScreen()
        }
    }
}