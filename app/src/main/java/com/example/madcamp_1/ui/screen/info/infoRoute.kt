package com.example.madcamp_1.ui.screen.info

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun InfoRoute(
    // ★ 이 부분을 추가해야 MainScreen의 빨간 줄이 사라집니다!
    onNavigateToDetail: (String) -> Unit,
    viewModel: InfoViewModel = viewModel()
) {
    val videoId by viewModel.videoId.collectAsState()
    val location by viewModel.stadiumLocation.collectAsState()
    val stadiumName by viewModel.stadiumName.collectAsState()

    InfoScreen(
        videoId = videoId,
        location = location,
        stadiumName = stadiumName
    )
}