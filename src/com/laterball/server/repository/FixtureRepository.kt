package com.laterball.server.repository

import com.laterball.server.model.LeagueId
import com.laterball.server.api.DataApi
import com.laterball.server.api.model.ApiFixtureList
import com.laterball.server.data.Database
import com.laterball.server.model.LeagueUpdateTime
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FixtureRepository(private val dataApi: DataApi, private val database: Database, private val clock: Clock = SystemClock()) {

    private val fixtureCache: ConcurrentHashMap<LeagueId, ApiFixtureList>
    private val lastUpdatedMap = ConcurrentHashMap<LeagueId, Long>()
    private val nextFixtureUpdateTime = ConcurrentHashMap<LeagueId, Long>()
    private val logger = LoggerFactory.getLogger(FixtureRepository::class.java.name)

    init {
        val storedFixtures = database.getFixtures()
        logger.info("Retrieved ${storedFixtures.size} stored fixtures")
        fixtureCache = ConcurrentHashMap(storedFixtures)
        val storedLastUpdateTimes = database.getLastUpdatedMap()
        val storedNextUpdateTimes = database.getNextUpdatedMap()
        storedLastUpdateTimes.forEach { lastUpdatedMap[it.leagueId] = it.updateTime }
        storedNextUpdateTimes.forEach { nextFixtureUpdateTime[it.leagueId] = it.updateTime }
        logger.info("Initialised with fixtureCache size ${fixtureCache.size}")
    }

    fun getFixturesForLeague(leagueId: LeagueId): Pair<ApiFixtureList?, Long?> {
        val current = fixtureCache[leagueId]
        logger.info("Fixture cache currently contains ${current?.fixtures?.size ?: 0} fixtures")
        if (needsUpdate(leagueId)) {
            try {
                lastUpdatedMap[leagueId] = clock.time
                val updated = dataApi.getPreviousFixtures(leagueId.id)
                val next = dataApi.getNextFixtures(leagueId.id)
                next?.let {
                    val timeFormatter = DateTimeFormatter.ISO_DATE_TIME
                    val nextUpdate = next.fixtures
                        ?.map { Date.from(Instant.from(timeFormatter.parse(it.event_date))).time }
                        ?.minOrNull()
                    if (nextUpdate != null) {
                        val value = nextUpdate + 10800000L // 3 hours
                        nextFixtureUpdateTime[leagueId] = value
                        logger.info("Next update time for $leagueId is $value")
                    } else {
                        nextFixtureUpdateTime.remove(leagueId)
                    }
                }
                database.storeLastUpdatedMap(lastUpdatedMap.map { LeagueUpdateTime(it.key, it.value) })
                database.storeNextUpdatedMap(nextFixtureUpdateTime.map { LeagueUpdateTime(it.key, it.value) })

                val valid = (updated?.fixtures?.size ?: 0) > 0
                if (valid) fixtureCache[leagueId] = updated!!
                database.storeFixtures(fixtureCache)
                if (valid) {
                    return Pair(updated, nextFixtureUpdateTime[leagueId])
                } else {
                    return Pair(current, null)
                }
            } catch (e: Exception) {
                return Pair(current, null)
            }
        }
        return Pair(current, null)
    }

    private fun needsUpdate(leagueId: LeagueId): Boolean {
        logger.info("Checking if update is required based on currentTime:")
        val currentTime = clock.time
        val lastUpdate = lastUpdatedMap[leagueId] ?: 0L
        val nextUpdate = nextFixtureUpdateTime[leagueId] ?: currentTime
        return (currentTime - lastUpdate > 86400000) || (currentTime > nextUpdate)
    }
}
