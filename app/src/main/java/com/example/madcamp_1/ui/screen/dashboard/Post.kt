package com.example.madcamp_1.ui.screen.dashboard

import java.text.SimpleDateFormat
import java.util.*

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",
    val imageUri: String? = null,
    val likes: Int = 0
)

fun formatPostTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val postDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (now.get(Calendar.YEAR) == postDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == postDate.get(Calendar.DAY_OF_YEAR)
    ) {
        // 오늘인 경우: 14:30
        SimpleDateFormat("HH:mm", Locale.KOREAN).format(Date(timestamp))
    } else {
        // 오늘이 아닌 경우: 01월 12일
        SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(Date(timestamp))
    }
}