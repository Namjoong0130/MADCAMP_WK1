package com.example.madcamp_1.ui.screen.article

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ArticleRoute(
    postId: Int,
    onBack: () -> Unit,
    viewModel: ArticleViewModel = viewModel()
) {
    val post by viewModel.post.collectAsState()

    LaunchedEffect(postId) {
        viewModel.fetchPostById(postId)
    }

    ArticleScreen(post = post, onBack = onBack)
}