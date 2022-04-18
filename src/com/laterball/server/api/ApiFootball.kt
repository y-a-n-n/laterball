package com.laterball.server.api

import com.laterball.server.api.model.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.url
import io.ktor.client.request.get
import io.ktor.config.ApplicationConfig
import io.ktor.http.ContentType
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@KtorExperimentalAPI
class ApiFootball(config: ApplicationConfig, clientOverride: HttpClient? = null) : DataApi {

    companion object {
        private const val BASE_URL = "https://api-football-v1.p.rapidapi.com/v2"
    }

    private val logger = LoggerFactory.getLogger(ApiFootball::class.java)

    override var requestDelay: Long? = null

    private val requestThrottler = RequestThrottler()

    private val client: HttpClient = clientOverride ?: HttpClient(OkHttp){
        val apiKey = config.propertyOrNull("ktor.api.apiKey")?.getString()
        install(HttpTimeout) {
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
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
                client.get<FixtureList> {
                    url("$BASE_URL/fixtures/league/$leagueId/last/30")
                }.api
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
                client.get<FixtureList> {
                    url("$BASE_URL/fixtures/league/$leagueId/next/10")
                }.api
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
                client.get<FixtureStats> {
                    url("$BASE_URL/statistics/fixture/$fixtureId")
                }.api
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
                client.get<Odds> {
                    url("$BASE_URL/odds/fixture/$fixtureId")
                }.api
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
                client.get<FixtureEvents> {
                    url("$BASE_URL/events/$fixtureId")
                }.api
            } catch (e: Exception) {
                logger.error("Failed to fetch events", e)
                null
            }
        }
    }
}