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

    private val _selectedTag = MutableStateFlow("")
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

    fun refreshPosts() { fetchPosts() }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getPosts()
                _posts.value = response.items.map { item ->
                    Post(
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDateToMillis(item.createdAt),
                        author = item.author.nickname, // 서버가 준 이름 그대로 노출
                        imageUri = item.medias?.firstOrNull()?.url,
                        likes = item.likeCount
                    )
                }
            } catch (e: Exception) {
                Log.e("DashboardDebug", "데이터 로드 실패: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseIsoDateToMillis(isoString: String): Long {
        val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'")
        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
                return format.parse(isoString)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) { continue }
        }
        return System.currentTimeMillis()
    }

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onTagSelected(tag: String) { _selectedTag.value = tag }
}