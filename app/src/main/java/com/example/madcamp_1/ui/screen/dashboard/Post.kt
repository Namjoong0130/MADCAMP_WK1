package com.example.madcamp_1.ui.screen.dashboard

import java.text.SimpleDateFormat
import java.util.*

data class Post(
    // ✅ 서버 postId를 그대로 유지 (상세 페이지 라우팅에 필수)
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",
    // ✅ 서버에서 내려오는 medias.url 이 base64(data URL)일 수 있음
    //    리스트에서는 Coil이 data: 스킴을 처리할 수도 있고, 상세에서는 직접 디코딩해서 쓸 수도 있습니다.
    val imageUri: String? = null,
    val likes: Int = 0
)

fun formatPostTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val postDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (
        now.get(Calendar.YEAR) == postDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == postDate.get(Calendar.DAY_OF_YEAR)
    ) {
        // 오늘인 경우: 14:30
        SimpleDateFormat("HH:mm", Locale.KOREAN).format(Date(timestamp))
    } else {
        // 오늘이 아닌 경우: 01월 12일
        SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(Date(timestamp))
    }
}
