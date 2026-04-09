package com.seraphim.app.literacy

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LiteracyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LiteracyApplication)
            // modules(appModule)
        }
    }
}
