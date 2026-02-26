package com.seraphim.core.network

import io.ktor.http.HttpStatusCode

sealed class BffResult<T> {
    data class Success<T>(val response: T) : BffResult<T>()
    sealed class Failure<T> : BffResult<T>() {
        data class LocalizedError<T>(val error: NetworkException) : Failure<T>()
        data class HttpError<T>(val httpStatusCode: HttpStatusCode) : Failure<T>()
        data class SerializationError<T>(val throwable: Throwable) : Failure<T>()
        data class NetworkError<T>(val throwable: Throwable) : Failure<T>()
        data class GeneralError<T>(val throwable: Throwable) : Failure<T>()
    }
}