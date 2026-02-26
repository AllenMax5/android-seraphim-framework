package com.seraphim.delicacies.shared.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.logging.HttpLoggingInterceptor

actual fun getPlatformEngine(): HttpClientEngineFactory<*> = OkHttp
actual fun HttpClientConfig<*>.platformEngineConfig() {
    engine {
        val config = this as OkHttpConfig
        config.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }
}