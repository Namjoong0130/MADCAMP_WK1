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
    dashboardViewModel: DashboardViewModel, // 대시보드 새로고침을 위해 추가
    viewModel: WriteViewModel = viewModel()
) {
    val context = LocalContext.current

    WriteScreen(
        viewModel = viewModel,
        onBack = onBack,
        onComplete = { imageBase64, title, content, tag ->
            // [1] 디버깅 로그
            Log.d("WriteTest", "서버 전송 시작 - 제목: $title")

            // [2] 실제 서버 업로드 함수 호출
            viewModel.uploadPostToServer(context) {
                // [3] 업로드 성공 시 콜백 로직
                Log.d("WriteTest", "서버 전송 성공!")

                // 게시판 데이터 새로고침 (이걸 해야 돌아갔을 때 새 글이 보입니다)
                dashboardViewModel.fetchPosts()

                Toast.makeText(context, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()

                // 상태 초기화 및 뒤로 가기
                viewModel.clearFields()
                onBack()
            }
        }
    )
}