package com.laterball.server.api.model

data class ApiOdds(
    val odds: List<Odd>,
    val paging: Paging,
    val results: Int = 0
)