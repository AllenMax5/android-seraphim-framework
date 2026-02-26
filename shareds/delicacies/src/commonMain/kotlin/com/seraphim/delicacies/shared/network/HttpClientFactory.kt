package com.seraphim.delicacies.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import kotlinx.serialization.json.Json
expect fun getPlatformEngine(): HttpClientEngineFactory<*>
expect fun HttpClientConfig<*>.platformEngineConfig()

object HttpClientFactory {
    fun create(
        baseUrl: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMillis: Long = 30_000,
        json: Json = com.seraphim.core.network.serializable.json,
        logLevel: LogLevel = LogLevel.INFO,
        ktorLogger: Logger = Logger.DEFAULT,
    ): HttpClient {
        return HttpClient(getPlatformEngine()) {

        }
    }
}