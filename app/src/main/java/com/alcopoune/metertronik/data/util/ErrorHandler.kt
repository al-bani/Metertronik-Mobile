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
                    400 -> "Permintaan tidak valid. Silakan coba lagi."
                    401 -> "Anda tidak memiliki izin untuk mengakses data ini."
                    403 -> "Akses ditolak. Silakan hubungi administrator."
                    404 -> "Data tidak ditemukan."
                    500, 502, 503 -> "Server sedang mengalami masalah. Silakan coba lagi nanti."
                    else -> "Terjadi kesalahan pada server. Kode error: ${exception.code()}"
                }
            }
            is SocketTimeoutException -> {
                "Waktu koneksi habis. Periksa koneksi internet Anda dan coba lagi."
            }
            is UnknownHostException -> {
                "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            }
            is IOException -> {
                "Masalah koneksi internet. Pastikan perangkat Anda terhubung ke internet."
            }
            else -> {
                Log.e(TAG, "Unexpected error", exception)
                exception.message ?: "Terjadi kesalahan yang tidak diketahui. Silakan coba lagi."
            }
        }
    }
}

