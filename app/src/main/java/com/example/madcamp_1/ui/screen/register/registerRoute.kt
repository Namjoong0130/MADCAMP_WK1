package com.example.madcamp_1.ui.screen.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterRoute(
    onBackToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val school by viewModel.school.collectAsState()
    val email by viewModel.email.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    RegisterScreen(
        school = school,
        email = email,
        username = username,
        password = password,
        errorEvent = viewModel.errorEvent,
        onSchoolChange = { viewModel.onSchoolChange(it) },
        onEmailChange = { viewModel.onEmailChange(it) },
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onRegisterClick = { viewModel.register(onSuccess = onBackToLogin) },
        onBackClick = onBackToLogin
    )
}