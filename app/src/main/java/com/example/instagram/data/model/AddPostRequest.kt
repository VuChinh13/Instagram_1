package com.example.instagram.data.model

import java.io.File

data class AddPostRequest(
    val userId: String,
    val images: File,
    val content: String
)

data class LikePostRequest(
    val userId: String,
    val postId: String,
    val likeValue: Int // 1 để tym, -1 để bỏ tym
)

data class LikePostResponse(
    val status: Boolean,
    val message: String
)
