package com.alcopoune.metertronik.presentation.screen.auth.register

import com.alcopoune.metertronik.domain.model.RegisterResult

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data class Success(val result: RegisterResult) : RegisterState()
    data class Error(val message: String) : RegisterState()
}