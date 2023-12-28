package com.laterball.server.repository

import io.ktor.client.plugins.gson.GsonSerializer
import io.ktor.server.config.MapApplicationConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.json.GsonSerializer
import io.ktor.client.plugins.json.JsonFeature
import com.laterball.server.api.ApiFootball
import com.laterball.server.api.DataApi
import com.laterball.server.model.LeagueId
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class RatingsRepositoryTest {

    private lateinit var dataApi: DataApi
    private lateinit var clockMock: ClockMock
    private lateinit var client: HttpClient
    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var databaseMock: DatabaseMock
    private lateinit var schedulerMock: SchedulerMock

    @Before
    fun setUp() {
        clockMock = ClockMock()
        databaseMock = DatabaseMock()
        val mockData = getResourceAsText("test/mockdata.txt").lines()
        var req = -1

        client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    req++
                    val data = mockData[req]
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(ByteReadChannel(data.toByteArray(Charsets.UTF_8)), headers = responseHeaders)
                }
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            install(DefaultRequest) {
                headers.append("Accept", ContentType.Application.Json.toString())
            }

        }
        val config = MapApplicationConfig().apply {
            put("ktor.api.apiKey", "foobar")
        }
        dataApi = ApiFootball(config, client)
        val fixtureRepository = FixtureRepository(dataApi, databaseMock, clockMock)
        val statsRepository = StatsRepository(dataApi, databaseMock)
        val eventsRepository = EventsRepository(dataApi, databaseMock)
        val oddsRepository = OddsRepository(dataApi, databaseMock)
        val schedulerMock = SchedulerMock()

        ratingsRepository = RatingsRepository(
            fixtureRepository,
            statsRepository,
            eventsRepository,
            oddsRepository,
            schedulerMock,
            clockMock
        )
    }

    @Test
    fun testRatings() {
        clockMock.time = 1603065600000L
        val ratings1 = ratingsRepository.getRatingsForLeague(LeagueId.EPL)
        assertEquals(10, ratings1!!.size)
        val ratings2 = ratingsRepository.getRatingsForLeague(LeagueId.CHAMPIONS_LEAGUE)
        assertEquals(0, ratings2!!.size)
        val ratings3 = ratingsRepository.getRatingsForLeague(LeagueId.EPL)
        assertEquals(10, ratings3!!.size)
    }

    private fun getResourceAsText(path: String): String {
        return RatingsRepositoryTest::class.java.classLoader.getResource(path).readText()
    }
}