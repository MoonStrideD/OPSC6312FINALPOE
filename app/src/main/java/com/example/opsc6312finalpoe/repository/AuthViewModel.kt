package com.example.opsc6312finalpoe.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmail(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        role: String = "tenant"
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.registerWithEmail(email, password, firstName, lastName, phoneNumber, role)
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun loginWithMicrosoft() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithMicrosoft()
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Microsoft login failed")
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}