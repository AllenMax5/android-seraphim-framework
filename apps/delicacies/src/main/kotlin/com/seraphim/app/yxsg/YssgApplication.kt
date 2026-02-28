package com.seraphim.app.yxsg

import android.app.Application
import com.seraphim.app.yxsg.di.appModule
import com.seraphim.app.yxsg.worker.WorkManagerScheduler
import com.seraphim.delicacies.shared.di.sharedModule
import com.seraphim.delicacies.shared.di.sharedPlatformModule
import com.seraphim.utils.initLogger
import com.seraphim.utils.initMMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class YssgApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initMMKV(this)
        initLogger()
        startKoin {
            androidLogger()
            androidContext(this@YssgApplication)
            modules(appModule, sharedModule, sharedPlatformModule)
        }
        // Schedule daily meal reminder notifications
        WorkManagerScheduler.schedule(this)
    }
}