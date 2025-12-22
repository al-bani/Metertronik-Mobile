package com.alcopoune.metertronik.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Routes(val route: String) {
    data object Dashboard : Routes("dashboard")
    data object Detail : Routes("detail/{id}") {
        const val ARG_ID: String = "id"

        fun createRoute(id: String): String = "detail/$id"
    }
    data object Setting : Routes("setting")
    data object ListData : Routes("list_data")
    data object Error : Routes("error/{message}") {
        const val ARG_MESSAGE: String = "message"

        fun createRoute(message: String): String {
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            return "error/$encodedMessage"
        }
    }
}