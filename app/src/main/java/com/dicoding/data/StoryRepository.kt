package com.dicoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.data.remote.response.ListStoryItem
import com.dicoding.data.remote.response.StoryResponse
import com.dicoding.data.remote.service.ApiService
import com.dicoding.view.ResultStories
import com.dicoding.data.remote.response.ErrorResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(private val apiService: ApiService) {



    fun getStories(): LiveData<ResultStories<StoryResponse>> = liveData {
        emit(ResultStories.Loading)
        try {
            val res = apiService.getStories()
            emit(ResultStories.Success(res))
        } catch (e: Exception) {
            emit(ResultStories.Error("Error: ${e.message}"))
        }
    }

    suspend fun getDetail(id: String): ListStoryItem {
        return apiService.getDetailStory(id).story
    }

    fun postStory(description: String, photoFile: File) = liveData {
        emit(ResultStories.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )

        try {
            val successResponse = apiService.postStories(requestBody, multipartBody)
            emit(ResultStories.Success(successResponse))
        }catch (e: Exception){
            val errorBody = e.message.toString()
            val errorResponse = ErrorResponse(true, errorBody)
            emit(errorResponse.message?.let { ResultStories.Error(it)})
        }
    }
}