package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val fulltime: String? = "",
    val halftime: String? = "",
)