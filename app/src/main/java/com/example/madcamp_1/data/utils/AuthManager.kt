package com.example.madcamp_1.data.utils

import com.example.madcamp_1.data.model.AuthUser

object AuthManager {
    var currentUser: AuthUser? = null

    // 원본 닉네임 가져오기
    fun getNickname(): String = currentUser?.nickname ?: "사용자"

    fun getFormattedNickname(): String {
        val nickname = getNickname()
        return if (nickname.length > 7) {
            nickname.take(7) + "..."
        } else {
            nickname
        }
    }

    // 학교 ID 가져오기
    fun getSchoolId(): String = currentUser?.schoolId ?: ""

    // 로그인 처리
    fun login(user: AuthUser) {
        this.currentUser = user
    }
}