package com.example.madcamp_1.ui.screen.write

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WriteRoute(
    onBack: () -> Unit,
    viewModel: WriteViewModel = viewModel()
) {
    // 1. 이미지 변환을 위해 필요한 Context 가져오기
    val context = LocalContext.current

    WriteScreen(
        viewModel = viewModel,
        onBack = onBack,
        onComplete = { imageBase64, title, content, tag ->
            /**
             * [비트맵 테스트용 로그]
             * 서버가 없어도 Logcat에서 데이터가 잘 만들어졌는지 확인할 수 있습니다.
             */
            Log.d("WriteTest", "제목: $title")
            Log.d("WriteTest", "태그: $tag")
            Log.d("WriteTest", "내용: $content")

            if (imageBase64 != null) {
                // Base64 문자열은 매우 길기 때문에 앞부분 50자만 출력해서 확인
                Log.d("WriteTest", "이미지 변환 성공 (Base64): ${imageBase64}") //.take(50)
            } else {
                Log.d("WriteTest", "이미지가 선택되지 않았거나 변환에 실패했습니다.")
            }

            // TODO: 나중에 여기서 DashboardViewModel.addPost(post)를 호출하여 리스트에 추가합니다.

            // 작업 완료 후 입력 필드 초기화 및 뒤로 가기
            viewModel.clearFields()
            onBack()
        }
    )
}