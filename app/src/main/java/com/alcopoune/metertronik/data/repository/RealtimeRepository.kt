package com.alcopoune.metertronik.data.repository

import com.alcopoune.metertronik.data.remote.websocket.WebSocketService
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealtimeRepository @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun getRealtimeData(deviceId: String): Flow<ElectricityRealtime> {
        return webSocketService.connect(deviceId)
    }

    fun disconnect() {
        webSocketService.disconnect()
    }
}
