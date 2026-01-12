package com.example.madcamp_1.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _school = MutableStateFlow("")
    val school = _school.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow("") // 서버의 nickname으로 사용
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onSchoolChange(newSchool: String) { _school.value = newSchool }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onUsernameChange(newName: String) { _username.value = newName }
    fun onPasswordChange(newPw: String) { _password.value = newPw }

    // [중요] UI 텍스트를 서버 ID로 변환하는 함수
    private fun mapSchoolToId(schoolName: String): String? {
        return when (schoolName) {
            "KAIST" -> "seed_school_kaist"
            "POSTECH" -> "seed_school_postech"
            else -> null
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. 서버에 가입 요청
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(
                        email = _email.value,
                        password = _password.value,
                        nickname = _username.value, // username을 nickname으로 매핑
                        schoolId = mapSchoolToId(_school.value)
                    )
                )

                // 3. 성공 콜백 (로그인 화면으로 이동 등)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: 에러 처리 (이미 가입된 이메일 등)
            }
        }
    }
}