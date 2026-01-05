package com.alcopoune.metertronik.presentation.screen.main.dashboard

import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.ElectricityRealtime

sealed class DashboardState {

    data class Loading(
        val realtimeData: ElectricityRealtime? = null,
        val isRealtimeConnected: Boolean = false
    ) : DashboardState()

    data class NoData(
        val message: String = "Data will Displaying after 1 day",
        val realtimeData: ElectricityRealtime? = null,
        val isRealtimeConnected: Boolean = false
    ) : DashboardState()

    data class Success(
        val data: DashboardSummaryData,
        val realtimeData: ElectricityRealtime? = null,
        val isRealtimeConnected: Boolean = false
    ) : DashboardState()

    data class Error(
        val message: String,
        val realtimeData: ElectricityRealtime? = null,
        val isRealtimeConnected: Boolean = false
    ) : DashboardState()
}

// Extension functions untuk mengakses realtime data dari state
val DashboardState.realtimeData: ElectricityRealtime?
    get() = when (this) {
        is DashboardState.Loading -> this.realtimeData
        is DashboardState.NoData -> this.realtimeData
        is DashboardState.Success -> this.realtimeData
        is DashboardState.Error -> this.realtimeData
    }

val DashboardState.isRealtimeConnected: Boolean
    get() = when (this) {
        is DashboardState.Loading -> this.isRealtimeConnected
        is DashboardState.NoData -> this.isRealtimeConnected
        is DashboardState.Success -> this.isRealtimeConnected
        is DashboardState.Error -> this.isRealtimeConnected
    }
