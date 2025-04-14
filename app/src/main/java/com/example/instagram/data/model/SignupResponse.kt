package com.example.instagram.data.model

data class SignupResponse(
    val status: Boolean,
    val message: String,
    val data: User
)

data class User(
    val _id: String?,
    val username: String?,
    val password: String?,
    val name: String?,
    val avatar: String?,
    val gender: String?,
    val address: String?,
    val introduce: String?,
    val totalPost: Int = 0
)
