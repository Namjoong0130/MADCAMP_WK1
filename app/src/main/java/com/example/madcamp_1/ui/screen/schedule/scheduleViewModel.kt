package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// 일정 데이터 클래스
data class ScheduleEvent(
    val name: String,
    val color: Color,
    val dayIndex: Int, // 0: 1일차, 1: 2일차, 2: 3일차
    val startHour: Double,
    val duration: Double
)

class ScheduleViewModel : ViewModel() {
    private val _events = MutableStateFlow(listOf(
        ScheduleEvent("전야제", Color(0xFFAED581), 0, 18.0, 5.0),
        ScheduleEvent("개막식", Color(0xFFFF5722), 1, 12.0, 1.5),
        ScheduleEvent("E-sports", Color(0xFF4CAF50), 1, 14.5, 2.5),
        ScheduleEvent("AI", Color(0xFF03A9F4), 1, 17.0, 1.0),
        ScheduleEvent("축구", Color(0xFF9C27B0), 1, 21.5, 2.0),
        ScheduleEvent("야구", Color(0xFFFF9800), 2, 10.0, 2.5),
        ScheduleEvent("과학퀴즈", Color(0xFFE91E63), 2, 13.5, 1.5),
        ScheduleEvent("농구", Color(0xFFF44336), 2, 15.5, 2.5),
        ScheduleEvent("폐막식", Color(0xFF009688), 2, 19.0, 3.0),
    ))
    val events = _events.asStateFlow()
}