// LoginViewModel.kt
package com.example.madcamp_1.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.AuthResponse
import com.example.madcamp_1.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    fun login(onSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            val email = _username.value
            val pwd = _password.value

            // ✅ 케이스 1: 하나라도 비어있는 경우
            if (email.isBlank() || pwd.isBlank()) {
                _errorEvent.emit("이메일과 비밀번호를 입력하세요")
                return@launch
            }

            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = email, password = pwd)
                )
                onSuccess(response)

            } catch (e: Exception) {
                // ✅ 케이스 2: 틀렸거나 기타 서버 에러 발생 시 (HTTPException 등 모두 포함)
                _errorEvent.emit("이메일 및 비밀번호가 올바르지 않습니다")
            }
        }
    }
}