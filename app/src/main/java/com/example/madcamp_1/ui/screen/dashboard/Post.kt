package com.example.madcamp_1.ui.screen.dashboard

import com.google.gson.annotations.SerializedName

data class Media(val id: String, val url: String?)

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",
    val visibility: String = "PUBLIC",
    val authorSchoolId: String? = null,
    val imageUri: String? = null,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val commentCount: Int = 0,
    val medias: List<Media> = emptyList()
) {
    // ✅ 익명 판별: 제목에 [익명]이 붙어있는지 확인
    val isAnonymousPost: Boolean
        get() = title.startsWith("[익명]") || author == "익명"

    // ✅ 화면용 제목: [익명] 머리말 제거
    val displayTitle: String
        get() = if (title.startsWith("[익명]")) title.substringAfter("[익명]").trim() else title

    // ✅ 화면용 작성자: 익명글이면 "익명", 아니면 실제 이름
    val displayAuthor: String
        get() = if (isAnonymousPost) "익명" else author
}