package com.alcopoune.metertronik.domain.model

data class PairingResult(
    val isPaired: Boolean,
    val message: String?,
    val pairingToken: String?
)