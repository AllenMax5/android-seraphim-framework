package com.seraphim.app.yxsg.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import com.seraphim.delicacies.shared.domain.model.MealType
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyRecordsUseCase
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyStatsUseCase
import com.seraphim.delicacies.shared.domain.usecase.UpdateCheckInUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CalendarUiState(
    val year: Int,
    val month: Int,
    val selectedDate: LocalDate? = null,
    val showDayDetail: Boolean = false,
) {
    val monthLabel: String get() = "${year} 年 ${month} 月"
}

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val getMonthlyRecordsUseCase: GetMonthlyRecordsUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val updateCheckInUseCase: UpdateCheckInUseCase,
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    private val _uiState = MutableStateFlow(
        CalendarUiState(year = today.year, month = today.monthNumber)
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    val dayStatusMap: StateFlow<Map<LocalDate, DayCheckInStatus>> = _uiState
        .flatMapLatest { state ->
            getMonthlyRecordsUseCase(state.year, state.month)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val monthlyStats: StateFlow<MonthlyStats> = _uiState
        .flatMapLatest { state ->
            getMonthlyStatsUseCase(state.year, state.month)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonthlyStats())

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun previousMonth() {
        _uiState.update {
            if (it.month == 1) it.copy(year = it.year - 1, month = 12)
            else it.copy(month = it.month - 1)
        }
    }

    fun nextMonth() {
        _uiState.update {
            if (it.month == 12) it.copy(year = it.year + 1, month = 1)
            else it.copy(month = it.month + 1)
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, showDayDetail = true) }
    }

    fun dismissDayDetail() {
        _uiState.update { it.copy(showDayDetail = false) }
    }

    fun updateCheckIn(date: LocalDate, mealType: MealType, checkedIn: Boolean) {
        viewModelScope.launch {
            val result = updateCheckInUseCase(date, mealType, checkedIn)
            result.fold(
                onSuccess = {
                    val action = if (checkedIn) "补签" else "取消签到"
                    val label = if (mealType == MealType.LUNCH) "午餐" else "晚餐"
                    _snackbarMessage.emit("${label}${action}成功")
                },
                onFailure = { e ->
                    _snackbarMessage.emit(e.message ?: "操作失败")
                },
            )
        }
    }
}
