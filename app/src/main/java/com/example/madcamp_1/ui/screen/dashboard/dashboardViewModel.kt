package com.example.madcamp_1.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTag = MutableStateFlow("전체")
    val selectedTag = _selectedTag.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 필터링 로직: 태그와 검색어 동시 적용
    val filteredPosts = combine(_posts, _selectedTag, _searchText) { posts, tag, query ->
        posts.filter {
            (tag == "전체" || it.category == tag) &&
                    (it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            // 실제 환경에서는 여기서 Retrofit API를 호출합니다.
            val currentTime = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000

            _posts.value = listOf(
                Post(1, "포스텍 야구부 모집", "매주 주말 즐겁게 경기하실 분!", "소통", currentTime, "익명", "https://picsum.photos/200", 15),
                Post(2, "학식 메뉴 꿀팁 공유", "이 식당은 이게 제일 맛있어요.", "꿀팁", currentTime - (dayMillis * 1), "익명", null, 42),
                Post(3, "컴공 전공책 팝니다", "거의 새 책입니다. 연락주세요.", "소통", currentTime - (dayMillis * 2), "익명", "https://picsum.photos/201", 3)
            )
            _isLoading.value = false
        }
    }

    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onTagSelected(tag: String) { _selectedTag.value = tag }
}