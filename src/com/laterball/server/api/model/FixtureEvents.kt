package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class FixtureEvents(
    val api: ApiFixtureEvents
)