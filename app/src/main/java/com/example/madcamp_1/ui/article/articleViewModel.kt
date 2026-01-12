package com.example.madcamp_1.ui.screen.article

import androidx.lifecycle.ViewModel
import com.example.madcamp_1.ui.screen.dashboard.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArticleViewModel : ViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    fun fetchPostById(postId: Int) {
        // 실제 구현 시 서버 통신(Retrofit) 로직이 들어갑니다.
        // 현재는 샘플 데이터를 반환합니다.
        _post.value = Post(
            id = postId,
            title = "상세 페이지 테스트 제목",
            content = "이것은 게시글의 상세 내용입니다. 서버에서 전달받은 긴 텍스트가 이곳에 출력됩니다.",
            category = "소통",
            timestamp = System.currentTimeMillis(),
            author = "익명",
            imageUri = "https://picsum.photos/600/400",
            likes = 25
        )
    }
}