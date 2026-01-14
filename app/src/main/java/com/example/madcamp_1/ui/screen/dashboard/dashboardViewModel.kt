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
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTag = MutableStateFlow("")
    val selectedTag = _selectedTag.asStateFlow()

    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts.filter {
            (tag.isEmpty() || it.category == tag) &&
                    (it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true))
        }.sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshPosts() = fetchPosts()
    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getPosts()
                _posts.value = response.items.map { item ->
                    val medias = item.medias.orEmpty().map { Media(it.id, it.url) }
                    Post(
                        id = item.id,
                        title = item.title,
                        content = item.content,
                        category = item.tags?.firstOrNull()?.tag?.name ?: "소통",
                        timestamp = parseIsoDateToMillis(item.createdAt),
                        author = item.author?.nickname ?: "익명",
                        authorSchoolId = item.author?.schoolId,
                        imageUri = medias.firstOrNull()?.url,
                        likes = item.likeCount, // ✅ 서버에서 온 좋아요 수 매핑
                        likedByMe = item.likedByMe ?: false, // ✅ 내가 눌렀는지 여부 매핑
                        commentCount = item.commentCount ?: 0,
                        medias = medias
                    )
                }
            } catch (e: Exception) {
                Log.e("Dashboard", "목록 로드 실패: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseIsoDateToMillis(iso: String): Long {
        return try {
            val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
            f.parse(iso)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) { System.currentTimeMillis() }
    }

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onTagSelected(tag: String) { _selectedTag.value = tag }
}