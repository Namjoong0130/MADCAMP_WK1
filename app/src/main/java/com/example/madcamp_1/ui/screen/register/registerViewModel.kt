package com.example.madcamp_1.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {
    private val _school = MutableStateFlow("")
    val school = _school.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isAgreed = MutableStateFlow(false)
    val isAgreed = _isAgreed.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun onSchoolChange(v: String) { _school.value = v }
    fun onEmailChange(v: String) { _email.value = v }
    fun onUsernameChange(v: String) { _username.value = v }
    fun onPasswordChange(v: String) { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }
    fun onAgreementChange(v: Boolean) { _isAgreed.value = v }

    private fun mapSchoolToId(schoolName: String): String? {
        return when (schoolName) {
            "KAIST" -> "seed_school_kaist"
            "POSTECH" -> "seed_school_postech"
            else -> null
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_school.value.isBlank()) {
                _errorEvent.emit("소속 학교를 반드시 선택해주세요.")
                return@launch
            }
            if (_password.value.length < 6) {
                _errorEvent.emit("비밀번호는 6자리 이상이어야 합니다.")
                return@launch
            }
            if (_password.value != _confirmPassword.value) {
                _errorEvent.emit("비밀번호가 일치하지 않습니다.")
                return@launch
            }
            if (!_isAgreed.value) {
                _errorEvent.emit("이용약관 및 개인정보 수집에 동의해주세요.")
                return@launch
            }

            try {
                RetrofitClient.apiService.register(
                    RegisterRequest(
                        email = _email.value,
                        password = _password.value,
                        nickname = _username.value,
                        schoolId = mapSchoolToId(_school.value)
                    )
                )
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                val message = when {
                    errorBody.contains("email", ignoreCase = true) -> "이미 사용 중인 이메일입니다."
                    errorBody.contains("nickname", ignoreCase = true) ||
                            errorBody.contains("username", ignoreCase = true) -> "이미 존재하는 아이디입니다."
                    else -> "회원가입 실패: 다시 시도해주세요."
                }
                _errorEvent.emit(message)
            } catch (e: Exception) {
                _errorEvent.emit("네트워크 연결을 확인해주세요.")
            }
        }
    }
}