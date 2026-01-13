package com.example.madcamp_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madcamp_1.ui.screen.MainScreen
import com.example.madcamp_1.ui.screen.initial.InitRoute
import com.example.madcamp_1.ui.screen.login.LoginRoute
import com.example.madcamp_1.ui.screen.register.RegisterRoute

object Routes {
    const val INIT = "init"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.INIT
    ) {
        composable(Routes.INIT) {
            InitRoute(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INIT) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginRoute(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterRoute(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}
