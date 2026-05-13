package com.js.backendassembly.data.api

sealed class MovieApiResult<out T> {
    data class OnSuccess<out T>(val data: T) : MovieApiResult<T>()
    data class OnFailure(val error: Throwable) : MovieApiResult<Nothing>()
}