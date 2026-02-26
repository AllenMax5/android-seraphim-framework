package com.seraphim.core.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
data class NetworkException(val code: Int, val message: String?, val cause: String? = null)

inline fun <T> runCatchingOrError(block: () -> BffResult<T>): BffResult<T> =
    runCatching { block() }.getOrElse {
        when (it) {
            is SerializationException -> BffResult.Failure.SerializationError(it)
            else -> BffResult.Failure.NetworkError(it)
        }
    }