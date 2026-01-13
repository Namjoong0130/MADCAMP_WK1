package com.example.madcamp_1.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTag = MutableStateFlow("") // ""이면 전체
    val selectedTag = _selectedTag.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts
            .filter {
                val matchTag = tag.isEmpty() || it.category == tag
                val matchQuery = it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
                matchTag && matchQuery
            }
            .sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshPosts() = fetchPosts()

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("DashboardDebug", "서버에서 게시글 목록을 가져오는 중...")
                val response = RetrofitClient.apiService.getPosts()

                _posts.value = response.items.map { item ->
                    val medias = item.medias.orEmpty().map { m ->
                        Media(id = m.id, url = m.url)
                    }

                    Post(
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDateToMillis(item.createdAt),
                        author = item.author?.nickname ?: "익명",
                        authorSchoolId = item.author?.schoolId,      // ✅ 추가
                        imageUri = medias.firstOrNull()?.url,
                        likes = item.likeCount,
                        likedByMe = item.likedByMe ?: false,         // ✅ 서버가 주면 사용
                        commentCount = item.commentCount ?: 0,        // ✅ 서버가 주면 사용
                        medias = medias
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
        // 폴백 포함 버전(기존 로직 유지)
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
            } catch (_: Exception) { }
        }
        return System.currentTimeMillis()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onTagSelected(tag: String) {
        _selectedTag.value = tag
    }
}
