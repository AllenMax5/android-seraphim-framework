package com.seraphim.pokemon.shared.data.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getPlatformEngine(): HttpClientEngineFactory<*> = Darwin
actual fun HttpClientConfig<*>.platformEngineConfig() {}
