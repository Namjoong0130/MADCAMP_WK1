package com.example.madcamp_1.ui.screen.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    // 1. 상태 관리 (아이디, 비밀번호)
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // 2. 입력 값 변경 함수
    fun onUsernameChange(newName: String) {
        _username.value = newName
    }

    fun onPasswordChange(newPw: String) {
        _password.value = newPw
    }

    // 3. 로그인 버튼 클릭 로직
    fun login(onSuccess: () -> Unit) {
        if (_username.value.isNotEmpty() && _password.value.isNotEmpty()) {
            // 실제로는 여기서 서버 통신을 하겠지만, 지금은 바로 성공 처리
            onSuccess()
        }
    }
}