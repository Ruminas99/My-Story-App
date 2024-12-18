package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.Result

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> = _stories

    val story: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory(){
        _stories.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = repository.getStory()
                _stories.value = Result.Success(response)
            } catch (e: Exception) {
                _stories.value = Result.Error("Error get stories: ${e.message}")
            }
        }
    }

    fun getStoryLocation(){
        _stories.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = repository.getStoryWithLocation()
                _stories.value = Result.Success(response)
            } catch (e: Exception) {
                _stories.value = Result.Error("Error get stories: ${e.message}")
            }
        }
    }
}