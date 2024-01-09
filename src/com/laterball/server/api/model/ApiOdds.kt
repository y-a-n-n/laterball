package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiOdds(
    val odds: List<Odd>,
    val paging: Paging,
    val results: Int = 0
)