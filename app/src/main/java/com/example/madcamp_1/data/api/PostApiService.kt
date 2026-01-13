package com.example.madcamp_1.data.api

import com.example.madcamp_1.data.model.*
import retrofit2.http.*

interface PostApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    // 1. 미디어(Base64) 먼저 업로드
    @POST("media")
    suspend fun uploadMedia(@Body request: MediaCreateRequest): MediaResponse

    // 2. 게시글 작성
    @POST("posts")
    suspend fun createPost(@Body request: PostCreateRequest): Any // 응답은 Any 혹은 PostResponse

    // 3. 게시글 목록 조회
    @GET("posts")
    suspend fun getPosts(
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 20
    ): PostListResponse
}