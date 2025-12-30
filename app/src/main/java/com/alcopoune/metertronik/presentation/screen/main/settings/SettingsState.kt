package com.alcopoune.metertronik.presentation.screen.main.settings

sealed class SettingsState {
    data object Idle : SettingsState()
    data object Loading : SettingsState()
    data object Success : SettingsState()
    data class Error(val message: String) : SettingsState()
}