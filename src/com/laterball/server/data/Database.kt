package com.laterball.server.data

import com.laterball.server.api.model.*
import com.laterball.server.model.LeagueId
import com.laterball.server.model.LeagueUpdateTime
import com.laterball.server.model.TwitterData

interface Database {
    fun storeFixtures(fixtures: Map<LeagueId, ApiFixtureList>)
    fun getFixtures(): Map<LeagueId, ApiFixtureList>
    fun getStats(): Map<Int, Statistics>
    fun getEvents(): Map<Int, ApiFixtureEvents>
    fun getOdds(): Map<Int, Bet>
    fun storeStats(stats: Map<Int, Statistics>)
    fun storeEvents(events: Map<Int, ApiFixtureEvents>)
    fun storeOdds(odds: Map<Int, Bet>)
    fun storeTwitterData(twitterData: TwitterData)
    fun storeLastUpdatedMap(lastUpdated: List<LeagueUpdateTime>)
    fun getLastUpdatedMap(): List<LeagueUpdateTime>
    fun storeNextUpdatedMap(lastUpdated: List<LeagueUpdateTime>)
    fun getNextUpdatedMap(): List<LeagueUpdateTime>
    fun getTwitterData(): TwitterData
    fun storeUserRating(leagueId: LeagueId, fixtureId: Int, rating: Int, ip: String)
    fun getUserRating(leagueId: LeagueId, fixtureId: Int, ip: String): Int?
}