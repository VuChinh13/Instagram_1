package com.example.instagram.ui.component.addpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.data.model.Post
import com.example.instagram.data.model.PostResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.io.File

class AddPostViewModel : ViewModel(){
    private val authRepository = AuthRepository()
    private val _getPostResponse = MutableLiveData<PostResponse?>()
    val getPostResponse: LiveData<PostResponse?> = _getPostResponse

    fun addPost(
        userId: String,
        content: String,
        images: List<File>?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.addPost(userId, content, images)
            _getPostResponse.postValue(result) 
        }
    }

}
