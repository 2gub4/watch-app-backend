package com.js.backendassembly.domain.models

import com.js.backendassembly.data.models.dbmodels.Rating
import com.js.backendassembly.data.models.dtos.MovieDetailsDto

class MovieProfile(
    val movieDetails: MovieDetailsDto,
    val rating: Rating? = null,
    val containingLists: List<String> = emptyList()
) {}