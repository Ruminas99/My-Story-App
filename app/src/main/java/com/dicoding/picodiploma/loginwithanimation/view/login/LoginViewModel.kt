package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                response.loginResult?.let { loginResult ->
                    saveSession(UserModel(email, loginResult.name ?: "", loginResult.token ?: "", isLogin = true))
                }
                _loginResult.postValue(response.message ?: "Login Sukses")
            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java).message ?: "An error occurred"
                } ?: "An error occurred"
                _loginResult.postValue(errorMessage)
            } catch (e: Exception) {
                _loginResult.postValue(e.toString())
            }
        }
    }
}