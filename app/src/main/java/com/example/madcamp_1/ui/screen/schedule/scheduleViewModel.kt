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
    val startHour: Int,
    val duration: Int
)

class ScheduleViewModel : ViewModel() {
    private val _events = MutableStateFlow(listOf(
        ScheduleEvent("축구", Color(0xFFAED581), 0, 10, 2), // 1일차 10시~12시
        ScheduleEvent("야구", Color(0xFF81D4FA), 1, 14, 3)  // 2일차 14시~17시
    ))
    val events = _events.asStateFlow()
}