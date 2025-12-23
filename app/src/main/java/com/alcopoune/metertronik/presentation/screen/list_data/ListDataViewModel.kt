package com.alcopoune.metertronik.presentation.screen.list_data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.repository.ListDataRepository
import com.alcopoune.metertronik.domain.model.DailyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDataViewModel @Inject constructor(
    private val repository: ListDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListDataState>(ListDataState.Loading)
    val uiState: StateFlow<ListDataState> = _uiState

    private var deviceId: String? = null
    private var lastDate: String? = null
    private var hasMore: Boolean = true
    private var isLoadingPage: Boolean = false

    private val items: MutableList<DailyData> = mutableListOf()

    fun load(deviceId: String) {
        if (this.deviceId == deviceId && items.isNotEmpty()) return
        this.deviceId = deviceId
        resetPagination()
        fetchPage(reset = true)
    }

    fun loadMore() {
        if (!hasMore || isLoadingPage) return
        fetchPage(reset = false)
    }

    private fun resetPagination() {
        lastDate = null
        hasMore = true
        items.clear()
        _uiState.value = ListDataState.Loading
    }

    private fun fetchPage(reset: Boolean) {
        val currentDeviceId = deviceId ?: return
        viewModelScope.launch {
            isLoadingPage = true
            if (!reset) {
                _uiState.value = ListDataState.Success(
                    data = items.toList(),
                    isLoadingMore = true,
                    hasMore = hasMore
                )
            }

            try {
                val response = repository.getListData(currentDeviceId, lastDate)
                if (reset) items.clear()
                items.addAll(response.daily)
                lastDate = response.lastDate
                hasMore = response.daily.isNotEmpty() && response.lastDate != null

                _uiState.value = ListDataState.Success(
                    data = items.toList(),
                    isLoadingMore = false,
                    hasMore = hasMore
                )
            } catch (e: Exception) {
                Log.e("ListDataViewModel", "load data failed", e)
                _uiState.value = ListDataState.Error(
                    message = e.message ?: "Terjadi kesalahan",
                    cachedData = items.toList()
                )
            } finally {
                isLoadingPage = false
            }
        }
    }
}