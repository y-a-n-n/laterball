package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Odd(
    val bookmakers: List<Bookmaker>,
    val fixture: OddsFixture
)