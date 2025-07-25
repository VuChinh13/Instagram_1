package com.example.instagram.ui.component.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.Author
import com.example.instagram.data.model.PostResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  Trong class có 2 phương thức là getPost() và getPost1()
 *  phương thức getPost1() dùng để lấy danh sách dữ liệu vì
 *  không có api lấy dữ liệu người dùng
 *  phương thức getPost() là lấy những bài viết
 *
 * -Dùng MediatorLiveData để kết hợp 2 LiveData khi mà cả 2 load xong dữ liệu
 *  thì mới cập nhật giao diện
 *
 */
class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val _getPostResult = MutableLiveData<PostResponse?>()
    val getPostResult: LiveData<PostResponse?> = _getPostResult
    private val _getInforUserResults = MutableLiveData<MutableList<Author>>()
    val getInforUserResults: LiveData<MutableList<Author>> = _getInforUserResults
    public val combinedData = MediatorLiveData<Pair<PostResponse?, MutableList<Author>?>>()

    init {
        combinedData.addSource(getPostResult) { posts ->
            val authors = _getInforUserResults.value
            if (posts != null && authors != null) {
                combinedData.value = posts to authors
            }
        }

        combinedData.addSource(getInforUserResults) { authors ->
            val posts = _getPostResult.value
            if (posts != null && authors != null) {
                combinedData.value = posts to authors
            }
        }
    }


    fun getPost() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalPosts = authRepository.getPost("moi-nhat", 1, 10)?.data?.totalPost ?: 0
            val result = authRepository.getPost("moi-nhat", 1, totalPosts)
            _getPostResult.postValue(result)
        }
    }


    fun getPost1() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalPosts = authRepository.getPost("moi-nhat", 1, 10)?.data?.totalPost ?: 0
            val result = authRepository.getPost("moi-nhat", 1, totalPosts)
            val inforUses = mutableListOf<Author>()
            val usernames = mutableSetOf<String>()

            result?.data?.data?.forEach { item ->
                val author = item.author
                if (author.username !in usernames) {
                    inforUses.add(author)
                    usernames.add(author.username)
                }
            }
            _getInforUserResults.postValue(inforUses)
        }
    }
}
