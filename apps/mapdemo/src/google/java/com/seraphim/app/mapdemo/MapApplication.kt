package com.seraphim.app.mapdemo

import android.app.Application
import com.seraphim.core.map.commons.MapCredentials
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.MapProviders
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import com.seraphim.core.map.google.GoogleMapInstanceFactory

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val placesKey = BuildConfig.GOOGLE_PLACES_API_KEY
        MapInitializer.init(
            this,
            MapProviders.GOOGLE,
            MapCredentials.ApiKey(placesKey)
        )
        MapProviderRegistry.instance.register(GoogleMapInstanceFactory())
    }
}
