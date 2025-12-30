package com.alcopoune.metertronik.presentation.screen.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val uiState: StateFlow<VerifyState> = _uiState.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(180)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private var timerJob: Job? = null
    private var initialOtpTriggered: Boolean = false

    init {
        startTimer()
    }

    fun startTimer(totalSeconds: Int = 180) {
        timerJob?.cancel()
        _remainingSeconds.value = totalSeconds
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value = _remainingSeconds.value - 1
            }
        }
    }

    fun resendOtp(email: String) {
        resendOtp(email = email, force = false)
    }

    fun resendOtp(email: String, force: Boolean) {
        if (email.isBlank()) {
            _uiState.value = VerifyState.Error("Email tidak valid")
            return
        }
        if (!force && _remainingSeconds.value > 0) return

        viewModelScope.launch {
            _uiState.value = VerifyState.Resending
            try {
                val response = repository.resendOtp(email)
                if (response.statusResend) {
                    startTimer(180)
                    _uiState.value = VerifyState.Idle
                } else {
                    _uiState.value = VerifyState.Error(response.message.ifBlank { "Resend OTP gagal" })
                }
            } catch (e: Exception) {
                _uiState.value = VerifyState.Error(e.message ?: "Terjadi kesalahan saat resend OTP")
            }
        }
    }

    fun triggerInitialOtpIfNeeded(email: String, enabled: Boolean) {
        if (!enabled) return
        if (initialOtpTriggered) return
        initialOtpTriggered = true
        resendOtp(email = email, force = true)
    }

    fun verifyOtp(email: String, otp: String) {
        if (email.isBlank()) {
            _uiState.value = VerifyState.Error("Email tidak valid")
            return
        }
        if (otp.length != 6) return
        if (_uiState.value is VerifyState.Verifying) return

        viewModelScope.launch {
            _uiState.value = VerifyState.Verifying
            try {
                val verifyResp = repository.verifyOtp(email = email, otp = otp)
                if (!verifyResp.statusVerify) {
                    _uiState.value = VerifyState.Error(verifyResp.message.ifBlank { "Kode OTP salah / tidak valid" })
                    return@launch
                }
                _uiState.value = VerifyState.Success
            } catch (e: Exception) {
                _uiState.value = VerifyState.Error(e.message ?: "Terjadi kesalahan saat verifikasi")
            }
        }
    }

    fun clearError() {
        if (_uiState.value is VerifyState.Error) {
            _uiState.value = VerifyState.Idle
        }
    }
}