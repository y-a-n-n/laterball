package com.laterball.server.twitter

import com.laterball.server.api.ApiFootball
import com.laterball.server.api.DataApi
import com.laterball.server.model.LeagueId
import com.laterball.server.model.Rating
import com.laterball.server.repository.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.config.MapApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class TwitterBotTest {

    private lateinit var databaseMock: DatabaseMock
    private lateinit var dataApi: DataApi
    private lateinit var clockMock: ClockMock
    private lateinit var client: HttpClient
    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var twitterBot: TwitterBot
    private lateinit var twitterApiMock: TwitterApiMock

    @OptIn(KtorExperimentalAPI::class)
    @Before
    fun setUp() {
        databaseMock = DatabaseMock()
        clockMock = ClockMock()
        databaseMock = DatabaseMock()
        val config = MapApplicationConfig().apply {
            put("ktor.api.apiKey", "foobar")
        }
        client = HttpClient(MockEngine) {
            engine {
                addHandler { respond("") }
            }
        }
        dataApi = ApiFootball(config, client)
        val fixtureRepository = FixtureRepository(dataApi, databaseMock, clockMock)
        val statsRepository = StatsRepository(dataApi, databaseMock)
        val eventsRepository = EventsRepository(dataApi, databaseMock)
        val oddsRepository = OddsRepository(dataApi, databaseMock)

        twitterApiMock = TwitterApiMock()
        ratingsRepository = RatingsRepository(fixtureRepository, statsRepository, eventsRepository, oddsRepository)
        twitterBot = TwitterBot(twitterApiMock, databaseMock, ratingsRepository, clockMock)
    }

    @Test
    fun greatGame() {
        for (i in 0..100) {
            val rating1 = Rating(1 * (i + 1), "Foo", "Bar", Date(), "", "", 10f, "", 1f)
            val rating2 = Rating(2 * (i + 1), "Fizz", "Buss", Date(), "", "", 8f, "", 1f)
            val rating3 = Rating(3 * (i + 1), "Foo2", "Bar2", Date(), "", "", 4f, "", 1f)
            clockMock.time += 3600001L * 4
            twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating1, rating2, rating3))
            assertEquals(1, twitterApiMock.sent.size)
            assertTrue(twitterApiMock.sent[0].contains("Foo vs Bar"))
            twitterApiMock.sent.clear()
        }
    }

    @Test
    fun dontTweetTooOften() {

        clockMock.time += 3600001L * 4

        val rating2 = Rating(6,"Fizz", "Buss", Date(), "", "", 8f, "", 1f)
        val rating3 = Rating(7, "Foo2", "Bar2", Date(), "", "", 4f, "", 1f)
        twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating2, rating3))
        assertEquals(1, twitterApiMock.sent.size)
        assertTrue(twitterApiMock.sent[0].contains("Fizz vs Buss"))

        clockMock.time += 30000

        val rating4 = Rating(8,"Fizz", "Buss", Date(), "", "", 8f, "", 1f)
        val rating5 = Rating(9, "Foo2", "Bar2", Date(), "", "", 4f, "", 1f)

        twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating4, rating5))
        assertEquals(1, twitterApiMock.sent.size)
        assertTrue(twitterApiMock.sent[0].contains("Fizz vs Buss"))

        clockMock.time += 3600001L * 4

        val rating6 = Rating(6538,"Fizz", "Buss", Date(), "", "", 8f, "", 1f)
        val rating7 = Rating(9333, "Foo2", "Bar2", Date(), "", "", 4f, "", 1f)

        twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating6, rating7))
        assertEquals(2, twitterApiMock.sent.size)
        assertTrue(twitterApiMock.sent[1].contains("Fizz vs Buss"))
    }

    @Test
    fun dontTweetSameTwice() {
        clockMock.time += 3600001L * 4

        val rating2 = Rating(6,"Fizz", "Buss", Date(), "", "", 8f, "", 1f)
        val rating3 = Rating(7, "Foo2", "Bar2", Date(), "", "", 4f, "", 1f)
        twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating2, rating3))
        assertEquals(1, twitterApiMock.sent.size)
        assertTrue(twitterApiMock.sent[0].contains("Fizz vs Buss"))

        clockMock.time += 3600001L * 4

        twitterApiMock.sent.clear()
        twitterBot.tweetForRatings(LeagueId.CHAMPIONS_LEAGUE, listOf(rating2, rating3))
        assertEquals(0, twitterApiMock.sent.size)
    }
}