package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiFixtureEvents(
    val events: List<Event>,
    val results: Int = 0
)