package com.example.madcamp_1.ui.screen.battle

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.PostResponse
import com.example.madcamp_1.data.utils.AuthManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private enum class SchoolSide { KAIST, POSTECH, OTHER }

class BattleViewModel : ViewModel() {

    // ✅ 내 학교(색상/정렬 기준용)
    var isPostechUser by mutableStateOf(false)
        private set

    // ✅ 좋아요 합계(바 구성 핵심)
    var kaistLikesTotal by mutableLongStateOf(0L)
        private set
    var postechLikesTotal by mutableLongStateOf(0L)
        private set

    // ✅ 학교별 Top 게시글(좋아요 순)
    var kaistTopPosts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var postechTopPosts by mutableStateOf<List<PostResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var pollingJob: Job? = null

    init {
        val schoolId = AuthManager.getSchoolId()
        isPostechUser = schoolId.contains("postech", ignoreCase = true)
        refreshLikeBattle()
        startPolling()
    }

    fun refreshLikeBattle() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // ✅ 최근 게시글 N개 기준으로 “상대적 좋아요”를 계산
                // 너무 많이 가져오면 비효율이니 200개 정도에서 컷(필요시 조정)
                val all = fetchRecentPosts(maxItems = 200)

                val kaist = mutableListOf<PostResponse>()
                val postech = mutableListOf<PostResponse>()

                var kSum = 0L
                var pSum = 0L

                for (p in all) {
                    when (sideOf(p)) {
                        SchoolSide.KAIST -> {
                            kSum += p.likeCount.toLong()
                            kaist.add(p)
                        }
                        SchoolSide.POSTECH -> {
                            pSum += p.likeCount.toLong()
                            postech.add(p)
                        }
                        else -> Unit
                    }
                }

                kaistLikesTotal = kSum
                postechLikesTotal = pSum

                kaistTopPosts = kaist.sortedByDescending { it.likeCount }.take(5)
                postechTopPosts = postech.sortedByDescending { it.likeCount }.take(5)

            } catch (e: Exception) {
                errorMessage = e.message ?: "불러오기에 실패했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(10_000) // 10초마다 갱신(원하시면 3~5초로도 가능)
                refreshLikeBattle()
            }
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }

    private suspend fun fetchRecentPosts(maxItems: Int): List<PostResponse> {
        val result = mutableListOf<PostResponse>()
        var cursor: String? = null

        while (result.size < maxItems) {
            val resp = RetrofitClient.apiService.getPosts(
                tag = null,
                limit = 50,
                cursor = cursor
            )
            result.addAll(resp.items)

            cursor = resp.nextCursor
            if (cursor.isNullOrBlank()) break
        }

        return if (result.size > maxItems) result.take(maxItems) else result
    }

    private fun sideOf(p: PostResponse): SchoolSide {
        val sid = (p.author?.schoolId ?: "").lowercase()
        return when {
            sid.contains("kaist") -> SchoolSide.KAIST
            sid.contains("postech") -> SchoolSide.POSTECH
            else -> SchoolSide.OTHER
        }
    }
}
