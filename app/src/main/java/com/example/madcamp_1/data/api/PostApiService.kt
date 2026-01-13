// PostApiService.kt
package com.example.madcamp_1.data.api

import com.example.madcamp_1.data.model.*
import retrofit2.http.*

interface PostApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // Media
    @POST("media")
    suspend fun uploadMedia(@Body request: MediaCreateRequest): MediaResponse

    // Posts
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

    // Like
    @POST("posts/{id}/like")
    suspend fun toggleLike(@Path("id") id: String): LikeToggleResponse

    // Comments
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

    // (응원전 API가 필요하면 유지)
    @GET("cheer-matches/active")
    suspend fun getActiveMatch(): CheerMatchResponse

    @POST("cheer-taps")
    suspend fun postCheerTaps(@Body request: CheerTapRequest): Any
}
