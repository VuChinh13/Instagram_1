package com.example.instagram.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Trạng thái phản hồi chính từ API
data class PostResponse(
    val status: Boolean,
    val data: PostData
)

// Dữ liệu chứa danh sách bài đăng
data class PostData(
    val page: Int,
    val totalPage: Int,
    val totalPost: Int,
    val data: MutableList<Post>
)

// Thông tin bài đăng
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

// Thông tin tác giả bài đăng
data class Author(
    val username: String,
    val name: String,
    val gender: String,
    val avatar: String,
    val address: String,
    val introduce: String
)

// Người đã thích bài viế
data class UserLike(
    val username: String,
    val name: String,
    val gender: String?,
    val avatar: String?,
    val address: String?,
    val introduce: String?
)

