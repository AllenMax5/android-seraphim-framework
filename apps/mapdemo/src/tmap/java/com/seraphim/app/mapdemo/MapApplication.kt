package com.seraphim.app.mapdemo

import android.app.Application
import com.seraphim.core.map.tmap.TmapMapInstanceFactory
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.MapProviders
import com.seraphim.core.map.commons.registry.MapProviderRegistry

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapInitializer.init(this, MapProviders.TMAP)
        MapProviderRegistry.instance.register(TmapMapInstanceFactory())
    }
}
