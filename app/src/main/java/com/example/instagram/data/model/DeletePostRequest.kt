package com.example.instagram.data.model

data class DeletePostRequest(
    val userId: String,
    val postId: String
)

data class DeletePostResponse(
    val status: Boolean,
    val message: String
)
