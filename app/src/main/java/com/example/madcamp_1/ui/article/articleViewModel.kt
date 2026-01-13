package com.example.madcamp_1.ui.screen.article

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp_1.data.api.RetrofitClient
import com.example.madcamp_1.ui.screen.dashboard.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.TimeZone

class ArticleViewModel : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    fun fetchPostById(postId: String) {
        viewModelScope.launch {
            _post.value = null
            try {
                val dto = RetrofitClient.apiService.getPostDetail(postId)

                val category = dto.tags
                    ?.firstOrNull()
                    ?.tag
                    ?.name
                    ?: "소통"
                val firstUrl = dto.medias?.firstOrNull()?.url
                Log.d(
                    "ArticleViewModel",
                    "post=${dto.id} medias=${dto.medias?.size} urlPrefix=${firstUrl?.take(30)}"
                )

                _post.value = Post(
                    id = dto.id,
                    title = dto.title,
                    content = dto.content,
                    category = category,
                    timestamp = parseIsoDateToMillis(dto.createdAt),
                    author = dto.author?.nickname ?: "익명",
                    imageUri = dto.medias?.firstOrNull()?.url,
                    likes = dto.likeCount
                )
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "getPostDetail 실패: ${e.message}")
                e.printStackTrace()
                _post.value = null
            }
        }
    }

    private fun parseIsoDateToMillis(isoString: String): Long {
        try {
            return OffsetDateTime.parse(isoString).toInstant().toEpochMilli()
        } catch (_: DateTimeParseException) {
        } catch (_: Exception) {
        }

        return try {
            val f1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            f1.timeZone = TimeZone.getTimeZone("UTC")
            f1.parse(isoString)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            try {
                val f2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                f2.timeZone = TimeZone.getTimeZone("UTC")
                f2.parse(isoString)?.time ?: System.currentTimeMillis()
            } catch (_: Exception) {
                System.currentTimeMillis()
            }
        }
    }
}
