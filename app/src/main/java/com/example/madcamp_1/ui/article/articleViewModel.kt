package com.example.madcamp_1.ui.screen.article

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.data.model.CommentCreateRequest
import com.example.madcamp_1.ui.screen.dashboard.Media
import com.example.madcamp_1.ui.screen.dashboard.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ✅ 1. UiComment 정의 (이게 없으면 빨간색으로 뜹니다)
data class UiComment(
    val id: String,
    val postId: String,
    val content: String,
    val createdAtMillis: Long, // ✅ 여기서 정의되어야 함
    val authorNickname: String?,
    val authorSchoolId: String?
)

class ArticleViewModel : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<UiComment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText = _commentText.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy = _isBusy.asStateFlow()

    fun onCommentTextChange(v: String) { _commentText.value = v }

    fun fetch(postId: String) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                val dto = RetrofitClient.apiService.getPostDetail(postId)
                val category = dto.tags?.firstOrNull()?.tag?.name ?: "소통"
                val medias = dto.medias.orEmpty().map { m -> Media(id = m.id, url = m.url) }

                _post.value = Post(
                    id = dto.id,
                    title = dto.title,
                    content = dto.content,
                    category = category,
                    timestamp = parseIsoDateToMillis(dto.createdAt),
                    author = dto.author?.nickname ?: "익명",
                    authorSchoolId = dto.author?.schoolId,
                    imageUri = medias.firstOrNull()?.url,
                    likes = dto.likeCount,
                    likedByMe = dto.likedByMe ?: false,
                    commentCount = dto.commentCount ?: 0,
                    medias = medias
                )
                fetchComments(postId)
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "fetch 실패: ${e.message}")
            } finally {
                _isBusy.value = false
            }
        }
    }

    // ✅ 좋아요 토글 (낙관적 업데이트)
    private var isLikeToggling = false

    fun toggleLike() {
        val currentPost = _post.value ?: return
        if (isLikeToggling) return

        viewModelScope.launch {
            isLikeToggling = true

            // 1. 낙관적 업데이트
            val willBeLiked = !currentPost.likedByMe
            val newLikesCount = if (willBeLiked) currentPost.likes + 1 else currentPost.likes - 1

            // ✅ .value = ... 대신 .update { ... } 를 사용하면 더 안전합니다. (import kotlinx.coroutines.flow.update)
            _post.update { it?.copy(likedByMe = willBeLiked, likes = newLikesCount) }

            try {
                val res = RetrofitClient.apiService.toggleLike(currentPost.id)
                // 2. 서버 값으로 최종 동기화
                _post.update { it?.copy(likes = res.likeCount, likedByMe = res.liked) }
            } catch (e: Exception) {
                // 3. 실패 시 롤백
                _post.update { currentPost }
            } finally {
                isLikeToggling = false
            }
        }
    }

    fun sendComment() {
        val p = _post.value ?: return
        val content = _commentText.value.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            _isBusy.value = true
            try {
                RetrofitClient.apiService.createComment(p.id, CommentCreateRequest(content))
                _commentText.value = ""
                fetchComments(p.id)
                fetch(p.id)
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "댓글 실패")
            } finally {
                _isBusy.value = false
            }
        }
    }

    private suspend fun fetchComments(postId: String) {
        try {
            val res = RetrofitClient.apiService.getComments(postId)
            _comments.value = res.items.map { c ->
                // ✅ 여기서 CommentDto를 UiComment로 변환
                UiComment(
                    id = c.id,
                    postId = c.postId,
                    content = c.content,
                    createdAtMillis = parseIsoDateToMillis(c.createdAt), // ✅ 여기서 사용됨
                    authorNickname = c.author?.nickname,
                    authorSchoolId = c.author?.schoolId
                )
            }.sortedBy { it.createdAtMillis }
        } catch (e: Exception) {
            _comments.value = emptyList()
        }
    }

    // ✅ 날짜 파싱 유틸
    private fun parseIsoDateToMillis(isoString: String): Long {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (p in patterns) {
            try {
                val f = SimpleDateFormat(p, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
                val d = f.parse(isoString)
                if (d != null) return d.time
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }
}