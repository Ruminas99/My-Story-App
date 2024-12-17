package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> get() = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                _registerResult.postValue(response.message ?: "Resgistrasi Sukses")
            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java).message ?: "An error occurred"
                } ?: "An error occurred"
                _registerResult.postValue(errorMessage)
            } catch (e: Exception) {
                _registerResult.postValue("An unexpected error occurred")
            }
        }
    }
}