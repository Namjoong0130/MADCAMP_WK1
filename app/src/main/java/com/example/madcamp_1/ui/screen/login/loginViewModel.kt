package com.example.madcamp_1.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
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

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val email = _username.value
            val pwd = _password.value

            // [검증 1순위] 이메일 형식 확인 (로그 상 test@kaist.ac.kr 형태 필요)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _errorEvent.emit("올바른 이메일 형식이 아닙니다.")
                return@launch
            }

            // [검증 2순위] 비밀번호 길이 확인 (최소 6자)
            if (pwd.length < 6) {
                _errorEvent.emit("비밀번호는 6자리 이상이어야 합니다.")
                return@launch
            }

            try {
                // [검증 3순위] 서버 통신 및 인증 결과 확인
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = email, password = pwd)
                )

                val token = response.accessToken?.toString() ?: ""
                RetrofitClient.authToken = token
                onSuccess()
            } catch (e: HttpException) {
                // 로그에서 확인된 401 Unauthorized 에러 대응
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