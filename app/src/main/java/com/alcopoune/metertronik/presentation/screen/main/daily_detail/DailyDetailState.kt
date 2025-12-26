package com.alcopoune.metertronik.presentation.screen.main.daily_detail

import com.alcopoune.metertronik.domain.model.DailyDetailsData

sealed class DailyDetailState {
    object Loading : DailyDetailState()
    data class Success(val data: DailyDetailsData) : DailyDetailState()
    data class Error(val message: String) : DailyDetailState()
}