package com.alcopoune.metertronik.presentation.screen.auth.verify

sealed class VerifyState {
    data object Idle : VerifyState()
    data object Verifying : VerifyState()
    data object Resending : VerifyState()
    data object Success : VerifyState()
    data class Error(val message: String) : VerifyState()
}