package com.dicoding.view

import com.dicoding.data.remote.response.ErrorResponse
import com.dicoding.data.remote.response.RegisterResponse

sealed class RegisterResult{
    data class Success(val registerResponse: RegisterResponse) : RegisterResult()
    data class Error(val errorResponse: ErrorResponse) : RegisterResult()
}
