package com.laterball.server.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Bookmaker(
    val bets: List<Bet>,
    val bookmaker_id: Int = 0,
    val bookmaker_name: String = ""
)