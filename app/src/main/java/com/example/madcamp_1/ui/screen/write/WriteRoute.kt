package com.example.madcamp_1.ui.screen.write

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WriteRoute(
    onBack: () -> Unit,
    viewModel: WriteViewModel = viewModel()
) {
    WriteScreen(
        viewModel = viewModel,
        onBack = onBack,
        onComplete = {
            // TODO: DashboardViewModel에 데이터를 전달하는 로직 추가
            onBack()
        }
    )
}