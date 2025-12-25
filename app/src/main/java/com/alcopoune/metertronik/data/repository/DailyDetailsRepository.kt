package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.DailyDetailsApi
import com.alcopoune.metertronik.data.util.ErrorHandler
import com.alcopoune.metertronik.domain.model.DailyDetailsData
import javax.inject.Inject

class DailyDetailsRepository @Inject constructor(
    private val api: DailyDetailsApi
) {
    private val tag = "DailyDetailsRepository"

    suspend fun getDailyDetail(
        deviceId: String,
        date: String? = null
    ): DailyDetailsData {
        return try {
            Log.d(tag, "getDailyDetail() deviceId=$deviceId, date=$date - calling API")
            val response = api.getDailyDetail(deviceId, date)
            Log.d(tag, "getDailyDetail() success, mapping to domain. hourlySize=${response.data.hourly.size}")
            response.toDomain()
        } catch (e: Exception) {
            Log.e(tag, "Error getting daily detail for deviceId=$deviceId, date=$date", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }
}