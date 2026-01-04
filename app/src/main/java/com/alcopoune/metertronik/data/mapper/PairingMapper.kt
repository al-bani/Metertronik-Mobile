package com.alcopoune.metertronik.data.mapper

import com.alcopoune.metertronik.data.remote.dto.response.PairingResponse
import com.alcopoune.metertronik.domain.model.PairingResult

fun PairingResponse.toDomain() : PairingResult {
    return PairingResult(
        isPaired = isPaired,
        message = message,
        pairingToken = pairingToken
    )
}