package com.alcopoune.metertronik.data.repository

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.ListDataApi
import com.alcopoune.metertronik.data.util.ErrorHandler
import com.alcopoune.metertronik.domain.model.ListDataSummary
import javax.inject.Inject

class ListDataRepository @Inject constructor(
    private val api: ListDataApi
    ) {
    private val tag = "ListDataRepository"

    suspend fun getListData(
        deviceId: String,
        last: String? = null,
        time: String? = null,
        tariff: String? = null,
    ): ListDataSummary {
        return try {
            val response = api.getListData(
            deviceId = deviceId,last = last, time = time, tariff = tariff,
            )
            response.toDomain()
        } catch (e: Exception) {
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }

    suspend fun getRangeListData(
        deviceId: String,
        last: String? = null,
        start: String,
        end: String,
    ): ListDataSummary {
        return try {
            val response = api.getRangeListData(deviceId = deviceId, last = last, start = start, end = end)
            response.toDomain()
        } catch (e: Exception) {
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }
}