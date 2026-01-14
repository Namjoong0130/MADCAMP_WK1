package com.example.madcamp_1.ui.screen.dashboard
import com.google.gson.annotations.SerializedName
data class Media(
    val id: String,
    val url: String?
)

data class Post(
    val id: String,
    val title: String, // 서버에서 "[익명] 제목" 형태로 옴
    val content: String,
    val category: String,
    val timestamp: Long,
    val author: String = "익명",
    val visibility: String = "PUBLIC",
    val authorSchoolId: String? = null,
    val imageUri: String? = null,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val commentCount: Int = 0,
    val medias: List<Media> = emptyList()
) {
    // ✅ 익명인지 체크 (제목이 [익명]으로 시작하는지)
    val isAnonymousPost: Boolean
        get() = title.startsWith("[익명]") || author == "익명" || visibility == "SCHOOL_ONLY"

    // ✅ 화면에 보여줄 제목 (앞의 [익명] 태그 제거)
    val displayTitle: String
        get() = if (title.startsWith("[익명]")) title.replace("[익명]", "").trim() else title

    // ✅ 화면에 보여줄 작성자
    val displayAuthor: String
        get() = if (isAnonymousPost) "익명" else author
}