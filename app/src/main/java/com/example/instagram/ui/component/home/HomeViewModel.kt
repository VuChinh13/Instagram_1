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
// Trong lớp này cần lấy thông tin người dùng
// Lấy thông tin của tất cả bài viết từ đó hiển thị thông tin người dùng
// Ý tưởng : không cần truyền 2 dữ liệu vào cùng 1 lúc chỉ cần 1 hàm updateInfor bên trong adapter
class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    // LiveData để lưu trữ kết quả trả về từ Server
    private val _getPostResult = MutableLiveData<PostResponse?>()
    val getPostResult: LiveData<PostResponse?> = _getPostResult

    // Lấy thông tin của người dùng
    private val _getInforUserResults = MutableLiveData<MutableList<Author>>()
    val getInforUserResults: LiveData<MutableList<Author>> = _getInforUserResults

    // MediatorLiveData kết hợp bài viết và thông tin người dùng
    public val combinedData = MediatorLiveData<Pair<PostResponse?, MutableList<Author>?>>()

    init {
        // Lắng nghe sự thay đổi từ getPostResult
        combinedData.addSource(getPostResult) { posts ->
            val authors = _getInforUserResults.value
            // Kiểm tra nếu cả dữ liệu bài viết và thông tin người dùng đều có giá trị
            if (posts != null && authors != null) {
                combinedData.value = posts to authors
            }
        }

        // Lắng nghe sự thay đổi từ getInforUserResults
        combinedData.addSource(getInforUserResults) { authors ->
            val posts = _getPostResult.value
            // Kiểm tra nếu cả dữ liệu bài viết và thông tin người dùng đều có giá trị
            if (posts != null && authors != null) {
                combinedData.value = posts to authors
            }
        }
    }

    // Lấy tất cả các bài viết
    fun getPost() {
        viewModelScope.launch(Dispatchers.IO) {
            // Lấy tổng số bài viết để xác định số trang
            val totalPosts = authRepository.getPost("moi-nhat", 1, 10)?.data?.totalPost ?: 0
            // Lấy tất cả bài viết
            val result = authRepository.getPost("moi-nhat", 1, totalPosts)
            _getPostResult.postValue(result) // Cập nhật kết quả bài viết
        }
    }

    // Lấy thông tin người dùng từ các bài viết
    fun getPost1() {
        viewModelScope.launch(Dispatchers.IO) {
            // Lấy tổng số bài viết
            val totalPosts = authRepository.getPost("moi-nhat", 1, 10)?.data?.totalPost ?: 0
            // Lấy tất cả bài viết
            val result = authRepository.getPost("moi-nhat", 1, totalPosts)

            // Lấy thông tin tác giả (User)
            val inforUses = mutableListOf<Author>()
            val usernames = mutableSetOf<String>() // Set để lưu các username đã gặp

            result?.data?.data?.forEach { item ->
                val author = item.author
                if (author.username !in usernames) {
                    inforUses.add(author)
                    usernames.add(author.username) // Thêm username vào Set
                }
            }

            // Sau khi lấy thông tin người dùng, cập nhật _getInforUserResults
            _getInforUserResults.postValue(inforUses)
        }
    }
}
