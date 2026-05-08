package com.seraphim.app.nfc.ui.wallet

import androidx.compose.runtime.Immutable
import com.seraphim.nfc.shared.domain.model.HillCardInfo

@Immutable
data class WalletUiState(
    val cards: List<HillCardInfo> = emptyList(),
    val groups: List<String> = listOf("全部", "家", "公司", "访客", "未分组"),
    val selectedGroup: String = "全部",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)
