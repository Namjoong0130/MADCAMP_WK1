package com.example.madcamp_1.ui.screen.info

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 역대 전적을 위한 데이터 모델
data class MatchRecord(
    val year: String,
    val winner: String,
    val score: String,
    val note: String = ""
)

class InfoViewModel : ViewModel() {

    private val _videoId = MutableStateFlow("")
    val videoId: StateFlow<String> = _videoId.asStateFlow()

    private val _stadiumLocation = MutableStateFlow(LatLng(36.3721, 127.3604))
    val stadiumLocation: StateFlow<LatLng> = _stadiumLocation.asStateFlow()

    private val _stadiumName = MutableStateFlow("")
    val stadiumName: StateFlow<String> = _stadiumName.asStateFlow()

    // [변경] 일정 대신 역대 전적 리스트 관리
    private val _historyRecords = MutableStateFlow<List<MatchRecord>>(emptyList())
    val historyRecords: StateFlow<List<MatchRecord>> = _historyRecords.asStateFlow()

    fun loadDataByCategory(category: String) {
        when (category) {
            "축구" -> {
                _videoId.value = "dQw4w9WgXcQ" // 실제 ID로 변경
                _stadiumLocation.value = LatLng(36.3740, 127.3650)
                _stadiumName.value = "대운동장"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "3 : 1", "MVP: 홍길동"),
                    MatchRecord("2024", "POSTECH", "0 : 0", "무승부"),
                    MatchRecord("2023", "KAIST", "2 : 1", "역전승")
                )
            }
            "해킹" -> {
                _videoId.value = "videoId_hacking"
                _stadiumLocation.value = LatLng(36.3700, 127.3620)
                _stadiumName.value = "정보전자공학동"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "1500pt", "All Kill"),
                    MatchRecord("2024", "KAIST", "1200pt", "우승")
                )
            }
            "AI" -> {
                _videoId.value = "videoId_ai"
                _stadiumLocation.value = LatLng(36.3710, 127.3630)
                _stadiumName.value = "학술문화관"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "POSTECH", "Acc 98%", "딥러닝 세션 우승"),
                    MatchRecord("2024", "KAIST", "Acc 95%", "전략 우승")
                )
            }
            else -> {
                _videoId.value = ""
                _historyRecords.value = emptyList()
            }
        }
    }
}