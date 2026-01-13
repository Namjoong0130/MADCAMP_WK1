package com.example.madcamp_1.ui.screen.schedule

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.madcamp_1.R
import com.example.madcamp_1.data.utils.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScheduleEvent(
    val name: String,
    val color: Color,
    val dayIndex: Int,
    val startHour: Double,
    val duration: Double,
    val content: String,    // 상세 설명
    val location: String,   // 장소
    val categoryKey: String // Info 페이지 이동용 키
)

class ScheduleViewModel : ViewModel() {
    private val _userName = MutableStateFlow(AuthManager.getFormattedNickname())
    val userName = _userName.asStateFlow()

    private val _userSchoolId = MutableStateFlow(AuthManager.getSchoolId())
    val userSchoolId = _userSchoolId.asStateFlow()

    // 선택된 일정을 관리하는 상태
    private val _selectedEvent = MutableStateFlow<ScheduleEvent?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    val postechScore = 7
    val kaistScore = 0

    // 파스텔 색상 정의
    private val pastelRed = Color(0xFFFFB3BA)
    private val pastelOrange = Color(0xFFFFDFBA)
    private val pastelYellow = Color(0xFFFFFFBA)
    private val pastelGreen = Color(0xFFBAFFC9)
    private val pastelBlue = Color(0xFFBAE1FF)
    private val pastelPurple = Color(0xFFE1BAFF)

    private val _events = MutableStateFlow(listOf(
        // categoryKey를 InfoViewModel의 when 조건인 한글 이름과 일치시킵니다.
        ScheduleEvent("전야제", pastelGreen, 0, 18.0, 4.0, "축제의 시작!", "포스텍 대강당", "전야제"),
        ScheduleEvent("개막식", pastelRed, 1, 10.0, 1.5, "공식 행사", "카이스트 운동장", "개막식"),
        ScheduleEvent("E-sports", pastelOrange, 1, 12.0, 3.0, "LOL 결승전", "카이스트 대강당", "E-sports"),
        ScheduleEvent("AI", pastelBlue, 1, 16.0, 2.0, "인공지능 배틀", "정보제어관", "인공지능"),
        ScheduleEvent("축구", pastelPurple, 1, 19.0, 3.0, "메인 매치", "대운동장", "축구"),
        ScheduleEvent("야구", pastelOrange, 2, 10.0, 3.0, "야구 정기전", "야구장", "야구"),
        ScheduleEvent("과학퀴즈", pastelYellow, 2, 14.0, 2.0, "과학 상식 대결", "학술문화관", "과학퀴즈"),
        ScheduleEvent("농구", pastelRed, 2, 17.0, 2.0, "농구 정기전", "스포츠컴플렉스", "농구"),
        ScheduleEvent("폐막식", pastelBlue, 2, 20.0, 3.0, "축제 종료", "노천극장", "폐막식")
    ))
    val events = _events.asStateFlow()

    fun onEventClick(event: ScheduleEvent) { _selectedEvent.value = event }
    fun clearSelectedEvent() { _selectedEvent.value = null }

    fun getSchoolLogo(schoolId: String) = if (schoolId.contains("postech", ignoreCase = true)) R.drawable.postech else R.drawable.kaist
    fun getSchoolDisplayName(schoolId: String) = if (schoolId.contains("postech", ignoreCase = true)) "POSTECH" else "KAIST"
}