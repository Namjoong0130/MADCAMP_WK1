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

    private val _selectedTag = MutableStateFlow("전체")
    val selectedTag = _selectedTag.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts.filter {
            (tag == "전체" || it.category == tag) &&
                    (it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true))
        }
            .sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 초기화 시 호출
    init { fetchPosts() }

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
                        id = item.id.hashCode(),
                        title = item.title,
                        content = item.content,
                        // [수정] item.tags가 null인지 먼저 체크하고 firstOrNull 호출
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDate(item.createdAt),
                        author = item.author.nickname,
                        // [수정] item.medias가 null인지 체크
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

    private fun parseIsoDate(isoString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(isoString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) { System.currentTimeMillis() }
    }

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onTagSelected(tag: String) { _selectedTag.value = tag }
}