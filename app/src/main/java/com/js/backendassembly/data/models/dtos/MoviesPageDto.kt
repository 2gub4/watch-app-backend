package com.js.backendassembly.data.models.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesPageDto(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<MovieOverviewDto>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

// mamy problem bo w miejsce MovieOverviewDto.title serial ma .name co utrudnia mapowanie na ten jeden typ
// należy znaleźć rozwiązanie
// możliwe że będzie to globalne rozwiązanie na wszystkie kwestie między serialem a filmem
// najprostszy będzie dwustanowy przełącznik od którego będą zależały wyniki
// wtedy jeśli będzie tryb "tv" przeglądamy i zwracamy wszystko związane z serialami, a w przeciwnym wypadku filmami