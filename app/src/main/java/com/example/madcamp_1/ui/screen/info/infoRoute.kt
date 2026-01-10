package com.example.madcamp_1.ui.screen.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // 'by' 사용을 위해 반드시 필요
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun InfoRoute(
    category: String,
    navController: NavController,
    viewModel: InfoViewModel = viewModel()
) {
    // 1. 카테고리가 바뀔 때마다 해당 데이터를 로드합니다.
    LaunchedEffect(category) {
        viewModel.loadDataByCategory(category)
    }

    // 2. ViewModel의 StateFlow들을 Composable 상태로 변환합니다.
    // 'by' 키워드를 사용하려면 상단의 getValue 임포트가 필수입니다.
    val videoId by viewModel.videoId.collectAsState()
    val location by viewModel.stadiumLocation.collectAsState()
    val stadiumName by viewModel.stadiumName.collectAsState()
    val records by viewModel.historyRecords.collectAsState()

    // 3. 수집된 데이터를 InfoScreen으로 전달합니다.
    InfoScreen(
        category = category,
        videoId = videoId,
        location = location,
        stadiumName = stadiumName,
        records = records,
        onBackClick = { navController.popBackStack() }
    )
}