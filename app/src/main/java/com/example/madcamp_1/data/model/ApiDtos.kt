package com.example.madcamp_1.data.model

// ---------- Media ----------
data class MediaCreateRequest(
    val url: String
)

data class MediaResponse(
    val id: String,
    val url: String? = null,
    val mimeType: String? = null,
    val size: Int? = null,
    val width: Int? = null,
    val height: Int? = null
)

// ---------- Tag / Author ----------
data class AuthorDto(
    val id: String,
    val nickname: String,
    val schoolId: String?,
    val profileImageUrl: String?
)

data class TagDto(
    val id: String,
    val name: String
)

// posts 응답의 tags는 PostTag 조인 형태로 내려오므로 이 구조가 안전합니다.
data class PostTagDto(
    val postId: String? = null,
    val tagId: String? = null,
    val tag: TagDto
)

// ---------- Posts ----------
data class PostCreateRequest(
    val title: String,
    val content: String,
    val visibility: String,
    val tagIds: List<String>,
    val mediaIds: List<String>,
    val authorNickname: String // 👈 이 이름이 서버가 받는 이름과 정확히 일치해야 함
)

data class PostListResponse(
    val items: List<PostResponse>,
    val nextCursor: String?
)

data class PostResponse(
    val id: String,
    val authorId: String? = null,
    val title: String,
    val content: String,
    val visibility: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int?= null,
    val createdAt: String,
    val updatedAt: String? = null,
    val deletedAt: String? = null,

    val author: AuthorDto? = null,
    val tags: List<PostTagDto>? = null,     // ✅ nullable
    val medias: List<MediaResponse>? = null,// ✅ nullable

    // 서버가 제공하면 쓰고, 없으면 앱에서 별도 API로 갱신
    val likedByMe: Boolean? = null
)

// ---------- Like ----------
data class LikeToggleResponse(
    val liked: Boolean,
    val likeCount: Int
)

// ---------- Comments ----------
data class CommentCreateRequest(
    val content: String,
    val parentId: String? = null
)

data class CommentDto(
    val id: String,
    val postId: String,
    val authorId: String,
    val content: String,
    val parentId: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
    val author: AuthorDto? = null
)

data class CommentsListResponse(
    val items: List<CommentDto>,
    val nextCursor: String?
)
