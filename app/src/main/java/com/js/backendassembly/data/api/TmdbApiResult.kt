package com.js.backendassembly.data.api

sealed class TmdbApiResult<out T> {
    data class OnSuccess<out T>(val data: T) : TmdbApiResult<T>()
    data class OnFailure(val error: Throwable) : TmdbApiResult<Nothing>()
}