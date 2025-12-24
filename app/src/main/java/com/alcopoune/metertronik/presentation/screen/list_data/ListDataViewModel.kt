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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var isRangeMode: Boolean = false

    private val items: MutableList<DailyData> = mutableListOf()

    private enum class SortBy { TIME, COST }
    private enum class SortOrder { ASC, DESC }

    private var sortBy: SortBy = SortBy.TIME
    private var sortOrder: SortOrder = SortOrder.DESC

    fun load(deviceId: String) {
        if (this.deviceId == deviceId && items.isNotEmpty()) return
        this.deviceId = deviceId
        isRangeMode = false
        resetPagination()
        fetchPage(reset = true)
    }

    fun loadMore() {
        if (!hasMore || isLoadingPage || isRangeMode) return
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
                    data = getSortedItems(),
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

                emitSuccess()
            } catch (e: Exception) {
                Log.e("ListDataViewModel", "load data failed", e)
                _uiState.value = ListDataState.Error(
                    message = e.message ?: "Something Went Wrong",
                    cachedData = getSortedItems()
                )
            } finally {
                isLoadingPage = false
            }
        }
    }

    private fun getSortedItems(): List<DailyData> {
        val baseList = items.toList()

        val comparator = when (sortBy) {
            SortBy.TIME -> compareBy<DailyData> { it.day }
            SortBy.COST -> compareBy<DailyData> { it.totalCost }
        }

        val sorted = baseList.sortedWith(comparator)
        return if (sortOrder == SortOrder.DESC) sorted.reversed() else sorted
    }

    private fun emitSuccess() {
        _uiState.value = ListDataState.Success(
            data = getSortedItems(),
            isLoadingMore = false,
            hasMore = hasMore
        )
    }

    fun updateSort(sortByLabel: String, orderValue: String) {
        sortBy = if (sortByLabel == "Cost") SortBy.COST else SortBy.TIME
        sortOrder = if (orderValue == "DESC") SortOrder.DESC else SortOrder.ASC

        if (_uiState.value is ListDataState.Success) {
            emitSuccess()
        }
    }

    fun applyDateRange(start: LocalDate?, end: LocalDate?) {
        val currentDeviceId = deviceId ?: return

        if (start == null || end == null) {
            isRangeMode = false
            resetPagination()
            fetchPage(reset = true)
            return
        }

        viewModelScope.launch {
            isRangeMode = true
            _uiState.value = ListDataState.Loading
            isLoadingPage = true

            try {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val startStr = start.format(formatter)
                val endStr = end.format(formatter)

                val response = repository.getRangeListData(
                    deviceId = currentDeviceId,
                    last = null,
                    start = startStr,
                    end = endStr
                )

                items.clear()
                items.addAll(response.daily)
                lastDate = null
                hasMore = false

                emitSuccess()
            } catch (e: Exception) {
                Log.e("ListDataViewModel", "load range data failed", e)
                _uiState.value = ListDataState.Error(
                    message = e.message ?: "Something Went Wrong",
                    cachedData = getSortedItems()
                )
            } finally {
                isLoadingPage = false
            }
        }
    }
}