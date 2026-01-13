package com.example.madcamp_1.ui.screen.battle

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.CheerTapRequest
import com.example.madcamp_1.data.model.TapperDto
import com.example.madcamp_1.data.utils.AuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BattleViewModel : ViewModel() {
    var isPostechUser by mutableStateOf(false)
    var kaistScore by mutableLongStateOf(0L)
    var postechScore by mutableLongStateOf(0L)
    var kaistTopTappers by mutableStateOf<List<TapperDto>>(emptyList())
    var postechTopTappers by mutableStateOf<List<TapperDto>>(emptyList())

    private var matchId = ""
    private var myTeamId = ""

    init {
        val schoolId = AuthManager.getSchoolId()
        isPostechUser = schoolId.contains("postech", ignoreCase = true)
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                fetchServerData()
                delay(3000) // 3초 간격 폴링
            }
        }
    }

    private suspend fun fetchServerData() {
        try {
            val response = RetrofitClient.apiService.getActiveMatch()
            matchId = response.id

            // 서버의 home/away를 학교 이름에 따라 배정
            if (response.homeTeam.name == "POSTECH") {
                postechScore = response.homeTotalTaps
                kaistScore = response.awayTotalTaps
                postechTopTappers = response.topHomeTappers
                kaistTopTappers = response.topAwayTappers
                myTeamId = if (isPostechUser) response.homeTeam.id else response.awayTeam.id
            } else {
                kaistScore = response.homeTotalTaps
                postechScore = response.awayTotalTaps
                kaistTopTappers = response.topHomeTappers
                postechTopTappers = response.topAwayTappers
                myTeamId = if (isPostechUser) response.awayTeam.id else response.homeTeam.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onTap() {
        if (matchId.isEmpty() || myTeamId.isEmpty()) return

        // 1. 즉각적인 UI 반영 (Optimistic Update)
        if (isPostechUser) postechScore++ else kaistScore++

        // 2. 서버 연동
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.postCheerTaps(
                    CheerTapRequest(matchId, myTeamId, 1)
                )
                // 서버 응답 점수로 최종 동기화
                postechScore = if (isPostechUser) response.homeTotalTaps else response.awayTotalTaps // 실제 팀 구분에 따라 수정 필요
                // 주의: postCheerTap 응답에는 리더보드가 없으므로, 리더보드는 폴링에 의존합니다.
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}