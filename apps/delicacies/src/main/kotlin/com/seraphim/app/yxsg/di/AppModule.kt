package com.seraphim.app.yxsg.di

import com.seraphim.app.yxsg.ui.calendar.CalendarViewModel
import com.seraphim.app.yxsg.ui.checkin.CheckInViewModel
import com.seraphim.app.yxsg.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel { CheckInViewModel(get(), get(), get()) }
    viewModel { CalendarViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
