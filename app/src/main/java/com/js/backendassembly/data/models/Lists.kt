package com.js.backendassembly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.List


@Serializable
data class MovieList(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("created_by") val createdBy: String,
    @SerialName("iso_639_1") val iso6391: String,
    @SerialName("favorite_count") val favoriteCount: Int,
    @SerialName("item_count") val itemCount: Int,
    @SerialName("page") val page: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("items") val items: List<ListedMovie> = emptyList()
)

@Serializable
data class ListOverview(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("item_count") val itemCount: Int,
    @SerialName("favorite_count") val favoriteCount: Int,
    @SerialName("list_type") val listType: String,
    @SerialName("iso_639_1") val iso6391: String,
    @SerialName("poster_path") val posterPath: String? = null
)



