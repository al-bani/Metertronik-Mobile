package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.response.PairingResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PairingStatusApi {
    @GET("status/{device_id}/pairing")
    suspend fun getPairingStatus(
        @Path("device_id") deviceId: String
    ): PairingResponse
}

