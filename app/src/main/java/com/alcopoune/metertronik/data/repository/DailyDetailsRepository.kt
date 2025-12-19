package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.DailyDetailsApi
import com.alcopoune.metertronik.domain.model.DailyDetailsData

class DailyDetailsRepository (
    private val api: DailyDetailsApi
) {
    suspend fun getDailyDetail(
        deviceId: String,
        date: String
    ): DailyDetailsData {
        Log.d("DailyDetailsRepository", "getDailyDetail() deviceId=$deviceId, date=$date - calling API")
        val response = api.getDailyDetail(deviceId, date)
        Log.d("DailyDetailsRepository", "getDailyDetail() success, mapping to domain. hourlySize=${response.data.hourly.size}")
        return response.toDomain()
    }
}