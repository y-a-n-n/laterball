package com.laterball.server.api.model

data class Odd(
    val bookmakers: List<Bookmaker>,
    val fixture: OddsFixture
)