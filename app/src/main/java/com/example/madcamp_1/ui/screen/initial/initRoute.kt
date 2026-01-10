package com.example.madcamp_1.ui.screen.initial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun InitRoute(
    onNavigateToLogin: () -> Unit,
    viewModel: InitViewModel = viewModel() // Compose가 ViewModel을 자동 생성
) {
    // ViewModel의 상태를 관찰 (isTimeout이 true가 되는지 지켜봄)
    val isTimeout by viewModel.isTimeout.collectAsState()

    // 상태가 true로 변하면 네비게이션 함수 호출
    LaunchedEffect(isTimeout) {
        if (isTimeout) {
            onNavigateToLogin()
        }
    }

    // 실제 UI 화면을 호출
    InitScreen()
}