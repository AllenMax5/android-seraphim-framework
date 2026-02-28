package com.seraphim.app.yxsg.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.delicacies.shared.domain.model.MealType
import com.seraphim.delicacies.shared.domain.model.MonthlyStats
import com.seraphim.delicacies.shared.domain.usecase.CheckInUseCase
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyRecordsUseCase
import com.seraphim.delicacies.shared.domain.usecase.GetMonthlyStatsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CheckInUiState(
    val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val lunchCheckedIn: Boolean = false,
    val dinnerCheckedIn: Boolean = false,
    val isLoading: Boolean = false,
    val welcomeMessage: String = getWelcomeMessage(),
)

private fun getWelcomeMessage(): String {
    val hour = kotlin.time.Clock.System.now()
        .toEpochMilliseconds()
        .let { java.util.Calendar.getInstance().apply { timeInMillis = it }.get(java.util.Calendar.HOUR_OF_DAY) }
    return when {
        hour < 11 -> "早上好！准备享用午餐了吗？🌅"
        hour < 14 -> "午餐时间到！好好吃饭哦 🍚"
        hour < 17 -> "下午好！晚餐别忘了签到 ☀️"
        hour < 20 -> "晚餐时间！记得签到哦 🍜"
        else -> "今天也辛苦啦！明天继续加油 🌙"
    }
}

class CheckInViewModel(
    private val checkInUseCase: CheckInUseCase,
    private val getMonthlyRecordsUseCase: GetMonthlyRecordsUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    val monthlyStats: StateFlow<MonthlyStats> = getMonthlyStatsUseCase(today.year, today.monthNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonthlyStats())

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        observeTodayRecords()
    }

    private fun observeTodayRecords() {
        viewModelScope.launch {
            getMonthlyRecordsUseCase(today.year, today.monthNumber).collect { dayMap ->
                val todayStatus = dayMap[today]
                _uiState.update {
                    it.copy(
                        lunchCheckedIn = todayStatus?.lunchCheckedIn == true,
                        dinnerCheckedIn = todayStatus?.dinnerCheckedIn == true,
                    )
                }
            }
        }
    }

    fun checkIn(mealType: MealType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = checkInUseCase(today, mealType)
            result.fold(
                onSuccess = {
                    val label = if (mealType == MealType.LUNCH) "午餐" else "晚餐"
                    _snackbarMessage.emit("${label}签到成功！✓")
                },
                onFailure = { e ->
                    _snackbarMessage.emit(e.message ?: "签到失败")
                },
            )
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
