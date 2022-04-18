package com.laterball.server.model

import java.util.*

data class Rating(
    val fixtureId: Int,
    val homeTeam: String,
    val awayTeam: String,
    val date: Date,
    val homeLogo: String,
    val awayLogo: String,
    var rating: Float,
    val score: String,
    val goalsStat: Float
)