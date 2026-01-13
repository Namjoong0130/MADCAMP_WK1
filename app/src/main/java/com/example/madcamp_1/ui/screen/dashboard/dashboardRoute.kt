package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardRoute(
    onNavigateToWrite: () -> Unit,
    onNavigateToArticle: (Int) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val posts by viewModel.filteredPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // [추가] 이 화면이 사용자에게 보일 때마다 서버에서 데이터를 다시 가져옵니다.
    LaunchedEffect(Unit) {
        viewModel.fetchPosts()
    }

    DashboardScreen(
        searchText = searchText,
        selectedTag = selectedTag,
        posts = posts,
        isLoading = isLoading,
        onSearchChange = { viewModel.onSearchTextChange(it) },
        onTagSelect = { viewModel.onTagSelected(it) },
        onNavigateToWrite = onNavigateToWrite,
        onPostClick = onNavigateToArticle,
        onRefresh = { viewModel.refreshPosts() },
    )
}