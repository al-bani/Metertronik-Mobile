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
    ): ListDataSummary {
        return try {
            val response = api.getListData(deviceId, last)
            response.toDomain()
        } catch (e: Exception) {
            val errorMessage = ErrorHandler.getErrorMessage(e)
            throw Exception(errorMessage, e)
        }
    }
}