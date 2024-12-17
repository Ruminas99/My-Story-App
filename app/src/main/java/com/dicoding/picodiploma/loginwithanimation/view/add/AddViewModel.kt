package com.dicoding.picodiploma.loginwithanimation.view.add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.dicoding.picodiploma.loginwithanimation.Result

class AddViewModel(private val repository: UserRepository
) : ViewModel() {
    private val _uploadResult = MutableLiveData<Result<AddStoryResponse>>()
    val uploadResult: LiveData<Result<AddStoryResponse>> get() = _uploadResult

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun uploadStory(
        file: MultipartBody.Part,
        description: String,
    ) {
        viewModelScope.launch {
            handleLoading {
                val response = repository.uploadStory(
                    file,
                    description.toRequestBody("text/plain".toMediaTypeOrNull()),
                )
                _uploadResult.postValue(Result.Success(response))
            }
        }
    }

    private suspend fun handleLoading(block: suspend () -> Unit) {
        _uploadResult.postValue(Result.Loading)
        try {
            block()
        } catch (e: Exception) {
            _uploadResult.postValue(Result.Error(e.message ?: "Unknown error"))
        }
    }

}