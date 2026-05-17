package com.js.backendassembly.domain.models

data class MediaOverview(
    val id: Int,
    val titleOrName: String,
    val posterPath: String? = null
)