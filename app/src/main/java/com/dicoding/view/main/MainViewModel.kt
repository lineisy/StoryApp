package com.dicoding.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.data.UserRepository
import com.dicoding.data.pref.UserModel
import com.dicoding.data.remote.response.StoryResponse
import com.dicoding.view.ResultStories
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository,
    private val storyRepository: com.dicoding.data.StoryRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private val _stories = MediatorLiveData<ResultStories<StoryResponse>>()
    val stories: LiveData<ResultStories<StoryResponse>> get() = _stories

    fun getStories() {
        val source = storyRepository.getStories()
        _stories.addSource(source) { result ->
            _stories.value = result
        }
    }


}