package com.js.backendassembly.data.repo

import android.util.Log
import com.js.backendassembly.data.api.MovieApi
import com.js.backendassembly.data.api.MovieApiResult
import com.js.backendassembly.data.firebase.MovieFirestore
import com.js.backendassembly.domain.models.MovieProfile
import com.js.backendassembly.data.models.dtos.MovieDetailsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

const val CURRENT_USER: String = "test_user"


object MoviesRepository {
    private val api = MovieApi

    suspend fun getApiMovieDetails(movieId: Int): MovieDetailsDto? {
        return when (val response = api.fetchMovieDetails(movieId)) {
            is MovieApiResult.OnSuccess -> response.data
            is MovieApiResult.OnFailure -> {
                Log.e("Movie Repository", "Could not recieve MovieDetailsDto", response.error)
                print("Error: could not get movie details from TMDB Api")
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

}