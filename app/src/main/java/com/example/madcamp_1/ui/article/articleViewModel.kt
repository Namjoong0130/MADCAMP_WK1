// app/src/main/java/com/example/madcamp_1/ui/screen/article/articleViewModel.kt
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class UiComment(
    val id: String,
    val postId: String,
    val content: String,
    val createdAtMillis: Long,
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

    fun onCommentTextChange(v: String) {
        _commentText.value = v
    }

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
                _post.value = null
            } finally {
                _isBusy.value = false
            }
        }
    }

    fun toggleLike() {
        val p = _post.value ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.apiService.toggleLike(p.id)
                _post.value = p.copy(
                    likes = res.likeCount,
                    likedByMe = res.liked
                )
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "toggleLike 실패: ${e.message}")
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
                RetrofitClient.apiService.createComment(
                    id = p.id,
                    body = CommentCreateRequest(content = content)
                )
                _commentText.value = ""

                // ✅ 정합성: 댓글 목록 갱신 후, detail도 다시 불러서 commentCount/likedByMe 등 동기화
                fetchComments(p.id)
                fetch(p.id)
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "sendComment 실패: ${e.message}")
            } finally {
                _isBusy.value = false
            }
        }
    }

    private suspend fun fetchComments(postId: String) {
        try {
            val res = RetrofitClient.apiService.getComments(postId)
            _comments.value = res.items.map { c ->
                UiComment(
                    id = c.id,
                    postId = c.postId,
                    content = c.content,
                    createdAtMillis = parseIsoDateToMillis(c.createdAt),
                    authorNickname = c.author?.nickname,
                    authorSchoolId = c.author?.schoolId
                )
            }.sortedBy { it.createdAtMillis }
        } catch (e: Exception) {
            Log.e("ArticleViewModel", "fetchComments 실패: ${e.message}")
            _comments.value = emptyList()
        }
    }

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
