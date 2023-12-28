package com.laterball.server

import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.CORS
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.*
import io.ktor.server.http.content.*
import io.ktor.server.html.respondHtml
import io.ktor.serialization.gson.*
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
import io.ktor.util.KtorExperimentalAPI
import org.koin.core.logger.Level
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

val logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(KtorExperimentalAPI::class)
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
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        if (config.property("ktor.environment").getString() == "DEV") {
            host("localhost:8080", schemes = listOf("http"))
            host("127.0.0.1:8080", schemes = listOf("http"))
            host("0.0.0.0:8080", schemes = listOf("http"))
            host("laterball.test", schemes = listOf("http"))
        }
        host("laterball.com", schemes = listOf("https"))
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

        static("/static") {
            resources("static")
        }
    }
}

