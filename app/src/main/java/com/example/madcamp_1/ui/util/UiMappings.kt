// app/src/main/java/com/example/madcamp_1/ui/util/UiMappings.kt
package com.example.madcamp_1.ui.util

import androidx.compose.ui.graphics.Color
import java.util.Calendar
import java.util.concurrent.TimeUnit

object UiMappings {

    // ===== School =====
    fun schoolLabel(schoolId: String?): String {
        val s = schoolId?.lowercase().orEmpty()
        return when {
            s.contains("kaist") -> "KAIST"
            s.contains("postech") -> "POSTECH"
            s.contains("yonsei") -> "YONSEI"
            s.contains("korea") -> "KOREA"
            s.isBlank() -> "SCHOOL"
            else -> "SCHOOL"
        }
    }

    fun schoolColor(schoolId: String?): Color {
        val s = schoolId?.lowercase().orEmpty()
        return when {
            s.contains("kaist") -> Color(0xFF005EB8)
            s.contains("postech") -> Color(0xFFE0224E)
            s.contains("yonsei") -> Color(0xFF0D47A1)
            s.contains("korea") -> Color(0xFFB71C1C)
            else -> Color(0xFF616161)
        }
    }

    // ===== Tag =====
    fun tagColor(tagName: String): Color {
        return when (tagName.trim()) {
            "공지" -> Color(0xFF9C27B0)
            "소통" -> Color(0xFF03A9F4)
            "꿀팁" -> Color(0xFFFFB300)
            "Q&A" -> Color(0xFF4CAF50)
            else -> Color(0xFF607D8B)
        }
    }

    /**
     * ✅ Dashboard 작성 시간 표시 기준 (고정 룰)
     * - 0~59초: 방금
     * - 1~59분: N분 전
     * - 1~23시간: N시간 전
     * - 1일: 어제
     * - 그 외: MM/dd
     */
    fun formatDashboardTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        if (diff < 0) return "방금"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            diff < 60_000L -> "방금"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days == 1L -> "어제"
            else -> {
                val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
                val m = cal.get(Calendar.MONTH) + 1
                val d = cal.get(Calendar.DAY_OF_MONTH)
                "%02d/%02d".format(m, d)
            }
        }
    }

    /**
     * Article/댓글에서 쓰는 상대시간(밀리초 기준)
     * - 0~59초: 방금
     * - 1~59분: N분 전
     * - 1~23시간: N시간 전
     * - 그 외: MM/dd HH:mm
     */
    fun formatRelativeTime(millis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - millis
        if (diff < 0) return "방금"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            diff < 60_000L -> "방금"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days < 7 -> "${days}일 전"
            else -> {
                val cal = Calendar.getInstance().apply { timeInMillis = millis }
                val m = cal.get(Calendar.MONTH) + 1
                val d = cal.get(Calendar.DAY_OF_MONTH)
                val hh = cal.get(Calendar.HOUR_OF_DAY)
                val mm = cal.get(Calendar.MINUTE)
                "%02d/%02d %02d:%02d".format(m, d, hh, mm)
            }
        }
    }
}
