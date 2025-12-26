package com.alcopoune.metertronik.presentation.screen.main.daily_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.DailyDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyDetailViewModel @Inject constructor(
    private val repository: DailyDetailsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DailyDetailState>(DailyDetailState.Loading)
    val uiState: StateFlow<DailyDetailState> = _uiState

    fun load(deviceId: String, date: String) {
        Log.d("DailyDetailViewModel", "load() called with deviceId=$deviceId, date=$date")
        viewModelScope.launch {
            _uiState.value = DailyDetailState.Loading
            try {
                Log.d("DailyDetailViewModel", "Requesting repository.getDailyDetail...")
                val result = repository.getDailyDetail(deviceId, date)
                Log.d("DailyDetailViewModel", "Repository success. daily=${result.daily}, hourlySize=${result.hourly.size}")
                _uiState.value = DailyDetailState.Success(result)
            } catch (e: Exception) {
                Log.e("DailyDetailViewModel", "Error while loading daily detail", e)
                _uiState.value = DailyDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
}