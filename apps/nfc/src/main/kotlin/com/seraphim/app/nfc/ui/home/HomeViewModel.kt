package com.seraphim.app.nfc.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seraphim.app.nfc.nfc.NfcReader
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val nfcReader: NfcReader,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    /**
     * 检测 NFC 硬件和开关状态
     */
    fun checkNfcState() {
        val isAvailable = nfcReader.isNfcAvailable()
        val isEnabled = nfcReader.isNfcEnabled()
        _uiState.update { current ->
            current.copy(
                isNfcAvailable = isAvailable,
                isNfcEnabled = isEnabled,
                statusText = when {
                    !isAvailable -> "设备不支持 NFC 功能"
                    !isEnabled -> "请开启 NFC 功能"
                    else -> "将门禁卡贴靠手机背面"
                },
                nfcState = when {
                    !isAvailable || !isEnabled -> NfcWaveState.Error
                    else -> NfcWaveState.Idle
                },
            )
        }
    }

    /**
     * 跳转到系统 NFC 设置
     */
    fun openNfcSettings() {
        nfcReader.openNfcSettings()
    }

    fun onNfcDiscovered(cardInfo: HillCardInfo) {
        _uiState.update { current ->
            current.copy(
                nfcState = NfcWaveState.Success,
                statusText = "读取成功: ${cardInfo.name}",
                lastCard = cardInfo,
                isLoading = false,
            )
        }
        viewModelScope.launch {
            _snackbarMessage.emit("成功读取门禁卡: ${cardInfo.uid}")
        }
    }

    fun onNfcError(message: String) {
        _uiState.update { current ->
            current.copy(
                nfcState = NfcWaveState.Error,
                statusText = message,
                isLoading = false,
            )
        }
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    fun startScanning() {
        _uiState.update { current ->
            current.copy(
                nfcState = NfcWaveState.Scanning,
                statusText = "正在读取门禁卡...",
                isLoading = true,
            )
        }
    }

    fun resetToIdle() {
        _uiState.update { current ->
            current.copy(
                nfcState = NfcWaveState.Idle,
                statusText = "将门禁卡贴靠手机背面",
                isLoading = false,
            )
        }
    }

    fun startManualRead() {
        startScanning()
        // TODO: 触发 NFC 前台分发
    }

    fun setNfcEnabled(enabled: Boolean) {
        _uiState.update { current ->
            current.copy(
                isNfcEnabled = enabled,
                statusText = if (enabled) "将门禁卡贴靠手机背面" else "请开启 NFC 功能",
            )
        }
    }
}
