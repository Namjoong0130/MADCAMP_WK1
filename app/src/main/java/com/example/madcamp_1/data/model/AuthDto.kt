package com.example.madcamp_1.data.model

data class LoginRequest(
    val email: String,
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
    val accessToken: String,
    val refreshToken: String
)

data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String,
    val role: String,
    val schoolId: String? = null,
    val profileImageUrl: String? = null
)
