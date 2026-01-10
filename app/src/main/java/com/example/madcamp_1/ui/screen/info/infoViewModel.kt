package com.example.madcamp_1.ui.screen.info

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// [데이터 모델]
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

    private val _historyRecords = MutableStateFlow<List<MatchRecord>>(emptyList())
    val historyRecords: StateFlow<List<MatchRecord>> = _historyRecords.asStateFlow()

    fun loadDataByCategory(category: String) {
        when (category) {
            "축구" -> {
                _videoId.value = "n_McfqkAI1U"
                _stadiumLocation.value = LatLng(36.3740, 127.3650)
                _stadiumName.value = "KAIST 대운동장"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "3 : 2", "홈승"),
                    MatchRecord("2024", "KAIST", "1 : 0", "원정승"),
                    MatchRecord("2023", "KAIST", "1 : 0", "홈승")
                )
            }
            "해킹" -> {
                _videoId.value = "9bZkp7q19f0"
                _stadiumLocation.value = LatLng(36.3708, 127.3626)
                _stadiumName.value = "정보전자공학동(E3)"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "13100 : 11800", "홈승"),
                    MatchRecord("2024", "KAIST", "1850 : 1000", "원정승"),
                    MatchRecord("2023", "KAIST", "13782 : 10382", "홈승")
                )
            }
            "인공지능" -> {
                _videoId.value = "KkeNxIb1vkM"
                _stadiumLocation.value = LatLng(36.3712, 127.3638)
                _stadiumName.value = "학술문화관(E9) 양승택홀"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "3 : 0", "홈승"),
                    MatchRecord("2024", "POSTECH", "2 : 3", "홈승"),
                    MatchRecord("2023", "POSTECH", "1 : 4", "원정승")
                )
            }
            "E-sports" -> {
                _videoId.value = "PWuJxT7P5fM"
                _stadiumLocation.value = LatLng(36.3685, 127.3625)
                _stadiumName.value = "KAIST 대강당(E15)"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "2 : 0", "홈승"),
                    MatchRecord("2024", "KAIST", "2 : 1", "원정승"),
                    MatchRecord("2023", "KAIST", "2 : 0", "홈승")
                )
            }
            "야구" -> {
                _videoId.value = "Tg535gEwFa4"
                _stadiumLocation.value = LatLng(36.3705, 127.3600)
                _stadiumName.value = "KAIST 야구장"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "POSTECH", "4 : 8", "원정승"),
                    MatchRecord("2024", "KAIST", "14 : 0", "원정승"),
                    MatchRecord("2023", "KAIST", "6 : 3", "홈승")
                )
            }
            "과학퀴즈" -> {
                _videoId.value = "yS9YqZGMqA8"
                _stadiumLocation.value = LatLng(36.3712, 127.3638)
                _stadiumName.value = "학술문화관(E9) 양승택홀"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "POSTECH", "85 : 105", "원정승"),
                    MatchRecord("2024", "KAIST", "275 : 170", "원정승"),
                    MatchRecord("2023", "KAIST", "44 : 16", "홈승")
                )
            }
            "농구" -> {
                _videoId.value = "-WR3SbVs2rk"
                _stadiumLocation.value = LatLng(36.3702, 127.3620)
                _stadiumName.value = "류근철 스포츠 컴플렉스(N14)"
                _historyRecords.value = listOf(
                    MatchRecord("2025", "KAIST", "75 : 54", "홈승"),
                    MatchRecord("2024", "KAIST", "67 : 51", "원정승"),
                    MatchRecord("2023", "KAIST", "65 : 45", "홈승")
                )
            }
            else -> {
                _videoId.value = ""
                _stadiumName.value = "경기장 정보 없음"
                _historyRecords.value = emptyList()
            }
        }
    }
}