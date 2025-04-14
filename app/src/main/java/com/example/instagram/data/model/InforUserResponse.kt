package com.example.instagram.data.model

data class InforUserResponse (
    val status: Boolean,
    val data: User,
    val message: String?
)