package com.example.madcamp_1.ui.screen.infoselect

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SelectRoute(
    navController: NavController,
    viewModel: SelectViewModel = viewModel()
) {
    SelectScreen(
        categories = viewModel.categories,
        onCategoryClick = { category ->
            // 버튼 클릭 시 기존 info 경로로 카테고리명을 파라미터로 전달
            navController.navigate("info/$category")
        }
    )
}