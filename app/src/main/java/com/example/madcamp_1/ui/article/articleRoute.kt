package com.example.madcamp_1.ui.screen.article

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ArticleRoute(
    postId: String,
    onBack: () -> Unit,
    viewModel: ArticleViewModel = viewModel()
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentText by viewModel.commentText.collectAsState()

    LaunchedEffect(postId) {
        viewModel.fetch(postId)
    }

    ArticleScreen(
        post = post,
        comments = comments,
        commentText = commentText,
        onCommentTextChange = viewModel::onCommentTextChange,
        onToggleLike = viewModel::toggleLike,
        onSendComment = viewModel::sendComment,
        onBack = onBack
    )
}
