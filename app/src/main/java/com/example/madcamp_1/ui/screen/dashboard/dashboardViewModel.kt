package com.example.madcamp_1.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.*

class DashboardViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // ✅ [수정] 초기값을 "전체"에서 ""(빈 문자열)로 변경
    private val _selectedTag = MutableStateFlow("")
    val selectedTag = _selectedTag.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // ✅ [수정] 필터링 로직: tag가 비어있을 때 모든 카테고리를 허용하도록 변경
    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts
            .filter {
                // tag가 ""이면(아무것도 선택 안됨) true, 아니면 카테고리 일치 확인
                val matchTag = tag.isEmpty() || it.category == tag

                // 검색어 포함 확인
                val matchQuery = it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)

                matchTag && matchQuery
            }
            .sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshPosts() {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("DashboardDebug", "서버에서 게시글 목록을 가져오는 중...")
                val response = RetrofitClient.apiService.getPosts()
                Log.d("DashboardDebug", "서버 응답 성공! 아이템 개수: ${response.items.size}")


                _posts.value = response.items.map { item ->
                    val firstUrl = item.medias?.firstOrNull()?.url
                    Log.d("DashboardDebug", "post=${item.id} medias=${item.medias?.size} urlPrefix=${firstUrl?.take(30)}")

                    Post(
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        // 서버에서 온 태그 정보를 category로 매칭
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDateToMillis(item.createdAt),
                        author = item.author?.nickname ?: "익명",
                        imageUri = item.medias?.firstOrNull()?.url,
                        likes = item.likeCount,

                    )
                }
            } catch (e: Exception) {
                Log.e("DashboardDebug", "게시글 로드 실패: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseIsoDateToMillis(isoString: String): Long {
        // 1) 표준 ISO 파서 시도 (API 26 이상 또는 Desugaring 설정 시)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                return java.time.OffsetDateTime.parse(isoString).toInstant().toEpochMilli()
            }
        } catch (e: Exception) {
            // 실패하면 다음 단계로
        }

        // 2) SimpleDateFormat을 이용한 폴백 (모든 버전 호환)
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss"
        )

        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = format.parse(isoString)
                if (date != null) return date.time
            } catch (e: Exception) {
                continue // 다음 패턴 시도
            }
        }

        // 3) 모든 시도가 실패하면 현재 시간 반환
        return System.currentTimeMillis()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onTagSelected(tag: String) {
        _selectedTag.value = tag
    }
}