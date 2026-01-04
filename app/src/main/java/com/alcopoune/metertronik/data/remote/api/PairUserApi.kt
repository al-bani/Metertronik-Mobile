package com.alcopoune.metertronik.data.remote.api

import com.alcopoune.metertronik.data.remote.dto.request.UserPairingRequest
import com.alcopoune.metertronik.data.remote.dto.response.PairingResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

interface PairUserApi {
    @POST("user/pairing")
    suspend fun pairingUser(
        @Body request : UserPairingRequest
    ) : PairingResponse
}