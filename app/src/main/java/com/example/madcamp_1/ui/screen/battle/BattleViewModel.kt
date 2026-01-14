package com.example.madcamp_1.ui.screen.battle

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.CheerTapRequest
import com.example.madcamp_1.data.utils.AuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BattleViewModel : ViewModel() {
    var isPostechUser by mutableStateOf(false)
    var kaistScore by mutableLongStateOf(0L)
    var postechScore by mutableLongStateOf(0L)

    var matchId by mutableStateOf("")
    var myTeamId by mutableStateOf("")

    init {
        val schoolId = AuthManager.getSchoolId()
        isPostechUser = schoolId.contains("postech", ignoreCase = true)
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                fetchServerData()
                delay(3000) // 3초 간격
            }
        }
    }

    private suspend fun fetchServerData() {
        try {
            val response = RetrofitClient.apiService.getActiveMatch()
            matchId = response.id

            // ✅ 핵심: 팀 이름을 확인하여 점수와 ID를 동기화
            if (response.homeTeam.name.contains("POSTECH", ignoreCase = true)) {
                postechScore = response.homeTotalTaps
                kaistScore = response.awayTotalTaps
                myTeamId = if (isPostechUser) response.homeTeam.id else response.awayTeam.id
            } else {
                kaistScore = response.homeTotalTaps
                postechScore = response.awayTotalTaps
                myTeamId = if (isPostechUser) response.awayTeam.id else response.homeTeam.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onTap() {
        if (matchId.isEmpty() || myTeamId.isEmpty()) return

        // 1. 즉각적인 UI 반영
        if (isPostechUser) postechScore++ else kaistScore++

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.postCheerTaps(
                    CheerTapRequest(matchId, myTeamId, 1)
                )
                // 2. 서버 응답 점수로 최종 보정 (백엔드 구조에 맞춰 업데이트)
                fetchServerData() // 탭 후 즉시 다시 불러와서 정확한 합계 반영
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}