package com.alcopoune.metertronik.data.util

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    private const val TAG = "ErrorHandler"

    /**
     * Mengkonversi exception menjadi pesan error yang user-friendly
     */
    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "The request could not be processed due to invalid or malformed input. "
                    401 -> "Authentication failed. An unknown error occurred while validating credentials. "
                    403 -> "Access to the requested resource is denied due to an unknown permission error. "
                    404 -> "The requested resource could not be found. "
                    500, 502, 503 -> "An unknown and unexpected error occurred while processing the request. "
                    else -> "Something Went Wrong, Error Code : ${exception.code()}"
                }
            }
            is SocketTimeoutException -> {
                "The connection request timed out while attempting to reach the server. Please check your network connection and try again."
            }
            is UnknownHostException -> {
                "Unable to connect to the server at this time. The server may be temporarily unavailable or unreachable. Please try again later."
            }
            is IOException -> {
                "A network connection issue was detected. Please ensure that your internet connection is stable and try again."
            }
            else -> {
                Log.e(TAG, "Unexpected error", exception)
                exception.message ?: "An unknown error occurred while performing the requested operation. "
            }
        }
    }
}

