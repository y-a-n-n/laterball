package com.laterball.server.analytics

import com.laterball.server.analytics.model.Count
import com.laterball.server.analytics.model.Hit
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.collections.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

// https://www.goatcounter.com/help/api#backend-integration
@OptIn(KtorExperimentalAPI::class)
class GoatCounterApi(config: ApplicationConfig, clientOverride: HttpClient? = null) : AnalyticsApi {

    private val logger = LoggerFactory.getLogger(GoatCounterApi::class.java)

    private val baseUrl = config.property("ktor.analytics.baseUrl").getString()
    private val apiKey = config.property("ktor.analytics.apiKey").getString()

    private val batcher = ConcurrentList<Hit>();

    private val client: HttpClient = clientOverride ?: HttpClient(OkHttp) {
        install(HttpTimeout) {
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            headers.append("Authorization", "Bearer $apiKey")
            headers.append("Content-Type", ContentType.Application.Json.toString())
        }
    }

    override fun incrementPageView(hit: Hit) {
        // TODO don't block, but sync on batcher
        // TODO batch?
        batcher.add(hit)
        val count = Count(batcher)
        runBlocking {
            try {
                client.post<Void> {
                    url("${baseUrl}/count")
                    body = count
                }
                batcher.clear()
            } catch (e: Exception) {
                logger.error("Failed to log pageviews", e)
                null
            }
        }
    }

}