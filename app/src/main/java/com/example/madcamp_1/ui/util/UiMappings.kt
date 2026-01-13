package com.example.madcamp_1.ui.util

import androidx.compose.ui.graphics.Color
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun tagColor(tagName: String): Color = when (tagName) {
    "공지" -> Color(0xFF9C27B0)
    "소통" -> Color(0xFF03A9F4)
    "꿀팁" -> Color(0xFFFFB300)
    "Q&A", "Q&A " -> Color(0xFF4CAF50)
    else -> Color(0xFF9E9E9E)
}

fun tagBg(tagName: String): Color = tagColor(tagName).copy(alpha = 0.10f)

// 학교 뱃지 색(요청하신 기능 구현할 때 그대로 사용)
fun schoolBadgeColor(schoolId: String?): Color {
    val s = (schoolId ?: "").lowercase()
    return when {
        s.contains("kaist") -> Color(0xFF005EB8)   // KAIST 블루
        s.contains("postech") -> Color(0xFFE0224E) // POSTECH 레드
        else -> Color(0xFF9E9E9E)
    }
}
object UiMappings {

    // ✅ 학교 id가 seed_school_kaist 같은 형태여도 동작하도록 처리
    fun schoolLabel(schoolId: String?): String {
        val s = (schoolId ?: "").lowercase()
        return when {
            "kaist" in s -> "KAIST"
            "postech" in s -> "POSTECH"
            "yonsei" in s -> "YONSEI"
            "korea" in s -> "KOREA"
            else -> "SCHOOL"
        }
    }

    fun schoolColor(schoolId: String?): Color {
        val s = (schoolId ?: "").lowercase()
        return when {
            "kaist" in s -> Color(0xFF005BAC)   // KAIST Blue
            "postech" in s -> Color(0xFFC62828) // POSTECH Red
            "yonsei" in s -> Color(0xFF0B3D91)  // Yonsei Navy
            "korea" in s -> Color(0xFF7B001C)   // Korea Crimson
            else -> Color(0xFF757575)
        }
    }

    // ✅ 태그별 색(원하시는 팔레트로 조정 가능)
    fun tagColor(tagName: String, fallback: Color): Color {
        return when (tagName) {
            "공지" -> Color(0xFF6A1B9A)
            "Q&A" -> Color(0xFF1565C0)
            "소통" -> Color(0xFF2E7D32)
            "꿀팁" -> Color(0xFFEF6C00)
            "자유" -> Color(0xFF455A64)
            else -> fallback
        }
    }

    /**
     * ✅ “작성시간 표시 기준” 확립
     * - 1분 미만: 방금
     * - 1시간 미만: N분 전
     * - 24시간 미만: N시간 전
     * - 48시간 미만: 어제
     * - 7일 미만: N일 전
     * - 그 외: yyyy.MM.dd
     */
    fun formatRelativeTime(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val diff = nowMillis - epochMillis
        val sec = TimeUnit.MILLISECONDS.toSeconds(abs(diff))
        val min = TimeUnit.MILLISECONDS.toMinutes(abs(diff))
        val hour = TimeUnit.MILLISECONDS.toHours(abs(diff))
        val day = TimeUnit.MILLISECONDS.toDays(abs(diff))

        if (sec < 60) return "방금"
        if (min < 60) return "${min}분 전"
        if (hour < 24) return "${hour}시간 전"
        if (hour < 48) return "어제"
        if (day < 7) return "${day}일 전"

        // yyyy.MM.dd (간단 포맷)
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return "%04d.%02d.%02d".format(y, m, d)
    }
}
