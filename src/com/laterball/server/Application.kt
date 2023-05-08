package com.laterball.server

import com.laterball.server.api.DataApi
import com.laterball.server.html.Generator
import com.laterball.server.html.checkCsrfToken
import com.laterball.server.model.LeagueId
import com.laterball.server.model.RatingSubmission
import com.laterball.server.repository.RatingsRepository
import com.laterball.server.repository.UserRatingRepository
import io.ktor.application.*
import io.ktor.config.ApplicationConfig
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.KtorExperimentalAPI
import org.koin.core.logger.Level
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import kotlin.math.roundToInt

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
        }
        host("laterball.com", schemes = listOf("https"))
    }

    val generator = Generator(ratingsRepository, userRatingRepository, config)

    // Init data, slowly to not hit api request limits
    api.requestDelay = 15000
    ratingsRepository.getRatingsForLeague(LeagueId.EPL)
    ratingsRepository.getRatingsForLeague(LeagueId.CHAMPIONS_LEAGUE)
    api.requestDelay = null
    val csrfSecret = "1234" // config.property("ktor.security.csrfSecret").getString()

    routing {
        get("/about") {
            call.respondHtml {
                generator.generateAbout(this)
            }
        }

        get("/") {
            call.respondRedirect("/${LeagueId.EPL.path}")
        }

        LeagueId.values().forEach {leagueId ->
            get("/${leagueId.path}") {
                val sortByDate = call.request.queryParameters["sort"] == "date"
                call.respondHtml {
                    generator.generateForLeague(this, leagueId, sortByDate, call.request.origin.remoteHost)
                }
            }
            post("/${leagueId.path}/rating") {
                val formParams = call.receive<RatingSubmission>()
                if (checkCsrfToken(formParams.fixtureId.toString(), formParams.csrf, csrfSecret)) {
                    userRatingRepository.storeUserRating(
                        leagueId,
                        formParams.fixtureId,
                        (formParams.rating*2).roundToInt(),
                        call.request.origin.remoteHost
                    )
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }

        static("/static") {
            resources("static")
        }
    }
}

