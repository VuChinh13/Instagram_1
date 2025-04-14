package com.example.instagram.ui.component.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.LoginResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    private fun validateInput(username: String, password: String): Boolean {
        return if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("Hãy nhập đầy đủ thông tin")
            false
        } else {
            true
        }
    }

    fun login(username: String, password: String) {
        if (!validateInput(username, password)) return
        viewModelScope.launch(Dispatchers.IO) {
                val result = authRepository.login(username, password)
                _loginResult.postValue(result)
        }
    }
}