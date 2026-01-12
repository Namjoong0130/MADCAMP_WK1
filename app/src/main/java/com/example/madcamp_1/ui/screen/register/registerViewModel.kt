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

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun onSchoolChange(newSchool: String) { _school.value = newSchool }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    private fun mapSchoolToId(schoolName: String): String? {
        return when (schoolName) {
            "KAIST" -> "seed_school_kaist"
            "POSTECH" -> "seed_school_postech"
            else -> null
        }
    }

    // 오직 이 함수가 호출될 때만 검증이 시작됩니다.
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // [1순위] 학교 선택 여부
            if (_school.value.isBlank()) {
                _errorEvent.emit("소속 학교를 반드시 선택해주세요.")
                return@launch
            }

            // [2순위] 비밀번호 규칙 검사
            if (_password.value.length < 6) {
                _errorEvent.emit("비밀번호는 6자리 이상이어야 합니다.")
                return@launch
            }

            try {
                // [3순위 & 4순위] 서버 요청을 통한 중복 및 유효성 검사
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
                    // 이메일 중복 우선 확인
                    errorBody.contains("email", ignoreCase = true) -> "이미 사용 중인 이메일입니다."
                    // 아이디 중복 확인
                    errorBody.contains("nickname", ignoreCase = true) ||
                            errorBody.contains("username", ignoreCase = true) -> "이미 존재하는 아이디입니다."
                    else -> "회원가입에 실패했습니다. 다시 시도해주세요."
                }
                _errorEvent.emit(message)
            } catch (e: Exception) {
                _errorEvent.emit("네트워크 연결을 확인해주세요.")
            }
        }
    }
}