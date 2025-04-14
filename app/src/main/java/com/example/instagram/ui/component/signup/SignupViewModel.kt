package com.example.instagram.ui.component.signup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.SignupResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    // LiveData để lưu trữ kết quả trả về từ Server
    private val _signupResult = MutableLiveData<SignupResponse?>()
    val signResult: LiveData<SignupResponse?> = _signupResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private fun validateInput(username: String, password: String, name: String): Boolean {
        return if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            _errorMessage.postValue("Hãy nhập đầy đủ thông tin")
            false
        } else {
            true
        }
    }

    fun signup(username: String, password: String, name: String) {
        if (!validateInput(username, password, name)) return
        viewModelScope.launch {
                val result = authRepository.signup(username, password, name)
                _signupResult.postValue(result)
        }
    }
}
