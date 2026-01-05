package com.alcopoune.metertronik.presentation.screen.main.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.repository.DashboardRepository
import com.alcopoune.metertronik.data.repository.RealtimeRepository
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    private val dashboardRepository: DashboardRepository,
    private val dataStorage: DataStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading())
    val uiState: StateFlow<DashboardState> = _uiState

    val deviceId: Flow<String?> = dataStorage.deviceId

    private fun updateState(
        dashboardData: DashboardSummaryData? = null,
        realtimeData: ElectricityRealtime? = null,
        isRealtimeConnected: Boolean = false,
        errorMessage: String? = null,
        noDataMessage: String? = null
    ) {
        _uiState.value = when {
            noDataMessage != null -> DashboardState.NoData(
                message = noDataMessage,
                realtimeData = realtimeData,
                isRealtimeConnected = isRealtimeConnected
            )
            errorMessage != null -> DashboardState.Error(
                message = errorMessage,
                realtimeData = realtimeData,
                isRealtimeConnected = isRealtimeConnected
            )
            dashboardData != null -> DashboardState.Success(
                data = dashboardData,
                realtimeData = realtimeData,
                isRealtimeConnected = isRealtimeConnected
            )
            else -> DashboardState.Loading(
                realtimeData = realtimeData,
                isRealtimeConnected = isRealtimeConnected
            )
        }
    }

    fun connectWebSocket(deviceId: String) {
        Log.d("DashboardVM", "Connecting WebSocket: $deviceId")

        realtimeRepository.getRealtimeData(deviceId)
            .onEach { data ->
                Log.d("DashboardVM", "Realtime received: $data")
                val currentState = _uiState.value
                updateState(
                    dashboardData = (currentState as? DashboardState.Success)?.data,
                    realtimeData = data,
                    isRealtimeConnected = true,
                    errorMessage = (currentState as? DashboardState.Error)?.message,
                    noDataMessage = (currentState as? DashboardState.NoData)?.message
                )
            }
            .catch { e ->
                Log.e("DashboardVM", "WebSocket error", e)
                val currentState = _uiState.value
                updateState(
                    dashboardData = (currentState as? DashboardState.Success)?.data,
                    realtimeData = (currentState as? DashboardState.Success)?.realtimeData
                        ?: (currentState as? DashboardState.Error)?.realtimeData
                        ?: (currentState as? DashboardState.NoData)?.realtimeData
                        ?: (currentState as? DashboardState.Loading)?.realtimeData,
                    isRealtimeConnected = false,
                    errorMessage = (currentState as? DashboardState.Error)?.message,
                    noDataMessage = (currentState as? DashboardState.NoData)?.message
                )
            }
            .launchIn(viewModelScope)
    }

    fun disconnectWebSocket() {
        Log.d("DashboardVM", "Disconnect WebSocket")
        realtimeRepository.disconnect()
        val currentState = _uiState.value
        updateState(
            dashboardData = (currentState as? DashboardState.Success)?.data,
            realtimeData = (currentState as? DashboardState.Success)?.realtimeData
                ?: (currentState as? DashboardState.Error)?.realtimeData
                ?: (currentState as? DashboardState.NoData)?.realtimeData
                ?: (currentState as? DashboardState.Loading)?.realtimeData,
            isRealtimeConnected = false,
            errorMessage = (currentState as? DashboardState.Error)?.message,
            noDataMessage = (currentState as? DashboardState.NoData)?.message
        )
    }

    fun loadDashboardData(
        deviceId: String,
        date: String
    ) {
        // Set Loading secara sinkron supaya state Error lama tidak sempat dirender (menghindari "flash" ErrorScreen).
        Log.d("DashboardVM", "Load dashboard: device=$deviceId date=$date")
        val currentState = _uiState.value
        updateState(
            realtimeData = (currentState as? DashboardState.Success)?.realtimeData
                ?: (currentState as? DashboardState.Error)?.realtimeData
                ?: (currentState as? DashboardState.NoData)?.realtimeData
                ?: (currentState as? DashboardState.Loading)?.realtimeData,
            isRealtimeConnected = (currentState as? DashboardState.Success)?.isRealtimeConnected
                ?: (currentState as? DashboardState.Error)?.isRealtimeConnected
                ?: (currentState as? DashboardState.NoData)?.isRealtimeConnected
                ?: (currentState as? DashboardState.Loading)?.isRealtimeConnected ?: false
        )

        viewModelScope.launch {
            try {
                val result = dashboardRepository.getDashboard(deviceId, date)
                val updatedState = _uiState.value
                updateState(
                    dashboardData = result,
                    realtimeData = (updatedState as? DashboardState.Success)?.realtimeData
                        ?: (updatedState as? DashboardState.Error)?.realtimeData
                        ?: (updatedState as? DashboardState.NoData)?.realtimeData
                        ?: (updatedState as? DashboardState.Loading)?.realtimeData,
                    isRealtimeConnected = (updatedState as? DashboardState.Success)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Error)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.NoData)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Loading)?.isRealtimeConnected ?: false
                )
            } catch (e: IllegalStateException) {
                if (e.message?.contains("Data dashboard kosong", ignoreCase = true) != true) {
                    throw e
                }
                val updatedState = _uiState.value
                updateState(
                    realtimeData = (updatedState as? DashboardState.Success)?.realtimeData
                        ?: (updatedState as? DashboardState.Error)?.realtimeData
                        ?: (updatedState as? DashboardState.NoData)?.realtimeData
                        ?: (updatedState as? DashboardState.Loading)?.realtimeData,
                    isRealtimeConnected = (updatedState as? DashboardState.Success)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Error)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.NoData)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Loading)?.isRealtimeConnected ?: false,
                    noDataMessage = "Data will Displaying after 1 day",
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e("DashboardVM", "Dashboard error", e)
                val updatedState = _uiState.value
                updateState(
                    realtimeData = (updatedState as? DashboardState.Success)?.realtimeData
                        ?: (updatedState as? DashboardState.Error)?.realtimeData
                        ?: (updatedState as? DashboardState.NoData)?.realtimeData
                        ?: (updatedState as? DashboardState.Loading)?.realtimeData,
                    isRealtimeConnected = (updatedState as? DashboardState.Success)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Error)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.NoData)?.isRealtimeConnected
                        ?: (updatedState as? DashboardState.Loading)?.isRealtimeConnected ?: false,
                    errorMessage = e.message ?: "Unknown error",
                    noDataMessage = null
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}
