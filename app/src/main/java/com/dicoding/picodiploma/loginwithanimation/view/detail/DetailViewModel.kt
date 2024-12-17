package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.response.DetailResponse
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.Result

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyDetail = MutableLiveData<Result<DetailResponse>>()
    val storyDetail: LiveData<Result<DetailResponse>> = _storyDetail

    fun fetchStoryDetail(id: String) {
        _storyDetail.value = Result.Loading
        viewModelScope.launch {
            try {
                val result = repository.getDetail(id)
                _storyDetail.value = result
            } catch (e: Exception) {
                _storyDetail.value = Result.Error("Error fetching story detail: ${e.message}")
            }
        }
    }
}