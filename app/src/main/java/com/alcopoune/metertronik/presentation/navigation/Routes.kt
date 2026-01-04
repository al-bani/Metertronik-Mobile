package com.alcopoune.metertronik.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Verify : Routes("verify")
    data object Pairing : Routes("pairing")
    data object Dashboard : Routes("dashboard")
    data object Detail : Routes("detail/{date}") {
        const val ARG_DATE: String = "date"

        fun createRoute(date: String): String = "detail/$date"
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