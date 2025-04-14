package com.example.instagram.data.data_source.api

import com.example.instagram.data.model.DeletePostRequest
import com.example.instagram.data.model.DeletePostResponse
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.data.model.LikePostRequest
import com.example.instagram.data.model.LikePostResponse
import com.example.instagram.data.model.LoginRequest
import com.example.instagram.data.model.LoginResponse
import com.example.instagram.data.model.PostResponse
import com.example.instagram.data.model.SignupRequest
import com.example.instagram.data.model.SignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("/api/v1/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("/api/v1/list-post")
    suspend fun getPost(
        @Query("sort") sort: String = "moi-nhat",
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 10
    ): Response<PostResponse>

    @GET("/api/v1/user/{username}") // Dùng {username} để định nghĩa path parameter
    suspend fun getInforUser(@Path("username") username: String): Response<InforUserResponse?>

    @PATCH("/api/v1/user")
    @Multipart
    suspend fun updateUserInfo(
        @Part("old_password") oldPassword: RequestBody?,
        @Part("new_password") newPassword: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part avatar: MultipartBody.Part?,
        @Part("gender") gender: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("introduce") introduce: RequestBody?,
        @Part("userId") userId: RequestBody
    ): Response<InforUserResponse>

    @GET("api/v1/list-post/{username}")
    suspend fun getUserPosts(
        @Path("username") username: String,
        @Query("sort") sort: String = "moi-nhat",
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 10
    ): Response<PostResponse>

    @Multipart
    @POST("/api/v1/post")
    suspend fun addPost(
        @Part("userId") userId: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>?
    ): Response<PostResponse>

    @HTTP(method = "DELETE", path = "/api/v1/post", hasBody = true)
    suspend fun deletePost(
        @Body request: DeletePostRequest
    ): Response<DeletePostResponse>



    @PATCH("/api/v1/post")
    @Multipart
    suspend fun updatePost(
        @Part("userId") userId: RequestBody,
        @Part("postId") postId: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part("content") content: RequestBody?
    ): Response<PostResponse>

    @POST("/api/v1/like")
    suspend fun likePost(
        @Body request: LikePostRequest
    ): Response<LikePostResponse>

}
