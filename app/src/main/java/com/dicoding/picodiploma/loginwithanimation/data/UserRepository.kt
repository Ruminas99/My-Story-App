package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import com.dicoding.picodiploma.loginwithanimation.Result
import com.dicoding.picodiploma.loginwithanimation.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(private val userPreference: UserPreference,
                                         private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        val user = UserModel(
            email = email,
            name = response.loginResult?.name ?: "",
            token = response.loginResult?.token ?: "",
            isLogin = true
        )
        saveSession(user)
        return response
    }

    fun isTokenAvailable(): Flow<Boolean> {
        return userPreference.isTokenAvailable()
    }

    suspend fun getStory(): List<ListStoryItem> {
        return try {
            val response = apiService.getStories()
            response.listStory?.filterNotNull() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getDetail(id: String): Result<DetailResponse> {
        return try {
            val response = apiService.getDetail(id)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
    ): AddStoryResponse {
        return apiService.uploadImage(file, description)
    }
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}