package com.seraphim.app.mapdemo
import android.app.Application
import com.seraphim.core.map.here.HereMapInstanceFactory
import com.seraphim.core.map.commons.MapCredentials
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.MapProviders
import com.seraphim.core.map.commons.registry.MapProviderRegistry

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val keyId = BuildConfig.HERE_ACCESS_KEY_ID
        val secret = BuildConfig.HERE_ACCESS_KEY_SECRET
        MapInitializer.init(this, MapProviders.HERE, MapCredentials.HereCredentials(keyId, secret))
        MapProviderRegistry.instance.register(HereMapInstanceFactory())
    }
}
