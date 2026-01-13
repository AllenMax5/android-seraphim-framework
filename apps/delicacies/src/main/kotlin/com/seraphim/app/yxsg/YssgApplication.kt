package com.seraphim.app.yxsg

import android.app.Application
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
//            modules(appModule + sharedModule + domainModule)
        }
//        notificationWorkManager()
    }

//    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotifyWorker>(
//        1, TimeUnit.DAYS
//    )
//        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS) // 可选：定点触发
//        .build()

//    private fun notificationWorkManager() {
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "daily_notify",
//            ExistingPeriodicWorkPolicy.KEEP,
//            dailyWorkRequest
//        )
//    }
}