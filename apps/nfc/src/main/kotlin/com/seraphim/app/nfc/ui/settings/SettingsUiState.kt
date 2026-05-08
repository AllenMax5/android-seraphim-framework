package com.seraphim.app.nfc.ui.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsUiState(
    val appLockEnabled: Boolean = false,
    val biometricEnabled: Boolean = true,
    val autoReadEnabled: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val defaultCardId: String? = null,
    val isLoading: Boolean = false,
)
