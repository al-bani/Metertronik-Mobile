package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.DashboardApi
import com.alcopoune.metertronik.data.util.ErrorHandler
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val api: DashboardApi
) {
    private val tag = "DashboardRepository"

    suspend fun getDashboard(
        deviceId: String,
        date: String
    ): DashboardSummaryData {
        return try {
            Log.d(tag, "getDashboard() deviceId=$deviceId, date=$date - calling API")
            val response = api.getDashboard(deviceId, date)
                ?: throw IllegalStateException("Data dashboard kosong dari server (response=null)")
            Log.d(tag, "getDashboard() success, mapping to domain")
            response.toDomain()
        } catch (e: IllegalStateException) {
            // "Data kosong" bukan error fatal, jangan lewat ErrorHandler (biar UI bisa menampilkan NoData).
            if (e.message?.contains("Data dashboard kosong", ignoreCase = true) == true) {
                Log.d(tag, "No data dashboard: ${e.message}")
                throw e
            }
            Log.e(tag, "IllegalState error getting dashboard for deviceId=$deviceId, date=$date", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        } catch (e: Exception) {
            Log.e(tag, "Error getting dashboard for deviceId=$deviceId, date=$date", e)
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }
}