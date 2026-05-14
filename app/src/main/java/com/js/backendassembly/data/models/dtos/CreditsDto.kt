package com.js.backendassembly.data.models.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsDto(
    //@SerialName("id") val id: Int,
    @SerialName("cast") val castMembers: List<CastMemberDto> = emptyList(),
    @SerialName("crew") val crewMembers: List<CrewMemberDto> = emptyList(),

)

@Serializable
data class CastMemberDto(
    @SerialName("name") val name: String,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("character") val character: String,
)

@Serializable
data class CrewMemberDto(
    @SerialName("name") val name: String,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("department") val department: String,
    @SerialName("job") val job: String,
)