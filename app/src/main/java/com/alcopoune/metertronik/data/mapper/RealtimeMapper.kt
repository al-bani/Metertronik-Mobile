package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.ElectricityRealtimeDto
import com.alcopoune.metertronik.domain.model.ElectricityRealtime

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
