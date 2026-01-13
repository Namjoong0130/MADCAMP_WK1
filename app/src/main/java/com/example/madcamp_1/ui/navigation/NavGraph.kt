package com.example.madcamp_1.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.madcamp_1.ui.screen.article.ArticleRoute
import com.example.madcamp_1.ui.screen.dashboard.DashboardRoute
import com.example.madcamp_1.ui.screen.write.WriteRoute

object Routes {
    const val DASHBOARD = "dashboard"
    const val WRITE = "write"
    const val ARTICLE = "article"
    const val ARTICLE_WITH_ID = "article/{postId}"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        composable(Routes.DASHBOARD) {
            DashboardRoute(
                onNavigateToWrite = { navController.navigate(Routes.WRITE) },
                onNavigateToArticle = { postId ->
                    val encoded = Uri.encode(postId)
                    navController.navigate("${Routes.ARTICLE}/$encoded")
                }
            )
        }

        composable(Routes.WRITE) {
            WriteRoute(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.ARTICLE_WITH_ID,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId").orEmpty()
            ArticleRoute(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
