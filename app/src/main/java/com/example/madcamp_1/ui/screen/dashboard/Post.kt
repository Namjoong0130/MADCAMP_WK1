package com.example.madcamp_1.ui.screen.dashboard

import java.text.SimpleDateFormat
import java.util.*

data class Media(
    val id: String,
    val url: String
)

data class Post(
    // ✅ 서버 postId 그대로 사용 (상세 라우팅에 필수)
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",

    // ✅ 대시보드 썸네일용: medias의 첫 번째 url을 여기에 넣어두면 화면단이 단순해집니다.
    val imageUri: String? = null,

    val likes: Int = 0,

    // ✅ 상세에서 여러 장 보여주기 위한 원본 리스트
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
