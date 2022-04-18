package com.laterball.server.twitter

import com.laterball.server.data.Database
import com.laterball.server.model.LeagueId
import com.laterball.server.model.Rating
import com.laterball.server.model.TwitterData
import com.laterball.server.repository.Clock
import com.laterball.server.repository.RatingsRepository
import com.laterball.server.repository.SystemClock
import io.ktor.config.*
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

@KtorExperimentalAPI
class TwitterBot(
        private val twitterApi: TwitterApi,
        private val database: Database,
        config: ApplicationConfig,
        ratingsRepository: RatingsRepository,
        private val clock: Clock = SystemClock()
) {

    var enabled = config.property("ktor.environment").getString() == "PROD"
    set(value) {
        field = value
        logger.info("Twitter bot ${this.hashCode()} enabled: $value")
    }

    companion object {
        private val PROMO = listOf(
                "\n\nSee all this week's watchability ratings at laterball.com",
                "\n\nFor a full list of watchability ratings, head to laterball.com",
                "\n\nTo see what else is worth watching this week, visit laterball.com",
        )

        private const val INTERVAL = 3600000L * 4 // Don't tweet more than once per four hours
    }

    private val logger = LoggerFactory.getLogger(TwitterBot::class.java)

    private var lastFixtureId: MutableList<Int>

    private var lastTweetTime: Long

    init {
        ratingsRepository.addListener { leagueId, ratings ->
            tweetForRatings(leagueId, ratings)
        }
        val twitterData = database.getTwitterData()
        lastFixtureId = ArrayList(twitterData.lastFixtureTweeted)
        while (lastFixtureId.size < LeagueId.values().size) {
            lastFixtureId.add(0)
        }
        lastTweetTime = twitterData.lastTweetTime
    }

    fun tweetForRatings(leagueId: LeagueId, ratings: List<Rating>) {
        logger.info("Received ${ratings.size} new ratings")
        ratings.maxByOrNull { it.rating }?.let {
            if (enabled) {
                logger.info("Top rating is ${it.rating}")
                sendTweet(leagueId, it)
            } else {
                logger.info("Not tweeting--not enabled yet")
            }
        }
    }

    private fun randIndex(size: Int): Int {
        val r = Random()
        return max(0, r.nextInt(size) - 1)
    }

    private fun sendTweet(leagueId: LeagueId, rating: Rating) {
        val currentTime = clock.time
        if (!lastFixtureId.contains(rating.fixtureId) && currentTime - lastTweetTime > INTERVAL) {
            getStatus(rating)?.let {
                logger.info("Tweeting: $it")
                lastTweetTime = currentTime
                lastFixtureId[leagueId.ordinal] = rating.fixtureId
                database.storeTwitterData(TwitterData(lastTweetTime, lastFixtureId))
                twitterApi.sendTweet(it)
            }
        } else {
            logger.info("Not tweeting; too soon or already tweeted this rating")
        }
    }

    private fun getStatus(rating: Rating): String? {
        if (rating.rating >= 8f) {
            val teams = "${rating.homeTeam} vs ${rating.awayTeam}"
            val words = if (rating.rating >= 10f) greatGames[randIndex(greatGames.size)] else goodGames[randIndex(goodGames.size)]
            return words.first + String.format(words.second, teams) + PROMO[randIndex(PROMO.size)]
        }
        return null
    }

    private val greatGames = listOf(
            Pair("⭐⭐⭐⭐⭐ What a game!", " Checkout %s on your preferred streaming service."),
            Pair("⭐⭐⭐⭐⭐ Five star game alert!", " Checkout %s on your preferred streaming service."),
            Pair("⭐⭐⭐⭐⭐ An instant classic?", " %s gets a great rating from us."),
            Pair("An absolute belter ⭐⭐⭐⭐⭐!", " %s is one to watch.")
    )

    private val goodGames = listOf(
            Pair("⭐⭐⭐⭐ Worth a watch!", " Checkout %s on your preferred streaming service."),
            Pair("With ⭐⭐⭐⭐ four stars,", " %s gets a great rating from us."),
            Pair("⭐⭐⭐⭐ Nice one!", " %s is one to watch.")
    )
}