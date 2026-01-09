package com.example.madcamp_1.ui.screen.initial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {
    // 3초가 지났는지 상태를 관리
    private val _isTimeout = MutableStateFlow(false)
    val isTimeout = _isTimeout.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(3000) // 3초 대기
            _isTimeout.value = true // 상태 변경
        }
    }
}