package com.laterball.server.model

import java.util.*

class UserRating(val leagueId: LeagueId, val fixtureId: Int, val rating: Int, val cookie: String, val created: Date? = Date())