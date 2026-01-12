package com.example.madcamp_1.ui.screen.dashboard

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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { fetchPosts() }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getPosts()
                _posts.value = response.items.map { item ->
                    Post(
                        id = item.id.hashCode(), // String을 Int로 변환
                        title = item.title,
                        content = item.content,
                        category = item.tags.firstOrNull()?.name ?: "소통",
                        timestamp = parseIsoDate(item.createdAt),
                        author = item.author.nickname,
                        imageUri = item.medias.firstOrNull()?.url, // 서버에 저장된 Base64 URL
                        likes = item.likeCount
                    )
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { _isLoading.value = false }
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