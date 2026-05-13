package com.js.backendassembly.data.models.dbmodels

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserList(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String? = null,
    @ServerTimestamp val creationDate: Date? = null,
    val movies: List<Int> = emptyList(),
    val series: List<Int> = emptyList(),
)