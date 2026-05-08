package com.seraphim.app.nfc.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: 从 MMKV/Repository 加载设置
        }
    }

    fun toggleAppLock(enabled: Boolean) {
        _uiState.update { it.copy(appLockEnabled = enabled) }
        // TODO: 保存设置
    }

    fun toggleBiometric(enabled: Boolean) {
        _uiState.update { it.copy(biometricEnabled = enabled) }
        // TODO: 保存设置
    }

    fun toggleAutoRead(enabled: Boolean) {
        _uiState.update { it.copy(autoReadEnabled = enabled) }
        // TODO: 保存设置
    }

    fun toggleVibration(enabled: Boolean) {
        _uiState.update { it.copy(vibrationEnabled = enabled) }
        // TODO: 保存设置
    }

    fun setDefaultCard(cardId: String?) {
        _uiState.update { it.copy(defaultCardId = cardId) }
        // TODO: 保存设置
    }

    fun backupData() {
        viewModelScope.launch {
            // TODO: 导出加密备份
        }
    }

    fun restoreData() {
        viewModelScope.launch {
            // TODO: 从备份恢复
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // TODO: 清除所有数据（需密码确认）
        }
    }
}
