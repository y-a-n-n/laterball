package com.laterball.server

import com.laterball.server.api.DataApi
import com.laterball.server.html.Generator
import com.laterball.server.html.checkCsrfToken
import com.laterball.server.html.ensureCookieSet
import com.laterball.server.model.LeagueId
import com.laterball.server.model.RatingSubmission
import com.laterball.server.repository.HealthRepository
import com.laterball.server.repository.RatingsRepository
import com.laterball.server.repository.UserRatingRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.math.roundToInt

val logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    install(ContentNegotiation) {
        gson {}
    }

    install(Koin) {
        slf4jLogger(Level.ERROR)
        modules(appModule)
    }

    val ratingsRepository by inject<RatingsRepository>()
    val userRatingRepository by inject<UserRatingRepository>()
    val api by inject<DataApi>()
    val config by inject<ApplicationConfig>()
    val healthRepository by inject<HealthRepository>()

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        if (config.property("ktor.environment").getString() == "DEV") {
            allowHost("localhost:8080", schemes = listOf("http"))
            allowHost("127.0.0.1:8080", schemes = listOf("http"))
            allowHost("0.0.0.0:8080", schemes = listOf("http"))
            allowHost("laterball.test", schemes = listOf("http"))
        }
        allowHost("laterball.com", schemes = listOf("https"))
    }

    val generator = Generator(ratingsRepository, userRatingRepository, config)

    // Init data, slowly to not hit api request limits
    api.requestDelay = 15000
    LeagueId.values().forEach { leagueId -> if (leagueId.enabled) ratingsRepository.getRatingsForLeague(leagueId) }
    api.requestDelay = null
    val csrfSecret = config.property("ktor.security.csrfSecret").getString()
    val cookieDomain = config.propertyOrNull("ktor.security.cookieDomain")?.getString()

    routing {
        get("/health") {
            logger.info("/health")
            if (healthRepository.isHealthy) {
                call.respondText("OK")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Not healthy")
            }
        }

        get("/about") {
            logger.info("/about")
            call.respondHtml {
                generator.generateAbout(this)
            }
        }

        get("/") {
            call.respondRedirect("/${LeagueId.values()[0].path}")
        }

        LeagueId.values().forEach { leagueId ->
            if (leagueId.enabled) {
                get("/${leagueId.path}") {
                    logger.info("/${leagueId.path}")
                    val cookie = ensureCookieSet(call, cookieDomain)
                    val sortByDate = call.request.queryParameters["sort"] == "date"
                    call.respondHtml {
                        generator.generateForLeague(this, leagueId, sortByDate, cookie)
                    }
                }
                post("/${leagueId.path}/rating") {
                    logger.info("Received rating submission for ${leagueId.path}")
                    val cookie = call.request.cookies["laterball"]
                    if (cookie == null) {
                        logger.info("No cookie set")
                        call.respond(HttpStatusCode.Unauthorized, "Missing cookie")
                        return@post
                    }
                    val decodedCookie = java.net.URLDecoder.decode(cookie, "UTF-8")
                    val formParams = call.receive<RatingSubmission>()
                    if (checkCsrfToken(formParams.fixtureId.toString(), formParams.csrf, csrfSecret)) {
                        userRatingRepository.storeUserRating(
                            leagueId,
                            formParams.fixtureId,
                            (formParams.rating * 2).roundToInt(),
                            decodedCookie
                        )
                        call.respond(HttpStatusCode.OK)
                    } else {
                        logger.info("CSRF test failed")
                        call.respond(HttpStatusCode.Unauthorized, "CSRF test failed")
                    }
                }
            }
        }

        staticResources("/static", "static")
    }
}

