package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Bet(
    val label_id: Int = 0,
    val label_name: String = "",
    val values: List<Value>
)