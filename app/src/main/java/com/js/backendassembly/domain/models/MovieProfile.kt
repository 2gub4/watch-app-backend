package com.js.backendassembly.domain.models

import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.dtos.CastMemberDto
import com.js.backendassembly.data.models.dtos.CrewMemberDto
import com.js.backendassembly.data.models.dtos.MovieDetailsDto

class MovieProfile(
    val movieDetails: MovieDetailsDto,
    val rating: Rating? = null,
    val containingLists: List<String> = emptyList()
) {
    fun getTop5Actors(): List<CastMemberDto> {
        return movieDetails.credits.castMembers.take(5)
    }

    fun getDirector(): CrewMemberDto {
        return movieDetails.credits.crewMembers.first { it.job == "Director" }
    }
}