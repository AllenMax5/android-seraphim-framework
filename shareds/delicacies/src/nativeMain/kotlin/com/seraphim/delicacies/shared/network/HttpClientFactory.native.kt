package com.seraphim.delicacies.shared.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getPlatformEngine(): HttpClientEngineFactory<*> = Darwin
actual fun HttpClientConfig<*>.platformEngineConfig() {
}