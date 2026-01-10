package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScheduleEvent(
    val name: String,
    val color: Color,
    val dayIndex: Int,
    val startHour: Double,
    val duration: Double
)

class ScheduleViewModel : ViewModel() {
    // 학교 정보 (예시 데이터)
    val userName = "김철수"
    val userSchool = "POSTECH" // "POSTECH" 또는 "KAIST"
    val postechScore = 7
    val kaistScore = 0

    // 파스텔 톤 색상 정의
    private val pastelRed = Color(0xFFFFB3BA)
    private val pastelOrange = Color(0xFFFFDFBA)
    private val pastelYellow = Color(0xFFFFFFBA)
    private val pastelGreen = Color(0xFFBAFFC9)
    private val pastelBlue = Color(0xFFBAE1FF)
    private val pastelPurple = Color(0xFFE1BAFF)

    private val _events = MutableStateFlow(listOf(
        ScheduleEvent("전야제", pastelGreen, 0, 18.0, 4.0),
        ScheduleEvent("개막식", pastelRed, 1, 10.0, 1.5),
        ScheduleEvent("E-sports", pastelOrange, 1, 12.0, 3.0),
        ScheduleEvent("AI 세션", pastelBlue, 1, 16.0, 2.0),
        ScheduleEvent("축구", pastelPurple, 1, 19.0, 3.0),
        ScheduleEvent("야구", pastelOrange, 2, 10.0, 3.0),
        ScheduleEvent("과학퀴즈", pastelYellow, 2, 14.0, 2.0),
        ScheduleEvent("농구", pastelRed, 2, 17.0, 2.0),
        ScheduleEvent("폐막식", pastelBlue, 2, 20.0, 3.0)
    ))
    val events = _events.asStateFlow()
}