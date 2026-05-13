package com.kutira.kushala.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kushala.model.User
import com.kutira.kushala.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _userState = MutableLiveData<Result<User>>()
    val userState: LiveData<Result<User>> = _userState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun register(user: User, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(user, password)
            _userState.value = result
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _userState.value = result
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
    }
}