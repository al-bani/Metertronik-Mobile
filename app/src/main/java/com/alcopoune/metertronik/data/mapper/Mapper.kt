package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.DailyDetailResponse
import com.alcopoune.metertronik.data.remote.dto.DashboardResponse
import com.alcopoune.metertronik.data.remote.dto.ElectricityRealtimeDto
import com.alcopoune.metertronik.domain.model.DailyData
import com.alcopoune.metertronik.domain.model.DailyDetailsData
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import com.alcopoune.metertronik.domain.model.HourlyData
import com.alcopoune.metertronik.domain.model.MonthlyData

fun DailyDetailResponse.toDomain(): DailyDetailsData{
    return DailyDetailsData(
        daily = DailyData(
            deviceId = data.daily.deviceId,
            energy = data.daily.energy,
            totalCost = data.daily.totalCost,
            avgVoltage = data.daily.avgVoltage,
            avgCurrent = data.daily.avgCurrent,
            avgPower = data.daily.avgPower,
            minPower = data.daily.minPower,
            maxPower = data.daily.maxPower,
            day = data.daily.day
        ),
        hourly = data.hourly.map {
            HourlyData(
                deviceId = it.deviceId,
                energy = it.energy,
                totalCost = it.totalCost,
                avgVoltage = it.avgVoltage,
                avgCurrent = it.avgCurrent,
                avgPower = it.avgPower,
                minPower = it.minPower,
                maxPower = it.maxPower,
                ts = it.ts
            )
        }
    )
}

fun ElectricityRealtimeDto.toDomain(): ElectricityRealtime {
    return ElectricityRealtime(
        id = id,
        deviceId = deviceId,
        voltage = voltage,
        current = current,
        power = power,
        energy = energy,
        powerFactor = powerFactor,
        frequency = frequency,
        powerSurge = powerSurge,
        powerSurgePercentage = powerSurgePercentage,
        createdAt = createdAt
    )
}

fun DashboardResponse.toDomain(): DashboardSummaryData {
    return DashboardSummaryData(
        daily = data.daily.map{
            DailyData(
                deviceId = it.deviceId,
                energy = it.energy,
                totalCost = it.totalCost,
                avgVoltage = it.avgVoltage,
                avgCurrent = it.avgCurrent,
                avgPower = it.avgPower,
                minPower = it.minPower,
                maxPower = it.maxPower,
                day = it.day
            )
        },
        monthly = MonthlyData(
            deviceId = data.month.deviceId,
            energy = data.month.energy,
            totalCost = data.month.totalCost,
            month = data.month.month
        ),
        monthlyList = data.monthly.map {
            MonthlyData(
                deviceId = it.deviceId,
                energy = it.energy,
                totalCost = it.totalCost,
                month = it.month
            )
        }
    )
}