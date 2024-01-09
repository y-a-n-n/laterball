package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OddsFixture(
    val fixture_id: Int = 0,
    val league_id: Int = 0,
    val updateAt: Int = 0
)