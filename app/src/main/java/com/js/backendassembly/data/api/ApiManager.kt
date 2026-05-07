package com.js.backendassembly.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

enum class EndpointType(val prefix: String) {
    MOVIE("movie/"),
    LIST("list/"),
    POSTER(""),
    HOMEPAGE_LIST("movie/")

}

data class ApiResult(
    val fullUrl: String,
    val responseText: String,
    val isImage: Boolean
)

interface TmdbApi {
    @GET
    suspend fun getResponse(
        @Url endpoint: String,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("append_to_response") appendToResponse: String? = null
    ): Response<ResponseBody>
}

object ApiManager {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w300"
    private const val API_KEY = "7a0cf0cb349b8912480426231b4faf51"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // NAPRAWIONO BŁĄD: baseUrl jest absolutnie wymagane przez Retrofita!
        .build()

    private val api = retrofit.create(TmdbApi::class.java)

    suspend fun fetchApiData(type: EndpointType, input: String): Result<ApiResult> {
        return withContext(Dispatchers.IO) {
            try {
                val fullUrlOrPath = if (type == EndpointType.POSTER) {
                    "$IMAGE_BASE_URL$input"
                } else {
                    "$BASE_URL${type.prefix}$input"
                }

                val response = when (type) {
                    EndpointType.MOVIE -> api.getResponse(
                        endpoint = fullUrlOrPath,
                        apiKey = API_KEY,
                        language = "pl-PL",
                        appendToResponse = "credits"
                    )
                    EndpointType.LIST -> api.getResponse(
                        endpoint = fullUrlOrPath,
                        apiKey = API_KEY,
                        language = "pl-PL",
                        appendToResponse = null
                    )
                    EndpointType.POSTER -> api.getResponse(
                        endpoint = fullUrlOrPath,
                        apiKey = null,
                        language = null,
                        appendToResponse = null
                    )
//                    EndpointType.HOMEPAGE_LIST -> api.getResponse(
//                      //sklejenie 4 równoległych calli w jeden zwracany obiekt ładowany do infinite scrolla
//                    )
                }
                val finalUrl = response.raw().request.url.toString()

                if (response.isSuccessful) {
                    val isImage = type == EndpointType.POSTER

                    val responseString = if (isImage) {
                        "Plakat został załadowany z powodzeniem."
                    } else {
                        response.body()?.string() ?: "Pusta odpowiedź"
                    }

                    // Zwracamy naszą ulepszoną klasę ApiResult
                    Result.success(ApiResult(finalUrl, responseString, isImage))
                } else {
                    val errorString = response.errorBody()?.string() ?: "Nieznany błąd"
                    Result.failure(Exception("Kod HTTP ${response.code()}: $errorString\nURL: $finalUrl"))
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}