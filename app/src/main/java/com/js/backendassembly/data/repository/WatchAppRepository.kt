package com.js.backendassembly.data.repository

import android.util.Log
import com.js.backendassembly.data.api.TmdbApiResult
import com.js.backendassembly.data.api.TmdbApi
import com.js.backendassembly.data.firebase.MovieFirestore
import com.js.backendassembly.domain.models.profiles.MovieProfile
import com.js.backendassembly.data.models.dtos.movies.MovieDetailsDto
import com.js.backendassembly.data.models.dtos.movies.MoviesPageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

const val CURRENT_USER: String = "test_user"


object MoviesRepository {
    const val POSTERS_BASE_URL = "https://image.tmdb.org/t/p/w500"

    suspend fun getApiMovieDetails(movieId: Int): MovieDetailsDto? {
        return when (val response = TmdbApi.MoviesData.fetchMovieDetails(movieId)) {
            is TmdbApiResult.OnSuccess -> response.data
            is TmdbApiResult.OnFailure -> {
                Log.e("Movie Repository", "Could not recieve MovieDetailsDto", response.error)
                null
            }
        }
    }

    suspend fun getMovieProfile(movieId: Int): MovieProfile? {
        return withContext(Dispatchers.IO) {
            val apiMovieDetails = async { getApiMovieDetails(movieId) }
            val potentialUserRating = async { MovieFirestore.RatingData.getMovieRating(CURRENT_USER, movieId.toString()) }
            val containingLists = async { MovieFirestore.ListData.getListsContainingMovie(CURRENT_USER, movieId.toString()) }
            val details = apiMovieDetails.await() ?: return@withContext null
            MovieProfile(
                movieDetails = details,
                rating = potentialUserRating.await(),
                containingLists = containingLists.await()
            )
        }
    }

    suspend fun getMoviePage(pageNumber: Int, listType: String): MoviesPageDto? {
        return withContext(Dispatchers.IO) {
            when (val response = TmdbApi.MoviesData.fetchMoviesPage(listType, pageNumber)) {
                is TmdbApiResult.OnSuccess -> response.data
                is TmdbApiResult.OnFailure -> {
                    Log.e("Movie Repository", "Could not recieve MoviesPageDto", response.error)
                    null
                }
            }
        }
    }

}