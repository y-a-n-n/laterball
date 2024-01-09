package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val detail: String = "",
    val elapsed: Int = 0,
    val player: String = "",
    val player_id: Int = 0,
    val teamName: String = "",
    val team_id: Int = 0,
    val type: String = ""
)