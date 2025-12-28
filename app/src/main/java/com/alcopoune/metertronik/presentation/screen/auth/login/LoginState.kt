package com.alcopoune.metertronik.presentation.screen.auth.login

import com.alcopoune.metertronik.domain.model.LoginResult

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val result: LoginResult) : LoginState()
    data class Error(val message: String) : LoginState()
}