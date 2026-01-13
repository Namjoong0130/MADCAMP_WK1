package com.example.madcamp_1.data.model

data class LoginRequest(
    val email: String,    // 서버는 email을 원합니다
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val schoolId: String? = null
)

data class AuthResponse(
    val user: AuthUser,
    // String에서 Any?로 바꿉니다.
    // 서버가 "{}"(객체)를 주든 "token"(문자열)을 주든 일단 다 받아내기 위함입니다.
    val accessToken: Any?,
    val refreshToken: Any?
)

data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String,
    val role: String,
    val schoolId: String? = null,
    val profileImageUrl: String? = null
)