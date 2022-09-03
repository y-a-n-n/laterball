package com.laterball.server.api.model

data class Score(
    val extratime: Any?,
    val fulltime: String? = "",
    val halftime: String? = "",
    val penalty: Any?
)