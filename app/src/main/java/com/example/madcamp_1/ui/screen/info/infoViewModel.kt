package com.example.madcamp_1.ui.screen.info

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InfoViewModel : ViewModel() {
    // 1. YouTube 영상 ID (예시: 라이브 중계 ID)
    private val _videoId = MutableStateFlow("dQw4w9WgXcQ") // 예시 ID
    val videoId = _videoId.asStateFlow()

    // 2. 경기장 위치 (예시: 포스텍 대운동장)
    private val _stadiumLocation = MutableStateFlow(LatLng(36.0142, 129.3253))
    val stadiumLocation = _stadiumLocation.asStateFlow()

    private val _stadiumName = MutableStateFlow("포스텍 대운동장")
    val stadiumName = _stadiumName.asStateFlow()
}