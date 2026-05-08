package com.seraphim.app.nfc.di

import com.seraphim.app.nfc.nfc.NfcManager
import com.seraphim.app.nfc.nfc.NfcReader
import com.seraphim.app.nfc.ui.home.HomeViewModel
import com.seraphim.app.nfc.ui.wallet.WalletViewModel
import com.seraphim.app.nfc.ui.emulate.EmulateViewModel
import com.seraphim.app.nfc.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // NFC Reader（纯工具类，无生命周期依赖）
    single { NfcReader(androidContext()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { WalletViewModel(get()) }
    viewModel { EmulateViewModel() }
    viewModel { SettingsViewModel() }
}
