package com.laterball.server.api.model

data class Event(
    val assist: Any?,
    val assist_id: Any?,
    val comments: Any?,
    val detail: String = "",
    val elapsed: Int = 0,
    val elapsed_plus: Any?,
    val player: String = "",
    val player_id: Int = 0,
    val teamName: String = "",
    val team_id: Int = 0,
    val type: String = ""
)