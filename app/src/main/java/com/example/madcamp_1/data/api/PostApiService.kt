package com.example.madcamp_1.data.api

import com.example.madcamp_1.data.model.*
import retrofit2.http.*

interface PostApiService {
    // --- 기존 API ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("media")
    suspend fun uploadMedia(@Body request: MediaCreateRequest): MediaResponse

    @POST("posts")
    suspend fun createPost(@Body request: PostCreateRequest): PostResponse

    @GET("posts")
    suspend fun getPosts(
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): PostListResponse

    @GET("posts/{id}")
    suspend fun getPostDetail(@Path("id") id: String): PostResponse

    @POST("posts/{id}/like")
    suspend fun toggleLike(@Path("id") id: String): LikeToggleResponse

    @GET("posts/{id}/comments")
    suspend fun getComments(
        @Path("id") id: String,
        @Query("limit") limit: Int = 50,
        @Query("cursor") cursor: String? = null
    ): CommentsListResponse

    @POST("posts/{id}/comments")
    suspend fun createComment(
        @Path("id") id: String,
        @Body body: CommentCreateRequest
    ): CommentDto

    // --- ✅ 응원전(Cheer) API 수정 ---
    // 로그캣의 404 해결을 위해 백엔드 라우터 설정을 확인해야 합니다.
    // 만약 서버에서 app.use("/api/cheer", cheerRouter) 로 마운트했다면
    // 아래 경로는 "active-match" 가 되어야 합니다.

    @GET("cheer/active-match")
    suspend fun getActiveMatch(): CheerMatchResponse

    @POST("cheer/taps")
    suspend fun postCheerTaps(@Body request: CheerTapRequest): CheerTapResponse
}