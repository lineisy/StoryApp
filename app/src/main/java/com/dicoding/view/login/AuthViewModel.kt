package com.dicoding.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.data.UserRepository
import com.dicoding.data.pref.UserModel
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) = viewModelScope.launch {
        repository.saveSession(user)
    }


    fun login(email: String, password: String) = repository.login(email, password)

    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)

}