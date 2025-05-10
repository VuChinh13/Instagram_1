package com.example.instagram.data.repository

import android.util.Log
import com.example.instagram.data.data_source.api.ApiClient
import com.example.instagram.data.data_source.api.ApiClient.apiService
import com.example.instagram.data.model.DeletePostRequest
import com.example.instagram.data.model.DeletePostResponse
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.data.model.LikePostRequest
import com.example.instagram.data.model.LikePostResponse
import com.example.instagram.data.model.LoginRequest
import com.example.instagram.data.model.LoginResponse
import com.example.instagram.data.model.PostResponse
import com.example.instagram.data.model.SignupRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.instagram.data.model.SignupResponse
import okhttp3.MultipartBody
import java.io.File

class AuthRepository {
    suspend fun signup(
        username: String,
        password: String,
        name: String
    ): SignupResponse? {
        val response = ApiClient.apiService.signup(SignupRequest(username, password, name))
        return try {
            if (response.isSuccessful) {
                response.body()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(username: String, password: String): LoginResponse? {
        return try {
            val response = ApiClient.apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                if (response.body()?.status == true) {
                    return response.body()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPost(sort: String, page: Int, perPage: Int): PostResponse? {
        return try {
            val response = ApiClient.apiService.getPost(sort, page, perPage)
            if (response.isSuccessful) {
                if (response.body()?.status == true) {
                    return response.body()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getInforUser(username: String): InforUserResponse? {
        return try {
            val response = ApiClient.apiService.getInforUser(username)
            if (response.isSuccessful) {
                if (response.body()?.status == true) {
                    return response.body()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserInfo(
        oldPassword: String?,
        newPassword: String?,
        name: String?,
        avatar: File?, // Ảnh có thể null nếu không có thay đổi
        gender: String?,
        address: String?,
        introduce: String?,
        userId: String
    ): InforUserResponse? {
        return try {
            val avatarPart = avatar?.let {
                val mimeType = "image/jpeg" 
                val requestFile = it.asRequestBody(mimeType.toMediaTypeOrNull())

                // Đảm bảo tên file có phần mở rộng hợp lệ
                MultipartBody.Part.createFormData("avatar", it.name, requestFile)
            }
            val response = ApiClient.apiService.updateUserInfo(
                oldPassword?.toRequestBody("text/plain".toMediaTypeOrNull()),
                newPassword?.toRequestBody("text/plain".toMediaTypeOrNull()),
                name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                avatarPart, // Gửi null nếu không có ảnh
                gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                address?.toRequestBody("text/plain".toMediaTypeOrNull()),
                introduce?.toRequestBody("text/plain".toMediaTypeOrNull()),
                userId.toRequestBody("text/plain".toMediaTypeOrNull())
            )
            if (response.isSuccessful && response.body()?.status == true) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addPost(
        userId: String,
        content: String,
        images: List<File>?
    ): PostResponse? {
        return try {
          
            val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = images?.map { file ->
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            } ?: emptyList()

            // Gọi API
            val response = apiService.addPost(userIdBody, contentBody, imageParts)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserPosts(
        username: String,
        sort: String = "moi-nhat",
        page: Int = 1,
        perPage: Int = 10
    ): PostResponse? {
        return try {
            // Gọi API
            val response = apiService.getUserPosts(username, sort, page, perPage)
            if (response.isSuccessful) {
                if (response.body()!!.status) {
                    response.body()
                } else null

            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deletePost(userId: String, postId: String): DeletePostResponse? {
        return try {
            val request = DeletePostRequest(userId, postId)
            val response = apiService.deletePost(request)
            if (response.isSuccessful && response.body()?.status == true) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updatePost(
        userId: String,
        postId: String,
        images: List<File>?,
        content: String?
    ): PostResponse? {
        return try {
            // Chuyển userId & content thành RequestBody
            val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
            val postIdBody = postId.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content?.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageParts = images?.map { file ->
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            } ?: emptyList()
            val response = apiService.updatePost(userIdBody,postIdBody,imageParts,contentBody)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun likePost(userId: String, postId: String, likeValue: Int): LikePostResponse? {
        return try {
            val response = ApiClient.apiService.likePost(LikePostRequest(userId,postId,likeValue))
            if (response.isSuccessful) {
                if (response.body()?.status == true) {
                    return response.body()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
