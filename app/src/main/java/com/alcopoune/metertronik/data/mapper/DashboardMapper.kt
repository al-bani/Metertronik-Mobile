package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.MonthlyDto
import com.alcopoune.metertronik.data.remote.dto.response.DashboardResponse
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.MonthlyData

fun DashboardResponse.toDomain(): DashboardSummaryData {
    return DashboardSummaryData(
        daily = data.daily.map { it.toDomain() },
        monthly = data.month.toDomain(),
        monthlyList = data.monthly.map { it.toDomain() }
    )
}

fun MonthlyDto.toDomain(): MonthlyData {
    return MonthlyData(
        deviceId = deviceId,
        energy = energy,
        totalCost = totalCost,
        month = month
    )
}
