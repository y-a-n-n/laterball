package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Paging(
    val current: Int = 0,
    val total: Int = 0
)