package com.seraphim.app.yxsg.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.delicacies.shared.data.repository.CheckInRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val lunchReminderHour: Int = 11,
    val lunchReminderMinute: Int = 30,
    val dinnerReminderHour: Int = 17,
    val dinnerReminderMinute: Int = 30,
)

class SettingsViewModel(
    private val repository: CheckInRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            _events.emit(
                if (enabled) SettingsEvent.ScheduleNotifications
                else SettingsEvent.CancelNotifications
            )
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                _snackbarMessage.emit("所有签到数据已清除")
            } catch (e: Exception) {
                _snackbarMessage.emit("清除失败: ${e.message}")
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val records = repository.getAllCheckedInRecords()
                val csv = buildString {
                    appendLine("日期,餐次,签到时间")
                    records.forEach { record ->
                        val mealLabel = if (record.mealType.name == "LUNCH") "午餐" else "晚餐"
                        appendLine("${record.date},$mealLabel,${record.createdAt}")
                    }
                }
                _events.emit(SettingsEvent.ExportCsv(csv))
                _snackbarMessage.emit("导出成功，共 ${records.size} 条记录")
            } catch (e: Exception) {
                _snackbarMessage.emit("导出失败: ${e.message}")
            }
        }
    }
}

sealed class SettingsEvent {
    data object ScheduleNotifications : SettingsEvent()
    data object CancelNotifications : SettingsEvent()
    data class ExportCsv(val content: String) : SettingsEvent()
}
