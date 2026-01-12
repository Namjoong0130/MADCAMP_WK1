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
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    LoginScreen(
        username = username,
        password = password,
        errorEvent = viewModel.errorEvent, // ViewModel의 에러 흐름 연결
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onLoginClick = { viewModel.login(onSuccess = onLoginSuccess) },
        onRegisterClick = onNavigateToRegister
    )
}