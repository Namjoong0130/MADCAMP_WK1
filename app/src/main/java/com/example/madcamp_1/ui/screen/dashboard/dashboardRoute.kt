package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardRoute(
    onNavigateToWrite: () -> Unit,
    onNavigateToArticle: (String) -> Unit,
    // ✅ 핵심: 이름 dashboardViewModel + 기본값 제공
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val searchText by dashboardViewModel.searchText.collectAsState()
    val selectedTag by dashboardViewModel.selectedTag.collectAsState()
    val posts by dashboardViewModel.filteredPosts.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchPosts()
    }

    DashboardScreen(
        searchText = searchText,
        selectedTag = selectedTag,
        posts = posts,
        isLoading = isLoading,
        onSearchChange = { dashboardViewModel.onSearchTextChange(it) },
        onTagSelect = { dashboardViewModel.onTagSelected(it) },
        onNavigateToWrite = onNavigateToWrite,
        onPostClick = onNavigateToArticle,
        onRefresh = { dashboardViewModel.refreshPosts() },
    )
}
