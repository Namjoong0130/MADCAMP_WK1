package com.example.madcamp_1.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // ViewModel의 상태를 관찰
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    // UI(Screen) 호출
    LoginScreen(
        username = username,
        password = password,
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onLoginClick = {
            viewModel.login(onSuccess = onLoginSuccess)
        },
        onRegisterClick = onNavigateToRegister
    )
}