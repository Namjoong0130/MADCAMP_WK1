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
    var matchId by mutableStateOf("")
    var myTeamId by mutableStateOf("")

    var kaistScore by mutableLongStateOf(0L)
    var postechScore by mutableLongStateOf(0L)

    var isPostechUser by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    init {
        val schoolId = AuthManager.getSchoolId()
        isPostechUser = schoolId.contains("postech", ignoreCase = true)
        loadActiveMatch()
    }

    private fun loadActiveMatch() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getActiveMatch()
                matchId = response.id

                // 내 팀 ID 찾기
                myTeamId = if (isPostechUser) {
                    if (response.homeTeam.name == "POSTECH") response.homeTeam.id else response.awayTeam.id
                } else {
                    if (response.homeTeam.name == "KAIST") response.homeTeam.id else response.awayTeam.id
                }

                kaistScore = if (response.homeTeam.name == "KAIST") response.homeTotalTaps else response.awayTotalTaps
                postechScore = if (response.homeTeam.name == "POSTECH") response.homeTotalTaps else response.awayTotalTaps

                isLoading = false
                startPolling()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun onTap() {
        if (matchId.isEmpty() || myTeamId.isEmpty()) return

        // 1. UI 즉시 반영 (낙관적 업데이트)
        if (isPostechUser) postechScore++ else kaistScore++

        // 2. 서버 전송 (스키마의 CheerTap count 증가)
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.postCheerTaps(
                    CheerTapRequest(matchId = matchId, teamId = myTeamId, count = 1)
                )
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(3000) // 3초마다 점수 갱신
                val response = RetrofitClient.apiService.getActiveMatch()
                kaistScore = if (response.homeTeam.name == "KAIST") response.homeTotalTaps else response.awayTotalTaps
                postechScore = if (response.homeTeam.name == "POSTECH") response.homeTotalTaps else response.awayTotalTaps
            }
        }
    }
}