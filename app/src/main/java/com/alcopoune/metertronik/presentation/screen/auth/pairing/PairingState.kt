package com.alcopoune.metertronik.presentation.screen.auth.pairing

data class PairingDevice(
    val name: String,
    val address: String,
    val rssi: Int? = null,
    val deviceId: String? = null,
    val status: PairingStatus = PairingStatus.IDLE,
    val statusMessage: String? = null,
    val isPaired: Boolean = false,
)

data class PairingState(
    val devices: List<PairingDevice> = emptyList(),
    val isScanning: Boolean = false,
    val selectedAddress: String? = null,
    val error: String? = null,
)

enum class PairingStatus {
    IDLE,
    CONNECTING,
    READING_DEVICE_ID,
    PAIRING_API,
    WRITING_TOKEN,
    POLLING,
    CONNECTED,
    FAILED,
}