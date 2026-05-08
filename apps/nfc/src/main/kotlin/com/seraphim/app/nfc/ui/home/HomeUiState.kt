package com.seraphim.app.nfc.ui.home

import androidx.compose.runtime.Immutable
import com.seraphim.nfc.shared.domain.model.HillCardInfo

enum class NfcWaveState {
    Idle,       // 等待贴卡
    Scanning,   // 检测中
    Success,    // 读取成功
    Error,      // 读取失败
}

@Immutable
data class HomeUiState(
    val nfcState: NfcWaveState = NfcWaveState.Idle,
    val statusText: String = "将门禁卡贴靠手机背面",
    val lastCard: HillCardInfo? = null,
    val isNfcAvailable: Boolean = true,   // 设备是否有 NFC 硬件
    val isNfcEnabled: Boolean = true,      // NFC 是否已开启
    val isLoading: Boolean = false,
)
