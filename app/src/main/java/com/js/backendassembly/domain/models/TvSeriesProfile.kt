package com.js.backendassembly.domain.models

import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.dtos.CastMemberDto
import com.js.backendassembly.data.models.dtos.CreatedByDto
import com.js.backendassembly.data.models.dtos.TvSeriesDetailsDto

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