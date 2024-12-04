package com.dicoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.data.pref.UserModel
import com.dicoding.data.pref.UserPreference
import com.dicoding.data.remote.service.ApiService
import com.dicoding.data.remote.response.LoginResponse
import com.dicoding.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import com.dicoding.utils.Result

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService : ApiService,
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

    fun register(name : String, email : String, password : String) : LiveData<Result<RegisterResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.register(name, email, password)

            if(!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }
        } catch (e : HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, RegisterResponse::class.java)
                emit(Result.Success(parseError))
            } catch (exception : Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        try {
            emit(Result.Loading)
            val res = apiService.login(email, password)

            if (!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }

        } catch (e : HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parseError = gson.fromJson(errorRes, LoginResponse::class.java)
                emit(Result.Success(parseError))
            } catch (exception : Exception) {
                emit(Result.Error("Error parsing exception response"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference,apiService)
            }.also { instance = it }
    }
}