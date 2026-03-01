package com.seraphim.app.pokemon

import android.app.Application
import com.seraphim.app.pokemon.di.appModule
import com.seraphim.pokemon.shared.di.sharedModule
import com.seraphim.pokemon.shared.di.sharedPlatformModule
import com.seraphim.utils.initLogger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PokemonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
        startKoin {
            androidLogger()
            androidContext(this@PokemonApplication)
            modules(appModule, sharedModule, sharedPlatformModule)
        }
    }
}
