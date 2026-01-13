package com.example.madcamp_1.data.utils

import com.example.madcamp_1.data.model.AuthUser

object AuthManager {
    var currentUser: AuthUser? = null

    // 닉네임 5글자 제한 및 ... 처리 로직
    fun getFormattedNickname(): String {
        val nickname = currentUser?.nickname ?: "사용자"
        return if (nickname.length > 5) {
            nickname.take(5) + "..."
        } else {
            nickname
        }
    }

    // 학교 ID (로고 결정용)
    fun getSchoolId(): String = currentUser?.schoolId ?: ""
}