package com.js.backendassembly.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.js.backendassembly.data.models.dbmodels.Rating
import kotlinx.coroutines.tasks.await

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




object MovieFirestore {
    val firestoreDb by lazy { Firebase.firestore }

    suspend fun initialSeeding() {}

    suspend fun getDbMovieRating(userId: String, movieId: String): Rating? {
        return try {
            val result = firestoreDb.collection("users")
                .document(userId)
                .collection("ratings")
                .document(movieId)
                .get()
                .await()
            result.toObject(Rating::class.java)
        } catch (e: Exception) {
            Log.e("Movie Repository", "Could not receive Rating", e)
            null
        }
    }

    suspend fun getDbListsContainingMovie(userId: String, movieId: String): List<String> {
        return try {
            val snapshot = firestoreDb.collection("users")
                .document(userId)
                .collection("lists")
                .whereArrayContains("movies", movieId)
                .get()
                .await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            Log.e("Movie Repository", "Could not receive Lists", e)
            emptyList()
        }
    }

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