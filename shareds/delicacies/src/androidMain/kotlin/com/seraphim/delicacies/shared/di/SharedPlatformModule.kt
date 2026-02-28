package com.seraphim.delicacies.shared.di

import com.seraphim.delicacies.shared.data.db.DelicaciesDatabase
import com.seraphim.delicacies.shared.data.db.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPlatformModule = module {
    single<DelicaciesDatabase> {
        getDatabaseBuilder(androidContext()).build()
    }
}
