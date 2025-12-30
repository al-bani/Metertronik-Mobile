package com.alcopoune.metertronik.presentation.screen.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.remote.dto.request.UserRegisterRequest
import com.alcopoune.metertronik.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val uiState: StateFlow<RegisterState> = _uiState

    private val _checkIdState = MutableStateFlow<CheckIdState>(CheckIdState.Idle)
    val checkIdState: StateFlow<CheckIdState> = _checkIdState.asStateFlow()

    fun checkId(userId: String) {
        if (userId.isBlank()) {
            _checkIdState.value = CheckIdState.Error("ID tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _checkIdState.value = CheckIdState.Loading
            try {
                val response = repository.checkIdAvailable(userId)
                _checkIdState.value = CheckIdState.Result(
                    available = response.available,
                    message = response.message
                )
            } catch (e: Exception) {
                _checkIdState.value = CheckIdState.Error(
                    e.message ?: "Terjadi kesalahan saat check ID"
                )
            }
        }
    }

    fun register(email: String, username: String, password: String, confirmPassword: String) {
        if (email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = RegisterState.Error("Semua field wajib diisi")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = RegisterState.Error("Konfirmasi password tidak sama")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterState.Loading
            try {
                val request = UserRegisterRequest(
                    email = email,
                    username = username,
                    password = password
                )
                val result = repository.userRegister(request)
                Log.d("RegisterViewModel", "Register success for user: ${result.email}, status=${result.status}")
                _uiState.value = RegisterState.Success(result)
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Register error", e)
                _uiState.value = RegisterState.Error(e.message ?: "Terjadi kesalahan saat register")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterState.Idle
    }

    fun resetCheckIdState() {
        _checkIdState.value = CheckIdState.Idle
    }
}

sealed class CheckIdState {
    data object Idle : CheckIdState()
    data object Loading : CheckIdState()
    data class Result(val available: Boolean, val message: String) : CheckIdState()
    data class Error(val message: String) : CheckIdState()
}