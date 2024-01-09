package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeAwayStat (
    val away: String = "",
    val home: String = ""
)