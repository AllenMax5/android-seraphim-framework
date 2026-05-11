package com.seraphim.app.mapdemo

import android.app.Application
import com.seraphim.core.map.amap.AMapMapInstanceFactory
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.MapProviders
import com.seraphim.core.map.commons.registry.MapProviderRegistry

class MapApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        // Init SDK + set active provider
        MapInitializer.init(this, MapProviders.AMAP)

        // Register factories into global singleton
        MapProviderRegistry.instance.register(AMapMapInstanceFactory())
    }

    companion object {
        private const val TAG = "MapApplication"
    }
}
