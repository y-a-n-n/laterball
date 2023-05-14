package com.laterball.server.repository

import com.laterball.server.data.Database
import com.laterball.server.model.LeagueId

class UserRatingRepository(private val database: Database) {
    fun storeUserRating(leagueId: LeagueId, fixtureId: Int, rating: Int, cookie: String) =
        database.storeUserRating(leagueId, fixtureId, rating, cookie)

    fun getUserRating(leagueId: LeagueId, fixtureId: Int, cookie: String): Int? =
        database.getUserRating(leagueId, fixtureId, cookie)
}