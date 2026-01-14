package com.example.madcamp_1.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.utils.AuthManager

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    // ViewModel에서 로그인 결과(AuthResponse)를 관찰할 수 있는 상태가 있다고 가정합니다.
    // 만약 ViewModel의 login 함수 내부에서 직접 처리한다면 아래와 같이 성공 콜백을 구성합니다.

    LoginScreen(
        username = username,
        password = password,
        errorEvent = viewModel.errorEvent,
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onLoginClick = {
            viewModel.login(
                onSuccess = { authResponse ->
                    // 1. 토큰 저장 (API 요청용)
                    RetrofitClient.authToken = authResponse.accessToken.toString()

                    // 2. 전역 유저 정보 저장 (UI 표시용 - Schedule 화면 등에서 사용)
                    AuthManager.currentUser = authResponse.user

                    // 3. 화면 이동
                    onLoginSuccess()
                }
            )
        },
        onRegisterClick = onNavigateToRegister
    )
}