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
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("") // 실제로는 이메일 주소 입력
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // 에러 메시지 전달을 위한 SharedFlow
    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    /**
     * 수정된 login 함수: 성공 시 서버로부터 받은 AuthResponse를 콜백으로 넘겨줍니다.
     */
    fun login(onSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            val email = _username.value
            val pwd = _password.value

            // 1. 유효성 검사
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _errorEvent.emit("올바른 이메일 형식이 아닙니다.")
                return@launch
            }

            if (pwd.length < 6) {
                _errorEvent.emit("비밀번호는 6자리 이상이어야 합니다.")
                return@launch
            }

            try {
                // 2. 서버 통신 (PostApiService 호출)
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = email, password = pwd)
                )

                // 3. 성공 시 AuthResponse 전체를 Route로 전달
                onSuccess(response)

            } catch (e: HttpException) {
                // HTTP 에러 코드별 대응
                val message = when (e.code()) {
                    401 -> "이메일 또는 비밀번호가 잘못되었습니다."
                    404 -> "존재하지 않는 계정입니다."
                    else -> "서버 오류가 발생했습니다 (${e.code()})"
                }
                _errorEvent.emit(message)
            } catch (e: Exception) {
                _errorEvent.emit("네트워크 연결을 확인해주세요.")
            }
        }
    }
}