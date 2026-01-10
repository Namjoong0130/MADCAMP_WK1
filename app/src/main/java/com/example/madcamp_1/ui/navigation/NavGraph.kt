package com.example.madcamp_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madcamp_1.ui.screen.initial.InitScreen
import com.example.madcamp_1.ui.screen.login.LoginScreen // LoginScreen 파일이 있어야 함

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "init" // 시작 화면은 init
    ) {
        // 초기 화면 설정
        composable("init") {
            InitScreen(onTimeout = {
                // 3초 뒤에 실행될 로직: login 화면으로 이동
                // popUpTo("init") { inclusive = true } 를 붙이면 뒤로가기를 눌러도 다시 초기화면으로 안 옵니다.
                navController.navigate("login") {
                    popUpTo("init") { inclusive = true }
                }
            })
        }

        // 로그인 화면 설정
        composable("login") {
            LoginScreen() // LoginScreen.kt에 정의된 함수 호출
        }
    }
}
