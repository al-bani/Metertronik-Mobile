package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.DashboardApi
import com.alcopoune.metertronik.domain.model.DashboardSummaryData

class DashboardRepository(
    private val api: DashboardApi
) {
    suspend fun getDashboard(
        deviceId: String,
        date: String
    ): DashboardSummaryData {
        val response = api.getDashboard(deviceId, date)
        return response.toDomain()
    }
}