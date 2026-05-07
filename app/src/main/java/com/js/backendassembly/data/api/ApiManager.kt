package com.js.backendassembly.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    private const val LANG = "en-US"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

    private val api = retrofit.create(TmdbApi::class.java)

    suspend fun fetchApiData(type: EndpointType, input: String): Result<ApiResult> {
        return withContext(Dispatchers.IO) {
            try {
                if (type == EndpointType.HOMEPAGE_LIST) {
                    //dodać każdorazowo inkrementowaną zmienną i obsłużyć ją w getResponse(), aby otrzymywać strony wyników
                    val popularRes = async { api.getResponse("${BASE_URL}movie/popular", API_KEY, LANG) }
                    val topRatedRes = async { api.getResponse("${BASE_URL}movie/top_rated", API_KEY, LANG) }
                    val upcomingRes = async { api.getResponse("${BASE_URL}movie/upcoming", API_KEY, LANG) }
                    val nowPlayingRes = async { api.getResponse("${BASE_URL}movie/now_playing", API_KEY, LANG) }
                    val responseComponents = listOf(
                        "popular" to popularRes.await(),
                        "topRated" to topRatedRes.await(),
                        "upcoming" to upcomingRes.await(),
                        "nowPlaying" to nowPlayingRes.await()
                    )
                    val combinedUrls = responseComponents.joinToString("\n") { it.second.raw().request.url.toString() }
                    val combinedBodies = responseComponents.joinToString("\n\n") { (category, response) ->
                        val bodyText = if (response.isSuccessful) {
                            response.body()?.string() ?: "empty response"
                        } else {
                            "ERROR: HTTP ${response.code()}"
                        }
                        "• $category movies:\n\n$bodyText"
                    }
                    return@withContext Result.success(ApiResult(combinedUrls, combinedBodies, false))
                }
                val resourceLocator = if (type == EndpointType.POSTER) {
                    "$IMAGE_BASE_URL$input"
                } else {
                    "$BASE_URL${type.prefix}$input"
                }

                val response = when (type) {
                    EndpointType.MOVIE -> api.getResponse(
                        endpoint = resourceLocator,
                        apiKey = API_KEY,
                        language = LANG,
                        appendToResponse = "credits"
                    )
                    EndpointType.LIST -> api.getResponse(
                        endpoint = resourceLocator,
                        apiKey = API_KEY,
                        language = LANG,
                        appendToResponse = null
                    )
                    EndpointType.POSTER -> api.getResponse(
                        endpoint = resourceLocator,
                        apiKey = null,
                        language = null,
                        appendToResponse = null
                    )
                    else -> throw IllegalStateException("ERROR: endpoint type out of bounds")
                }
                val finalUrl = response.raw().request.url.toString()
                if (response.isSuccessful) {
                    val isImage = type == EndpointType.POSTER
                    val responseString = if (isImage) {
                        "poster loaded successfully"
                    } else {
                        response.body()?.string() ?: "empty response"
                    }
                    Result.success(ApiResult(finalUrl, responseString, isImage))
                } else {
                    val errorString = response.errorBody()?.string() ?: "unknown error occured"
                    Result.failure(Exception("HTTP ${response.code()}: $errorString\nURL: $finalUrl"))
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}