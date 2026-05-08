package com.seraphim.app.nfc.ui.emulate

import androidx.compose.runtime.Immutable
import com.seraphim.nfc.shared.domain.model.HillCardInfo

enum class EmulateState {
    Idle,     // 未开始
    Active,   // 模拟中
    Success,  // 模拟成功（被读卡器识别）
    Error,    // 模拟失败
}

@Immutable
data class EmulateUiState(
    val card: HillCardInfo? = null,
    val emulateState: EmulateState = EmulateState.Idle,
    val statusText: String = "准备模拟",
    val isLoading: Boolean = false,
)
