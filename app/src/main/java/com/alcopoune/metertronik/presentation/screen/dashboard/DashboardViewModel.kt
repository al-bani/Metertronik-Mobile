package com.alcopoune.metertronik.presentation.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val realtimeRepository: RealtimeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState

    fun connectWebSocket(deviceId: String) {
        Log.d("DashboardViewModel", "Connecting to WebSocket for device: $deviceId")
        _uiState.update { it.copy(isConnected = true, error = null) }
        
        realtimeRepository.getRealtimeData(deviceId)
            .onEach { realtimeData ->
                Log.d("DashboardViewModel", "Received realtime data: $realtimeData")
                _uiState.update { 
                    it.copy(
                        realtimeData = realtimeData,
                        isConnected = true,
                        error = null
                    )
                }
            }
            .catch { exception ->
                Log.e("DashboardViewModel", "WebSocket error", exception)
                _uiState.update { 
                    it.copy(
                        isConnected = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun disconnectWebSocket() {
        Log.d("DashboardViewModel", "Disconnecting WebSocket")
        realtimeRepository.disconnect()
        _uiState.update { it.copy(isConnected = false) }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}