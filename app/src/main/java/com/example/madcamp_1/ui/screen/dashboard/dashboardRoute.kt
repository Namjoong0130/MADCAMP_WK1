package com.example.madcamp_1.ui.screen.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardRoute(viewModel: DashboardViewModel = viewModel()) {
    val searchText by viewModel.searchText.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val posts by viewModel.filteredPosts.collectAsState(initial = emptyList())

    DashboardScreen(
        searchText = searchText,
        selectedTag = selectedTag,
        posts = posts,
        onSearchChange = { viewModel.onSearchTextChange(it) },
        onTagSelect = { viewModel.onTagSelected(it) }
    )
}