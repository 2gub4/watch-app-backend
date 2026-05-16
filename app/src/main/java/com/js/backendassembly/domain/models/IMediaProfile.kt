package com.js.backendassembly.domain.models

import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.dtos.CastMemberDto

interface IMediaProfile {
    val rating: Rating?
    val containingLists: List<String>
    fun getTopNActors(number: Int): List<CastMemberDto>
}

