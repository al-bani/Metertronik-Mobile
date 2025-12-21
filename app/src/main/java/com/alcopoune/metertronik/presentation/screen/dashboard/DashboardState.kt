package com.alcopoune.metertronik.presentation.screen.dashboard

import com.alcopoune.metertronik.domain.model.DashboardSummaryData

sealed class DashboardState {

    object Loading : DashboardState()

    data class Success(
        val data: DashboardSummaryData
    ) : DashboardState()

    data class Error(
        val message: String
    ) : DashboardState()
}
