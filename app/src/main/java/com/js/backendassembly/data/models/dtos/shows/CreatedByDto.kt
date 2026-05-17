package com.js.backendassembly.data.models.dtos.shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatedByDto(
    @SerialName("name") val name: String,
    @SerialName("profile_path") val profilePath: String? = null
)