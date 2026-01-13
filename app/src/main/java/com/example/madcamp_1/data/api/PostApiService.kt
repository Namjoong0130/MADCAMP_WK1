package com.example.madcamp_1.data.api

import com.example.madcamp_1.data.model.*
import retrofit2.http.*

interface PostApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // Media(Base64) 업로드
    @POST("media")
    suspend fun uploadMedia(@Body request: MediaCreateRequest): MediaResponse

    // Posts - 생성
    // ✅ 서버 응답 형식이 확정되지 않았다면 Any 유지(지금 코드 유지)
    @POST("posts")
    suspend fun createPost(@Body request: PostCreateRequest): Any

    // Posts - 목록
    @GET("posts")
    suspend fun getPosts(
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 20
    ): PostListResponse

    // ✅ Posts - 상세 (이번에 추가)
    @GET("posts/{id}")
    suspend fun getPostDetail(
        @Path("id") id: String
    ): PostResponse
}
