package com.seraphim.pokemon.shared.data.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.logging.HttpLoggingInterceptor

actual fun getPlatformEngine(): HttpClientEngineFactory<*> = OkHttp
actual fun HttpClientConfig<*>.platformEngineConfig() {
    engine {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        addInterceptor(loggingInterceptor)
    }
}
