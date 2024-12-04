package com.dicoding.view

sealed class ResultStories<out T> {
    data class Success<out T>(val data: T?) : ResultStories<T>()
    data class Error(val error: String) : ResultStories<Nothing>()
    data object Loading : ResultStories<Nothing>()
}