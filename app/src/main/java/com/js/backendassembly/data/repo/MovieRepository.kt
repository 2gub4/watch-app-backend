package com.js.backendassembly.data.repo

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.js.backendassembly.data.api.MovieApi
import com.js.backendassembly.data.api.MovieApiResult
import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.domain.MovieProfile
import com.js.backendassembly.data.models.dtos.MovieDetailsDto
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlin.collections.emptyList
//import com.google.firebase.auth.auth

val userLists = listOf(1, 2, 3, 4)
val userRatedMovies = listOf(1111, 2222, 3333, 4444)

val favouritesTemplate = hashMapOf(
    "list_name" to "Ulubione",
    "list_description" to "Filmy, które wyjątkowo doceniłeś",
    "favourite_movies_ids" to emptyList<Int>()
)

val bucketlistTemplate = hashMapOf(
    "list_name" to "Kupka Wstydu",
    "list_description" to "Filmy, które już dawno powinieneś obejrzeć",
    "bucketlist_movies_ids" to emptyList<Int>()
)

val testUsr = hashMapOf(
    "uid" to "123456789dsfhsd",
    "email" to "test@example.com",
    "display_name" to "test_user_123",
    "date_of_birth" to "2004-01-01",
    "date_of_registration" to "2026-13-05",
    "user_lists" to userLists,
    "predefined_lists" to listOf(favouritesTemplate, bucketlistTemplate),
    "rated_movies" to userRatedMovies,
    "watched_movies_cout" to 41,
    "watched_series_cout" to 13,
    "favourites_count" to 12,
    "rated_movies_count" to userRatedMovies.size,
    //"pfp_path" to "pfp.png"
)

val CURRENT_USER: String = "test_user"


object MoviesRepository {
    private val db = Firebase.firestore
    //private val auth = Firebase.auth
    private val api = MovieApi

    suspend fun getApiMovieDetails(movieId: Int): MovieDetailsDto? {
        return when (val response = api.fetchMovieDetails(movieId)) {
            is MovieApiResult.OnSuccess -> response.data
            is MovieApiResult.OnFailure -> { Log.e("Movie Repository", "Could not recieve MovieDetailsDto", response.error); null }
        }
    }

    suspend fun getDbMovieRating(movieId: Int): Rating? {
        val result = db.collection("users")
            .document(CURRENT_USER)
            .collection("ratings")
            .document("$movieId")
            .get()
            .await()
        return result.toObject(Rating::class.java)
    }

    suspend fun getListsContainingMovie(movieId: Int): List<String> {
        val snapshot = db.collection("users")
            .document(CURRENT_USER)
            .collection("lists")
            .whereArrayContains("movies", movieId)
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }

    suspend fun getMovieProfile(movieId: Int): MovieProfile {
        return MovieProfile()
    }







//    suspend fun getMovieOverview(userId: String, movieId: Int): MovieOverviewDto? {
//        val movieId = "test_user"
//        return try {
//            val snapshot = db.collection("users")
//                .document(userId)
//                .collection("movies")
//                .document(movieId)
//                .get()
//                .await()
//            snapshot.toObject(MovieOverviewDto::class.java)
//        } catch (e: Exception) {
//            null // W razie błędu zwracamy null
//        }
//    }
//
//    suspend fun saveMovieState(userId: String, movie: MovieOverviewDto) {
//        try {
//            db.collection("users")
//                .document(userId)
//                .collection("movies")
//                .document(movie.id.toString())
//                .set(movie)
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    suspend fun getUserLists(userId: String): List<MovieList> {
//        return try {
//            val snapshot = db.collection("users")
//                .document(userId)
//                .collection("custom_lists")
//                .get()
//                .await()
//
//            snapshot.toObjects(MovieList::class.java)
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//
//    suspend fun createNewUserList(userId: String, newList: MovieList) {
//        try {
//            db.collection("users")
//                .document(userId)
//                .collection("custom_lists")
//                .document(newList.id)
//                .set(newList)
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}