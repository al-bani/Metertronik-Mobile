package com.alcopoune.metertronik.presentation.screen.dashboard

import com.alcopoune.metertronik.domain.model.ElectricityRealtime

data class DashboardState(
    val realtimeData: ElectricityRealtime? = null,
    val isConnected: Boolean = false,
    val error: String? = null
)