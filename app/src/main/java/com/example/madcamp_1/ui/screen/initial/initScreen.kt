package com.example.madcamp_1.ui.screen.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.madcamp_1.R // 프로젝트 패키지명에 맞게 수정 필요
import kotlinx.coroutines.delay

@Composable
fun InitScreen(onTimeout: () -> Unit) {
    // 화면이 처음 나타날 때 실행되는 효과
    LaunchedEffect(Unit) {
        delay(3000L) // 3000ms = 3초 대기
        onTimeout()    // 3초 후 전달받은 함수(화면 이동) 실행
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 가운데 띄울 사진 (res/drawable 폴더에 있는 이미지 이름을 넣으세요)
        // 예시로 ic_launcher_foreground를 썼습니다. 본인의 이미지로 변경하세요.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Splash Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}
