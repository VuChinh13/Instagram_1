package com.example.instagram.ui.component.updateinformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.data.model.SignupResponse
import com.example.instagram.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UpdateInformationViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _updateInforResult = MutableLiveData<InforUserResponse?>()
    val updateInforResult: LiveData<InforUserResponse?> = _updateInforResult

    fun updateInformation(
        oldPassword: String?,
        newPassword: String?,
        name: String?,
        avatar: File?,
        gender: String?,
        address: String?,
        introduce: String?,
        userId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // khi mà trả về thì có thể nhận giá trị là null
            val result = authRepository.updateUserInfo(oldPassword, newPassword, name, avatar, gender, address, introduce,userId)
             _updateInforResult.postValue(result)
        }
    }
}
