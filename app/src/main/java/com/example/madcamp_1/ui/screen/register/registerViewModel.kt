package com.example.madcamp_1.ui.screen.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
    // 1. 상태 선언
    private val _school = MutableStateFlow("") // 기본값
    val school = _school.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // 2. 값 변경 함수
    fun onSchoolChange(newSchool: String) { _school.value = newSchool }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    fun register(onSuccess: () -> Unit) {
        // 모든 필드가 채워졌을 때만 가입 성공 (간단한 예시)
        if (_email.value.isNotBlank() && _username.value.isNotBlank() && _password.value.isNotBlank()) {
            onSuccess()
        }
    }
}