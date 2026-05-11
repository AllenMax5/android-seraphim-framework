package com.seraphim.app.mapdemo

import android.app.Application
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.MapProviders
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import com.seraphim.core.map.here.HereMapInstanceFactory

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapInitializer.init(
            this, MapProviders.HERE,
            apiKey = BuildConfig.HERE_ACCESS_KEY_ID,
            apiSecret = BuildConfig.HERE_ACCESS_KEY_SECRET
        )
        MapProviderRegistry.instance.register(HereMapInstanceFactory())
    }
}
