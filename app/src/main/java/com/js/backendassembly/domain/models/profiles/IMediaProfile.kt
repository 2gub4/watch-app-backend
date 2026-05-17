package com.js.backendassembly.domain.models.profiles

import com.js.backendassembly.data.models.entities.Rating
import com.js.backendassembly.data.models.dtos.shared.CastMemberDto

interface IMediaProfile {
    val rating: Rating?
    val containingLists: List<String>
    fun getTopNActors(number: Int): List<CastMemberDto>
}

