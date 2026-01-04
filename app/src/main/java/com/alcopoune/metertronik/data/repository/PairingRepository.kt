package com.alcopoune.metertronik.data.repository

import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.api.PairUserApi
import com.alcopoune.metertronik.data.remote.api.PairingStatusApi
import com.alcopoune.metertronik.data.remote.dto.request.UserPairingRequest
import com.alcopoune.metertronik.domain.model.PairingResult
import javax.inject.Inject

class PairingRepository @Inject constructor(
    private val pairUserApi: PairUserApi,
    private val pairingStatusApi: PairingStatusApi
) {
    suspend fun pairUser(deviceId: String): PairingResult {
        val response = pairUserApi.pairingUser(
            UserPairingRequest(deviceId = deviceId)
        )
        return response.toDomain()
    }

    suspend fun getPairingStatus(deviceId: String): PairingResult {
        return pairingStatusApi.getPairingStatus(deviceId).toDomain()
    }
}