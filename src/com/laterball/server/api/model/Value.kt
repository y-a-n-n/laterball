package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Value(
    val odd: String = "",
    val value: String = ""
)