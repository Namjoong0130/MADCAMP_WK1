package com.example.madcamp_1.data.model

// 미디어 생성 요청/응답
data class MediaCreateRequest(val url: String)
data class MediaResponse(
    val id: String,
    val url: String,
    val mimeType: String? = null,
    val size: Int? = null,
    val width: Int? = null,
    val height: Int? = null
)

// ✅ 서버 원본 DTO에 맞게 nickname 제거
data class PostCreateRequest(
    val title: String,
    val content: String,
    val visibility: String = "PUBLIC", // 서버 허용 값: "PUBLIC" 또는 "SCHOOL_ONLY"
    val tagIds: List<String> = emptyList(),
    val mediaIds: List<String> = emptyList()
)

data class PostListResponse(
    val items: List<PostListItem>,
    val nextCursor: String?
)

data class PostListItem(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val author: AuthorDto,
    val medias: List<MediaResponse>? = null,
    val tags: List<TagDto>? = null,
    val likeCount: Int = 0
)

data class AuthorDto(val nickname: String)

data class TagDto(val tag: TagDetailDto?)
data class TagDetailDto(val id: String? = null, val name: String)

data class PostResponse(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val author: AuthorDto? = null,
    val medias: List<MediaResponse>? = null,
    val tags: List<TagDto>? = null,
    val likeCount: Int = 0
)
