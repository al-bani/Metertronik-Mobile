package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PairingResponse(
    val message: String?,
    @SerializedName("is_paired") val isPaired: Boolean,
    @SerializedName("pairing_token") val pairingToken: String?
)