package com.example.instagram.ui.component.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.Post
import com.example.instagram.data.model.PostResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// Trong lớp này cần lấy thông tin người dùng
// Lấy thông tin của tất cả bài viết từ đó hiển thị thông tin người dùng
// Ý tưởng : không cần truyền 2 dữ liệu vào cùng 1 lúc chỉ cần 1 hàm updateInfor bên trong adapter
class HomeViewModel : ViewModel(){
    private val authRepository = AuthRepository()

    // LiveData để lưu trữ kết quả trả về từ Server
    private val _getPostResult = MutableLiveData<PostResponse?>()
    val getPostResult: LiveData<PostResponse?> = _getPostResult


    fun getPost() {
        viewModelScope.launch(Dispatchers.IO) {
            // phái biết tống số bài viết là bao nhiêu , lấy được bằng
            val totalPosts = authRepository.getPost("moi-nhat",1,10)?.data?.totalPost ?: 0 // lấy tổng bài viết
            val result = authRepository.getPost("moi-nhat",1,totalPosts) // sau khi mà lấy được tổng bài viết thì lấy tất cả bài viết
            _getPostResult.postValue(result)
        }
    }
}