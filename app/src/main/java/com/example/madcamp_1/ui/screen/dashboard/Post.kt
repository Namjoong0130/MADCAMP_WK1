// Post.kt
package com.example.madcamp_1.ui.screen.dashboard

import java.text.SimpleDateFormat
import java.util.*

data class Media(
    val id: String,
    val url: String?
)

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",

    // ✅ 학교 뱃지/댓글 정렬 등에 필요
    val authorSchoolId: String? = null,

    val imageUri: String? = null,

    val likes: Int = 0,
    val likedByMe: Boolean = false,

    val commentCount: Int = 0,

    val medias: List<Media> = emptyList()
)

fun formatPostTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val postDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (
        now.get(Calendar.YEAR) == postDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == postDate.get(Calendar.DAY_OF_YEAR)
    ) {
        SimpleDateFormat("HH:mm", Locale.KOREAN).format(Date(timestamp))
    } else {
        SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(Date(timestamp))
    }
}
