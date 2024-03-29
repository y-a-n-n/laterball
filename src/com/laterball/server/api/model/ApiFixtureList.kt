package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiFixtureList(
    val fixtures: List<Fixture>?,
    val results: Int = 0
)