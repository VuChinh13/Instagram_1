package com.example.instagram.data.model

data class PostResponse(
    val status: Boolean,
    val data: PostData
)


data class PostData(
    val page: Int,
    val totalPage: Int,
    val totalPost: Int,
    val data: MutableList<Post>
)


data class Post(
    val _id: String,
    val author: Author,
    val images: List<String>,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val listLike: List<UserLike>,
    var totalLike: Int
)


data class Author(
    val username: String,
    val name: String,
    val gender: String,
    val avatar: String,
    val address: String,
    val introduce: String
)


data class UserLike(
    val username: String,
    val name: String,
    val gender: String?,
    val avatar: String?,
    val address: String?,
    val introduce: String?
)

