package com.alcopoune.metertronik.presentation.screen.list_data

import com.alcopoune.metertronik.domain.model.DailyData

sealed class ListDataState {
    object Loading : ListDataState()
    data class Success(
        val data: List<DailyData>,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
    ) : ListDataState()
    data class Error(
        val message: String,
        val cachedData: List<DailyData> = emptyList()
    ) : ListDataState()
}