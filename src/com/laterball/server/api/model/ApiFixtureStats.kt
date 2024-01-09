package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiFixtureStats(
    val results: Int = 0,
    val statistics: Statistics
)