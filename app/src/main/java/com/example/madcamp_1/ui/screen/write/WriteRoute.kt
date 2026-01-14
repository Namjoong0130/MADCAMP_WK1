package com.example.madcamp_1.ui.screen.write

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madcamp_1.ui.screen.dashboard.DashboardViewModel

@Composable
fun WriteRoute(
    onBack: () -> Unit,
    dashboardViewModel: DashboardViewModel,
    viewModel: WriteViewModel = viewModel()
) {
    val context = LocalContext.current

    WriteScreen(
        viewModel = viewModel,
        onBack = onBack,
        // ✅ [수정] 매개변수를 5개(image, title, content, tag, isAnonymous)로 업데이트
        onComplete = { imageBase64, title, content, tag, isAnonymous ->
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(context, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@WriteScreen
            }

            // 서버 전송 로직 호출
            viewModel.uploadPostToServer(context) {
                // 게시판 데이터 새로고침
                dashboardViewModel.fetchPosts()

                Toast.makeText(context, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()

                // 상태 초기화 및 뒤로 가기
                viewModel.clearFields()
                onBack()
            }
        }
    )
}