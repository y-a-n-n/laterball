package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class League(
    val country: String = "",
    val flag: String? = "",
    val logo: String = "",
    val name: String = ""
)