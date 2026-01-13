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

    private val _selectedTag = MutableStateFlow("전체")
    val selectedTag = _selectedTag.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts
            .filter {
                (tag == "전체" || it.category == tag) &&
                        (it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true))
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
                    Post(
                        // ✅ hashCode()로 Int 만들지 말고 서버 id 그대로 사용
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDateToMillis(item.createdAt),
                        author = item.author?.nickname ?: "익명",
                        imageUri = item.medias?.firstOrNull()?.url,
                        likes = item.likeCount
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

    /**
     * 서버 createdAt(ISO-8601 date-time)을 최대한 튼튼하게 millis로 변환합니다.
     * - 1순위: OffsetDateTime.parse (표준 ISO)
     * - 2순위: 기존 SimpleDateFormat(UTC 'Z') 패턴
     * - 실패 시: 현재 시간
     */
    private fun parseIsoDateToMillis(isoString: String): Long {
        // 1) 표준 ISO 파서
        try {
            return OffsetDateTime.parse(isoString).toInstant().toEpochMilli()
        } catch (_: DateTimeParseException) {
            // fallthrough
        } catch (_: Exception) {
            // fallthrough
        }

        // 2) 기존 패턴(예: 2026-01-12T11:22:33.123Z)
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(isoString)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            // 3) 보조 패턴(밀리초 없는 Z)
            try {
                val format2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                format2.timeZone = TimeZone.getTimeZone("UTC")
                format2.parse(isoString)?.time ?: System.currentTimeMillis()
            } catch (_: Exception) {
                System.currentTimeMillis()
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onTagSelected(tag: String) {
        _selectedTag.value = tag
    }
}
