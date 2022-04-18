package com.laterball.server.api.model

data class ApiFixtureList(
    val fixtures: List<Fixture>?,
    val results: Int = 0
)