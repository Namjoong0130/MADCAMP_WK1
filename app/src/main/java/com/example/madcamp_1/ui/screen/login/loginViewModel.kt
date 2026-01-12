package com.example.madcamp_1.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("") // 여기에 email을 입력받음
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    // LoginViewModel.kt 내부
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = _username.value, password = _password.value)
                )

                // 토큰이 객체일 경우를 대비해 문자열로 안전하게 변환
                val token = response.accessToken?.toString() ?: ""
                RetrofitClient.authToken = token

                // 이제 파싱 에러가 나지 않으므로 정상적으로 onSuccess가 실행됩니다!
                onSuccess()
            } catch (e: Exception) {
                // 에러가 나면 로그에 찍어서 확인
                android.util.Log.e("LoginError", "로그인 실패: ${e.message}")
            }
        }
    }
}