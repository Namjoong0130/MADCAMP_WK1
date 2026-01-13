package com.example.madcamp_1.data.model

import com.google.gson.annotations.SerializedName

// 미디어 생성 요청/응답
data class MediaCreateRequest(val url: String) // 여기에 Base64 문자열 전달
data class MediaResponse(val id: String, val url: String)

// 게시글 생성 요청
data class PostCreateRequest(
    val title: String,
    val content: String,
    val visibility: String = "PUBLIC",
    val tagIds: List<String> = emptyList(),
    val mediaIds: List<String> = emptyList()
)

// 게시글 목록 응답 (서버 명세 기준)
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
    // 서버에서 필드가 안 올 수 있으므로 nullable
    val medias: List<MediaResponse>?,
    val tags: List<TagDto>?,
    val likeCount: Int = 0
)

// ✅ 작성자 DTO (상세에서도 최소 nickname만 쓰면 충분)
data class AuthorDto(val nickname: String)

// ✅ 태그 DTO
data class TagDto(
    val tag: TagDetailDto?
)
data class TagDetailDto(
    val name: String
)

// ===============================
// ✅ 게시글 상세 응답 DTO (추가)
// ===============================
data class PostResponse(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    // 서버가 author를 안 줄 수도 있으니 nullable 허용
    val author: AuthorDto? = null,

    // 상세에서도 medias/tags가 없을 수 있으니 nullable/기본값 처리
    val medias: List<MediaResponse>? = null,
    val tags: List<TagDto>? = null,

    // likeCount가 없다면 0으로
    val likeCount: Int = 0
)
