package com.alcopoune.metertronik.presentation.navigation

/**
 * Simple sealed class untuk mendefinisikan rute utama aplikasi.
 * Hanya fokus ke routing UI, tanpa logic data/API.
 */
sealed class AppDestination(val route: String) {
    data object ListData : AppDestination("list_data")
    data object Dashboard : AppDestination("dashboard")
    data object Settings : AppDestination("settings")

    data object DetailsHistoryDay : AppDestination("details_history_day")
    data object DetailsCurrentDay : AppDestination("details_current_day")
}


