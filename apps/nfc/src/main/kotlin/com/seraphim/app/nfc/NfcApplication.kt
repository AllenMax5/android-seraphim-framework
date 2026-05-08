package com.seraphim.app.nfc

import android.app.Application
import androidx.room.Room
import com.seraphim.nfc.shared.data.db.database.KeyWorldDatabase
import com.seraphim.app.nfc.di.appModule
import com.seraphim.nfc.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class NfcApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NfcApplication)
            modules(
                listOf(sharedModule, appModule, databaseModule),
            )
        }
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder<KeyWorldDatabase>(
            androidContext(),
            "keyworld.db",
        ).build()
    }
}
