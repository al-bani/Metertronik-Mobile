package com.alcopoune.metertronik.presentation.screen.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.remote.dto.request.UserLoginRequest
import com.alcopoune.metertronik.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dataStorage: DataStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, username: String, password: String) {
        if (password.isBlank()) {
            _uiState.value = LoginState.Error("Password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                val request = UserLoginRequest(
                    email = email,
                    username = username,
                    password = password
                )
                val result = repository.userLogin(request)
                
                // Save tokens and user_id to DataStore
                dataStorage.saveTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    userId = result.user.id
                )
                
                Log.d("LoginViewModel", "Login successful for user: ${result.user.email}")
                Log.d("LoginViewModel", "Tokens saved successfully")
                _uiState.value = LoginState.Success(result)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error", e)
                _uiState.value = LoginState.Error(e.message ?: "Terjadi kesalahan saat login")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginState.Idle
    }

    suspend fun getStoredDeviceId(): String? {
        return dataStorage.getDeviceId()
    }
}