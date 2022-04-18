package com.laterball.server.repository

import com.laterball.server.alg.determineRating
import com.laterball.server.api.model.Fixture
import com.laterball.server.model.LeagueId
import com.laterball.server.model.Rating
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.floor
import kotlin.math.min

class RatingsRepository(
    private val fixtureRepository: FixtureRepository,
    private val statsRepository: StatsRepository,
    private val eventsRepository: EventsRepository,
    private val oddsRepository: OddsRepository,
    private val clock: Clock = SystemClock()
) {

    private val logger = LoggerFactory.getLogger(RatingsRepository::class.java)
    private val ratingsMap = ConcurrentHashMap<LeagueId, ConcurrentHashMap<Int, Rating>>()
    private val newRatingsListeners: MutableSet<NewRatingListener> = HashSet()

    fun addListener(listener: NewRatingListener) {
        newRatingsListeners.add(listener)
    }

    fun removeListener(listener: NewRatingListener) {
        newRatingsListeners.remove(listener)
    }

    fun getRatingsForLeague(leagueId: LeagueId, sortByDate: Boolean = false): List<Rating>? {
        val leagueMap = ratingsMap[leagueId] ?: ConcurrentHashMap()
        logger.info("Map for league $leagueId has ${leagueMap.size} ratings")
        val currentTime = clock.time
        // Get completed fixtures in this league less that 1 week old
        val timeFormatter = DateTimeFormatter.ISO_DATE_TIME
        val relevantFixtures = fixtureRepository.getFixturesForLeague(leagueId)
            ?.fixtures
            ?.filter {
                it.status == STATUS_FINISHED && currentTime - Date.from(Instant.from(timeFormatter.parse(it.event_date))).time < 604_800_000
            }

        logger.info("There are ${relevantFixtures?.size} relevant fixtures at this time")

        val removeList = leagueMap.entries.filter { entry ->
            relevantFixtures?.find { entry.value.fixtureId == it.fixture_id } == null
        }.map { it.key }

        logger.info("There are ${removeList.size} old fixtures to remove")

        // Remove old data from caches
        removeList.forEach {
            leagueMap.remove(it)
            statsRepository.removeFromCache(it)
            eventsRepository.removeFromCache(it)
            oddsRepository.removeFromCache(it)
        }

        if (removeList.isNotEmpty()) {
            statsRepository.syncDatabase()
            eventsRepository.syncDatabase()
            oddsRepository.syncDatabase()
        }

        val newRatings = ArrayList<Int>()

        val ratings = relevantFixtures?.mapNotNull { fixture ->
            // Calculate the rating only if we don't already have it
            val existing = leagueMap[fixture.fixture_id]
            if (existing != null) {
                logger.info("Found existing rating for ${fixture.fixture_id}")
                existing
            } else {
                val calculated = calculateRating(fixture)
                calculated?.let { rating ->
                    leagueMap[fixture.fixture_id] = rating
                    newRatings.add(rating.fixtureId)
                }
                calculated
            }
        }?.sortedByDescending { it.rating }

        // Cache before normalisation
        ratingsMap[leagueId] = leagueMap

        // Normalise the ratings
        val normed = if (!ratings.isNullOrEmpty()) normalize(ratings) else emptyList()

        // Pick out the new ones
        val normedNew = normed.filter { rating -> newRatings.contains(rating.fixtureId) }

        // Inform listeners what's new
        newRatingsListeners.forEach { it.invoke(leagueId, normedNew) }

        return if (sortByDate) normed.toMutableList().sortedByDescending { it.date.time } else normed
    }

    private fun normalize(ratings: List<Rating>): List<Rating> {
        val maxRating = ratings[0].rating
        // 5 goals (or xG) is roughly 5 starts as a starting point
        val maxStars = min(ratings[0].goalsStat * 2, 10f)
        val starsFactor = maxStars / maxRating
        return ratings.map {
            val normRating = floor(it.rating * starsFactor).coerceAtLeast(1f)
            Rating(
                it.fixtureId,
                it.homeTeam,
                it.awayTeam,
                it.date,
                it.homeLogo,
                it.awayLogo,
                normRating,
                it.score,
                it.goalsStat
            )
        }
    }

    private fun calculateRating(fixture: Fixture): Rating? {
        logger.info("Cache miss! Calculating new rating for fixture ${fixture.fixture_id} ${fixture.homeTeam} - ${fixture.awayTeam}")
        val stats = statsRepository.getData(fixture)
        val odds = oddsRepository.getData(fixture)
        val events = eventsRepository.getData(fixture)
        return if (stats != null && odds != null && events != null) {
            determineRating(fixture, odds, stats, events)
        } else {
            null
        }
    }

    companion object {
        private const val STATUS_FINISHED = "Match Finished"
    }
}

typealias NewRatingListener = (LeagueId, List<Rating>) -> Unit
