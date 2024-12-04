package com.dicoding.di

import android.content.Context
import com.dicoding.data.UserRepository
import com.dicoding.data.pref.UserPreference
import com.dicoding.data.pref.dataStore
import com.dicoding.data.remote.service.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val api = ApiConfig(pref, context).apiService
        return UserRepository.getInstance(pref, api)
    }

    fun provideStoryRepository(context: Context): com.dicoding.data.StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val api = ApiConfig(pref, context).apiService
        return com.dicoding.data.StoryRepository(api)
    }
}