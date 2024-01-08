package com.laterball.server.api

import com.laterball.server.api.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.server.config.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class ApiFootball(config: ApplicationConfig, clientOverride: HttpClient? = null) : DataApi {

    companion object {
        private const val BASE_URL = "https://api-football-v1.p.rapidapi.com/v2"
    }

    private val logger = LoggerFactory.getLogger(ApiFootball::class.java)

    override var requestDelay: Long? = null

    private val requestThrottler = RequestThrottler()

    private val client: HttpClient = clientOverride ?: HttpClient(OkHttp) {
        val apiKey = config.propertyOrNull("ktor.api.apiKey")?.getString()
        install(HttpTimeout) {
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            headers.append("X-RapidAPI-Key", apiKey ?: "")
            headers.append("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")
            headers.append("Accept", ContentType.Application.Json.toString())
        }
        HttpResponseValidator {
            validateResponse { response ->
                response.headers["x-ratelimit-requests-remaining"]?.let {
                    try {
                        requestThrottler.requestsRemainingToday = it.toInt()
                    } catch (e: Exception) {
                        logger.warn("Failed to parse remaining requests header", e)
                    }
                }
            }
        }
    }

    override fun getPreviousFixtures(leagueId: Int): ApiFixtureList? {
        if (!requestThrottler.canRequest) {
            logger.error("Ran out of requests!")
            return null
        }
        return runBlocking {
            requestDelay?.let { delay(it) }
            return@runBlocking try {
                client.get("$BASE_URL/fixtures/league/$leagueId/last/30").body<FixtureList>().api
            } catch (e: Exception) {
                logger.error("Failed to fetch stats", e)
                null
            }
        }
    }

    override fun getNextFixtures(leagueId: Int): ApiFixtureList? {
        if (!requestThrottler.canRequest) {
            logger.error("Ran out of requests!")
            return null
        }
        return runBlocking {
            requestDelay?.let { delay(it) }
            return@runBlocking try {
                client.get("$BASE_URL/fixtures/league/$leagueId/next/10").body<FixtureList>().api
            } catch (e: Exception) {
                logger.error("Failed to fetch stats", e)
                null
            }
        }
    }

    override fun getStats(fixtureId: Int): ApiFixtureStats? {
        if (!requestThrottler.canRequest) {
            logger.error("Ran out of requests!")
            return null
        }
        return runBlocking {
            requestDelay?.let { delay(it) }
            return@runBlocking try {
                client.get("$BASE_URL/statistics/fixture/$fixtureId").body<FixtureStats>().api
            } catch (e: Exception) {
                logger.error("Failed to fetch stats", e)
                null
            }
        }
    }

    override fun getOdds(fixtureId: Int): ApiOdds? {
        if (!requestThrottler.canRequest) {
            logger.error("Ran out of requests!")
            return null
        }
        return runBlocking {
            requestDelay?.let { delay(it) }
            return@runBlocking try {
                client.get("$BASE_URL/odds/fixture/$fixtureId").body<Odds>().api
            } catch (e: Exception) {
                logger.error("Failed to fetch odds", e)
                null
            }
        }
    }

    override fun getEvents(fixtureId: Int): ApiFixtureEvents? {
        if (!requestThrottler.canRequest) {
            logger.error("Ran out of requests!")
            return null
        }
        return runBlocking {
            requestDelay?.let { delay(it) }
            return@runBlocking try {
                client.get("$BASE_URL/events/$fixtureId").body<FixtureEvents>().api
            } catch (e: Exception) {
                logger.error("Failed to fetch events", e)
                null
            }
        }
    }
}