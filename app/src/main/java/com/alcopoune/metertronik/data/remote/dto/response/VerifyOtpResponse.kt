package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("status_verify") val statusVerify: Boolean,
    val message: String
)


