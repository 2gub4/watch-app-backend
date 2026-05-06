package com.js.backendassembly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("overview") val overview: String,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("adult") val adult: Boolean,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("genres") val genres: List<Genre> = emptyList(),
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
)

@Serializable
data class MovieCollection(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null
)

@Serializable
data class Genre(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)


@Serializable
data class ListedMovie(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("overview") val overview: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("softcore") val softcore: Boolean = false,
    @SerialName("video") val video: Boolean = false,
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("original_language") val originalLanguage: String = "",
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList()
)
