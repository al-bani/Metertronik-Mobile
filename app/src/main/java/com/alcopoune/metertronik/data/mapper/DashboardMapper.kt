package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.MonthlyDto
import com.alcopoune.metertronik.data.remote.dto.response.DashboardResponse
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.MonthlyData

fun DashboardResponse.toDomain(): DashboardSummaryData {
    val wrapper = data ?: throw IllegalStateException("Data dashboard kosong (data=null)")
    val month = wrapper.month ?: throw IllegalStateException("Data dashboard kosong (month=null)")

    return DashboardSummaryData(
        daily = wrapper.daily.orEmpty().map { it.toDomain() },
        monthly = month.toDomain(),
        monthlyList = wrapper.monthly.orEmpty().map { it.toDomain() }
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
