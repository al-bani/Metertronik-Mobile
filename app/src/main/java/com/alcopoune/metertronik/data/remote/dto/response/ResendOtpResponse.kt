package com.alcopoune.metertronik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ResendOtpResponse(
    @SerializedName("status_resend") val statusResend: Boolean,
    val message: String
)


