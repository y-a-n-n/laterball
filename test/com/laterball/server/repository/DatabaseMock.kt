package com.laterball.server.repository

import com.google.gson.Gson
import com.laterball.server.api.model.*
import com.laterball.server.data.Database
import com.laterball.server.model.LeagueId
import com.laterball.server.model.LeagueUpdateTime
import com.laterball.server.model.TwitterData

class DatabaseMock(override val isHealthy: Boolean = true) : Database {

    private val map = HashMap<String, HashMap<String, String>>()
    private val gson = Gson()

    init {
        map["fixtures"] = HashMap()
        map["stats"] = HashMap()
        map["events"] = HashMap()
        map["odds"] = HashMap()
    }

    override fun storeFixtures(fixtures: Map<LeagueId, ApiFixtureList>) {
        fixtures.forEach {
            map["fixtures"]!![it.key.name] = gson.toJson(it.value)
        }
    }

    override fun getFixtures(): Map<LeagueId, ApiFixtureList> {
        val decoded = HashMap<LeagueId, ApiFixtureList>()
        LeagueId.values().forEach { leagueId ->
            map["fixtures"]!![leagueId.name]?.let {
                decoded[leagueId] = gson.fromJson(it, ApiFixtureList::class.java)
            }
        }
        return decoded
    }

    override fun getStats(): Map<Int, Statistics> {
        val decoded = HashMap<Int, Statistics>()
        map["stats"]?.entries?.forEach { entry ->
            val stat = gson.fromJson(entry.value, Statistics::class.java)
            decoded[entry.key.toInt()] = stat
        }
        return decoded
    }

    override fun getEvents(): Map<Int, ApiFixtureEvents> {
        val decoded = HashMap<Int, ApiFixtureEvents>()
        map["events"]?.entries?.forEach { entry ->
            val stat = gson.fromJson(entry.value, ApiFixtureEvents::class.java)
            decoded[entry.key.toInt()] = stat
        }
        return decoded
    }

    override fun getOdds(): Map<Int, Bet> {
        val decoded = HashMap<Int, Bet>()
        map["odds"]?.entries?.forEach { entry ->
            val stat = gson.fromJson(entry.value, Bet::class.java)
            decoded[entry.key.toInt()] = stat
        }
        return decoded
    }

    override fun storeStats(stats: Map<Int, Statistics>) {
        stats.forEach {
            map["stats"]!![it.key.toString()] = gson.toJson(it.value)
        }
    }

    override fun storeEvents(events: Map<Int, ApiFixtureEvents>) {
        events.forEach {
            map["events"]!![it.key.toString()] = gson.toJson(it.value)
        }
    }

    override fun storeOdds(odds: Map<Int, Bet>) {
        odds.forEach {
            map["odds"]!![it.key.toString()] = gson.toJson(it.value)
        }
    }

    override fun storeTwitterData(twitterData: TwitterData) {

    }

    override fun storeLastUpdatedMap(lastUpdated: List<LeagueUpdateTime>) {

    }

    override fun getLastUpdatedMap(): List<LeagueUpdateTime> {
        return emptyList()
    }

    override fun storeNextUpdatedMap(lastUpdated: List<LeagueUpdateTime>) {
    }

    override fun getNextUpdatedMap(): List<LeagueUpdateTime> {
        return emptyList()
    }

    override fun getTwitterData(): TwitterData {
        return TwitterData()
    }

    override fun storeUserRating(leagueId: LeagueId, fixtureId: Int, rating: Int, cookie: String) {
    }

    override fun getUserRating(leagueId: LeagueId, fixtureId: Int, cookie: String): Int? {
        return null
    }
}