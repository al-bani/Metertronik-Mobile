package com.alcopoune.metertronik.presentation.screen.main.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val uiState: StateFlow<SettingsState> = _uiState

    fun logout() {
        viewModelScope.launch {
            _uiState.value = SettingsState.Loading
            try {
                val success = authRepository.userLogout()
                if (success) {
                    Log.d("SettingsViewModel", "Logout successful")
                    _uiState.value = SettingsState.Success
                } else {
                    Log.e("SettingsViewModel", "Logout failed")
                    _uiState.value = SettingsState.Error("Gagal melakukan logout")
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error during logout", e)
                _uiState.value = SettingsState.Error(e.message ?: "Terjadi kesalahan saat logout")
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsState.Idle
    }
}