package com.alcopoune.metertronik.presentation.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.DashboardRepository
import com.alcopoune.metertronik.data.repository.RealtimeRepository
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val realtimeRepository: RealtimeRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState
    private val _realtimeData = MutableStateFlow<ElectricityRealtime?>(null)
    val realtimeData: StateFlow<ElectricityRealtime?> = _realtimeData

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    fun connectWebSocket(deviceId: String) {
        Log.d("DashboardVM", "Connecting WebSocket: $deviceId")

        realtimeRepository.getRealtimeData(deviceId)
            .onEach { data ->
                Log.d("DashboardVM", "Realtime received: $data")
                _realtimeData.value = data
                _isConnected.value = true
            }
            .catch { e ->
                Log.e("DashboardVM", "WebSocket error", e)
                _isConnected.value = false
            }
            .launchIn(viewModelScope)
    }

    fun disconnectWebSocket() {
        Log.d("DashboardVM", "Disconnect WebSocket")
        realtimeRepository.disconnect()
        _isConnected.value = false
    }

    fun loadDashboardData(
        deviceId: String,
        date: String
    ) {
        viewModelScope.launch {
            Log.d("DashboardVM", "Load dashboard: device=$deviceId date=$date")
            _uiState.value = DashboardState.Loading

            try {
                val result = dashboardRepository.getDashboard(deviceId, date)
                _uiState.value = DashboardState.Success(result)
            } catch (e: Exception) {
                Log.e("DashboardVM", "Dashboard error", e)
                _uiState.value = DashboardState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}
