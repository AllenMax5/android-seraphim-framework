package com.seraphim.pokemon.shared.data.network

import com.seraphim.core.network.serializable.json
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

expect fun getPlatformEngine(): HttpClientEngineFactory<*>
expect fun HttpClientConfig<*>.platformEngineConfig()

object PokeHttpClientFactory {
    fun create(): HttpClient = HttpClient(getPlatformEngine()) {
        platformEngineConfig()

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.HEADERS
        }
    }
}
