package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class FixtureTeam(
    val logo: String = "",
    val team_id: Int = 0,
    val team_name: String = ""
)