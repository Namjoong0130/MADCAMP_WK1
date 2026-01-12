package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardRoute(
    onNavigateToWrite: () -> Unit,
    onNavigateToArticle: (Int) -> Unit, // 추가
    viewModel: DashboardViewModel = viewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val posts by viewModel.filteredPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    DashboardScreen(
        searchText = searchText,
        selectedTag = selectedTag,
        posts = posts,
        isLoading = isLoading,
        onSearchChange = { viewModel.onSearchTextChange(it) },
        onTagSelect = { viewModel.onTagSelected(it) },
        onNavigateToWrite = onNavigateToWrite,
        onPostClick = onNavigateToArticle // 추가
    )
}