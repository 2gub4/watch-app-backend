package com.js.backendassembly.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

// 1. Nowy Enum ułatwiający sterowanie z poziomu UI i hermetyzację ścieżek
enum class EndpointType(val prefix: String) {
    MOVIE("movie/"),
    LIST("list/")
}

data class ApiResult(
    val fullUrl: String,
    val jsonBody: String
)

interface TmdbDynamicApi {
    @GET
    suspend fun getRawResponse(
        @Url endpoint: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "pl-PL"
    ): Response<ResponseBody>
}

object ApiManager {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

    private val api = retrofit.create(TmdbDynamicApi::class.java)

    // 2. Funkcja przyjmuje teraz typ endpointu, a następnie sama buduje ostateczny URL
    suspend fun fetchRawJson(type: EndpointType, input: String, apiKey: String): Result<ApiResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Automatyczne doklejanie odpowiedniego fragmentu (np. "movie/" + "popular")
                val fullEndpoint = "${type.prefix}$input"
                val response = api.getRawResponse(fullEndpoint, apiKey)

                val fullUrl = response.raw().request.url.toString()

                if (response.isSuccessful) {
                    val jsonString = response.body()?.string() ?: "Pusta odpowiedź"
                    Result.success(ApiResult(fullUrl, jsonString))
                } else {
                    val errorString = response.errorBody()?.string() ?: "Nieznany błąd HTTP"
                    Result.failure(Exception("Kod ${response.code()}: $errorString\nURL: $fullUrl"))
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}