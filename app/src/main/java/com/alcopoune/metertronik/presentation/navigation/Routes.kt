package com.alcopoune.metertronik.presentation.navigation

sealed class Routes(val route: String) {
    data object Dashboard : Routes("dashboard")
    data object Detail : Routes("detail/{id}") {
        const val ARG_ID: String = "id"

        fun createRoute(id: String): String = "detail/$id"
    }
    data object Setting : Routes("setting")
    data object ListData : Routes("list_data")
}