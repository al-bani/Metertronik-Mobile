package com.alcopoune.metertronik.data.remote.websocket

import android.util.Log
import com.alcopoune.metertronik.data.mapper.toDomain
import com.alcopoune.metertronik.data.remote.dto.ElectricityRealtimeDto
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
    private val webSocketBaseUrl: String
) {
    private var webSocket: WebSocket? = null
    private val tag = "WebSocketService"

    fun connect(deviceId: String): Flow<ElectricityRealtime> = callbackFlow {
        val url = "$webSocketBaseUrl$deviceId"
        val request = Request.Builder()
            .url(url)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "WebSocket connected to $url")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    Log.d(tag, "Received message: $text")
                    val dto = gson.fromJson(text, ElectricityRealtimeDto::class.java)
                    val domain = dto.toDomain()
                    trySend(domain)
                } catch (e: Exception) {
                    Log.e(tag, "Error parsing WebSocket message", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "WebSocket failure", t)
                close(t)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "WebSocket closing: $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "WebSocket closed: $reason")
                close()
            }
        }

        webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            Log.d(tag, "Closing WebSocket connection")
            webSocket?.close(1000, "Client closing")
            webSocket = null
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Manual disconnect")
        webSocket = null
    }
}

