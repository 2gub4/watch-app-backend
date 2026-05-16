package com.js.backendassembly.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.js.backendassembly.data.models.dtos.MovieDetailsDto
import com.js.backendassembly.data.models.dtos.MoviesPageDto
import com.js.backendassembly.data.models.dtos.TvSeriesDetailsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): MovieDetailsDto

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false
    ): MoviesPageDto

    @GET("tv/{tv_id}")
    suspend fun getTvSeriesDetails(
        @Path("series_id") movieId: Int,
        @Query("api_key") apiKey: String? = null,
        @Query("language") language: String? = null,
        @Query("include_adult") includeAdultContent: Boolean = false,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TvSeriesDetailsDto //


    // endpoints for media searching
    @GET("search/multi")
    suspend fun searchMulti(

    ): MoviesPageDto /*might need another wrapper do decide whether its a tv series or movie OR needs generalisation on MovieDetailsDto so it could contain tv series data*/
}

object MovieApi {
    private const val API_KEY = "7a0cf0cb349b8912480426231b4faf51"
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private const val LANG_EN = "en-US"
    private const val LANG_PL = "pl-PL"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonParser.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(TmdbApi::class.java)

    suspend fun fetchMovieDetails(movieId: Int): MovieApiResult<MovieDetailsDto> {
        return withContext(Dispatchers.IO) {
            try {
                val movieDetailsDto = api.getMovieDetails(movieId, API_KEY, LANG_PL)
                MovieApiResult.OnSuccess(movieDetailsDto)
            } catch (e: Throwable) {
                MovieApiResult.OnFailure(e)
            }
        }
    }

    suspend fun fetchMoviesPage(listType: String /*could be changed to enum*/, pageNumber: Int) : MovieApiResult<MoviesPageDto> {
        return withContext(Dispatchers.IO) {
            try {
                val moviesPageDto = when (listType) {
                    "popular" -> api.getPopularMovies(pageNumber, API_KEY, LANG_PL)
                    "top_rated" -> api.getTopRatedMovies(pageNumber, API_KEY, LANG_PL)
                    "upcoming" -> api.getUpcomingMovies(pageNumber, API_KEY, LANG_PL)
                    "now_playing" -> api.getNowPlayingMovies(pageNumber, API_KEY, LANG_PL)
                    else -> throw IllegalArgumentException("Invalid list type: $listType")
                }
                MovieApiResult.OnSuccess(moviesPageDto)
            } catch (e: Throwable) {
                MovieApiResult.OnFailure(e)
            }
        }
    }
}