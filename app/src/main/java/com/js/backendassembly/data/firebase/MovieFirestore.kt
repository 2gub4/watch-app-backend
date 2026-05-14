package com.js.backendassembly.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.dbmodels.User
import com.js.backendassembly.data.models.dbmodels.UserList
import kotlinx.coroutines.tasks.await

val testUsr = User(
    uid = "test_user",
    email = "test@example.com",
    username = "test_user_123",
    pfpPath = "pfp.png",
    watchedMoviesCount = 0,
    watchedSeriesCount = 0,
    favouritesCount = 0,
    ratingsCount = 0
)

val favouritesTemplate = UserList(
    name = "Ulubione",
    description = "Filmy, które wyjątkowo doceniłeś",
    movies = emptyList(),
    series = emptyList()
)

val bucketlistTemplate = UserList(
    name = "Kupka Wstydu",
    description = "Filmy, które już dawno powinieneś był obejrzeć",
    movies = emptyList(),
    series = emptyList()
)

val customListTest = UserList(
    name = "Guilty Pleasures",
    description = "Słabe produkcje, dobra zabawa",
    movies = listOf(1022690),
    series = listOf()
)

object MovieFirestore {
    val firestoreDb by lazy { Firebase.firestore }

    suspend fun initialSeeding() {
        try {
            firestoreDb.collection("users").document(testUsr.uid).set(testUsr).await()
            firestoreDb.collection("users").document(testUsr.uid).collection("lists").document("favourites").set(favouritesTemplate).await()
            firestoreDb.collection("users").document(testUsr.uid).collection("lists").document("bucketlist").set(bucketlistTemplate).await()
            firestoreDb.collection("users").document(testUsr.uid).collection("lists").document().set(customListTest).await()
        } catch (e: Exception) {
            Log.e("MovieFirestore", "Seeding Failure", e)
        }
    }

    object UserData {
        suspend fun getUserLists(userId: String): List<UserList> {
            return try {
                val snapshot = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .get()
                    .await()
                snapshot.toObjects(UserList::class.java)
            } catch (e: Exception) {
                Log.e("Movie Repository", "Could not receive Lists of user: $userId", e)
                emptyList()
            }
        }
    }

    object ListData {
        suspend fun createNewUserList(userId: String, newList: UserList) {
            try {
                val listsCollection = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                if (newList.id.isNotEmpty()) {
                    listsCollection.document(newList.id).set(newList).await()
                } else {
                    listsCollection.document().set(newList).await()
                }
            } catch (e: Exception) {
                Log.e("Movie Repository", "Could not add user list to database", e)
            }
        }

        suspend fun getListsContainingMovie(userId: String, movieId: String): List<String> {
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
                print("could not access lists collection to recieve data")
                emptyList()
            }
        }

    }

    object RatingData {
        //to implement
    }

    object MovieData {
        suspend fun getMovieRating(userId: String, movieId: String): Rating? {
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
                print("Error: could not access document with rating")
                null
            }
        }
    }
}
