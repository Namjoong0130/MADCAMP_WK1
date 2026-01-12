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
    val medias: List<MediaResponse>,
    val tags: List<TagDto>,
    val likeCount: Int // <--- 여기에 이 이름으로 존재해야 ViewModel에서 빨간 줄이 사라집니다.
)

data class AuthorDto(val nickname: String)
data class TagDto(val name: String)