package com.js.backendassembly.domain.models.profiles

import com.js.backendassembly.data.models.entities.Rating
import com.js.backendassembly.data.models.dtos.shared.CastMemberDto
import com.js.backendassembly.data.models.dtos.shows.CreatedByDto
import com.js.backendassembly.data.models.dtos.shows.TvSeriesDetailsDto

class TvSeriesProfile(
    val seriesDetails: TvSeriesDetailsDto,
    override val rating: Rating? = null,
    override val containingLists: List<String> = emptyList()
) : IMediaProfile {

    fun getTop10Actors(): List<CastMemberDto> {
        return seriesDetails.credits.castMembers.take(10)
    }

    fun getCreator(): CreatedByDto {
        return seriesDetails.createdBy.first()
    }

    override fun getTopNActors(number: Int): List<CastMemberDto> {
        return seriesDetails.credits.castMembers.take(number)
    }
}