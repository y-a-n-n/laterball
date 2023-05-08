package com.laterball.server.repository

import com.laterball.server.data.Database
import com.laterball.server.model.LeagueId

class UserRatingRepository(private val database: Database) {
    fun storeUserRating(leagueId: LeagueId, fixtureId: Int, rating: Int, ip: String) =
        database.storeUserRating(leagueId, fixtureId, rating, ip)

    fun getUserRating(leagueId: LeagueId, fixtureId: Int, ip: String): Int? =
        database.getUserRating(leagueId, fixtureId, ip)
}