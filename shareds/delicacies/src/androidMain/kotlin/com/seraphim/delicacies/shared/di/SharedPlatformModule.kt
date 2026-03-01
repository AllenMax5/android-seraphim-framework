package com.seraphim.delicacies.shared.di

import com.seraphim.core.storage.db.AndroidDatabaseFactory
import com.seraphim.core.storage.db.DatabaseBuilder
import com.seraphim.core.storage.db.DatabaseFactory
import com.seraphim.delicacies.shared.data.db.DelicaciesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPlatformModule = module {
    single<DatabaseFactory> { AndroidDatabaseFactory(androidContext()) }
    single<DelicaciesDatabase> {
        DatabaseBuilder.build(
            factory = get(),
            klass = DelicaciesDatabase::class,
            name = "delicacies.db",
        )
    }
}
