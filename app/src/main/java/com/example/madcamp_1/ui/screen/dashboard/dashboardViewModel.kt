package com.example.madcamp_1.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// 게시글 데이터 클래스
data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val tag: String,
    val time: String,
    val author: String = "익명"
)

class DashboardViewModel : ViewModel() {
    // 1. 상태 선언
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedTag = MutableStateFlow("전체")
    val selectedTag = _selectedTag.asStateFlow()

    // 예시 데이터
    private val _allPosts = MutableStateFlow(listOf(
        Post(1, "포스텍 축구부 모집", "같이 축구하실 분 구합니다!", "소통", "10분 전"),
        Post(2, "학식 메뉴 꿀팁", "오늘 점심 메뉴가 대박이네요.", "꿀팁", "30분 전"),
        Post(3, "컴공 과제 도와주세요", "파이썬 기초 질문입니다.", "Q&A", "1시간 전"),
        Post(4, "겨울방학 생활관 공지", "퇴사 관련 안내드립니다.", "공지", "2시간 전"),
        Post(5, "카이스트 축제 라인업?", "올해 누가 오나요?", "소통", "3시간 전")
    ))

    // 태그와 검색어에 따라 필터링된 리스트
    val filteredPosts = combine(_allPosts, _selectedTag, _searchText) { posts, tag, query ->
        posts.filter {
            (tag == "전체" || it.tag == tag) && it.title.contains(query, ignoreCase = true)
        }
    }

    // 2. 이벤트 함수
    fun onSearchTextChange(text: String) { _searchText.value = text }
    fun onTagSelected(tag: String) { _selectedTag.value = tag }
}