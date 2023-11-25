package com.laterball.server.html

import com.laterball.server.model.LeagueId
import com.laterball.server.repository.RatingsRepository
import com.laterball.server.repository.UserRatingRepository
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import kotlinx.html.*
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

@KtorExperimentalAPI
class Generator(private val ratingsRepository: RatingsRepository, private val userRatingRepository: UserRatingRepository, config: ApplicationConfig) {

    private val logger = LoggerFactory.getLogger(Generator::class.java)
    private val csrfSecret = config.property("ktor.security.csrfSecret").getString()
    private val baseUrl = config.propertyOrNull("ktor.deployment.baseUrl")?.getString() ?: "http://localhost:8080"

    init {
        logger.info("Using baseUrl: $baseUrl")
    }

    private fun generateHeader(html: HTML) {
        html.head {
            styleLink("/static/laterball.css")
            link(href = "https://fonts.googleapis.com/css2?family=Quicksand&display=swap", rel = "stylesheet")
            script { src = "/static/submitRating.js"; type = "text/javascript" }
        }
    }

    fun generateForLeague(html: HTML, leagueId: LeagueId, sortByDate: Boolean, cookie: String) {
        val ratings = ratingsRepository.getRatingsForLeague(leagueId, sortByDate)
        generateHeader(html)
        html.body {
            div {
                style = "width: 100%; text-align:center"
                img(src = "/static/laterball_transparent.svg")
                h2(classes = "subtitle") { +"The best football games of the week, ranked by watchability" }
                div(classes = "center") {
                    style = "width:200px"
                    a(classes = "link", href = "./about") { +"What is Laterball? ↠" }
                }
                div(classes = "center") {
                    style = "width:600px"
                    LeagueId.values().forEach {
                        if (it.enabled) {
                            h4(classes = "center") {
                                a(classes = if (it == leagueId) "fancy" else "fancy2", href = "./${it.path}") {
                                    style = "margin-left: 30px; margin-right: 30px;"
                                    +it.title
                                }
                            }
                        }
                    }
                }
            }
            br {}
            div(classes = "center") {
                style = "width:200px"
                a(
                    classes = "link",
                    target = "_blank",
                    href = "https://www.google.com/search?q=${leagueId.title}+streaming+on+demand"
                ) {
                    +"Where to watch? ↠"
                }
            }
            br {}
            div(classes = "center") {
                style = "width:500px"
                a(classes = if (sortByDate) "link" else "static", href = "./${leagueId.path}") {
                    style = "margin-left: 15px; margin-right: 15px;"
                    +"↡ Highest Rated"
                }
                a(classes = if (sortByDate) "static" else "link", href = "./${leagueId.path}?sort=date") {
                    style = "margin-left: 15px; margin-right: 15px;"
                    +"Most Recent ↡"
                }
            }
            br {}
            div(classes = "center") {
                style = "width: 100%; text-align:center"
                div(classes = "lb-container") {
                    style = "max-width: 1200px"
                    if (!ratings.isNullOrEmpty()) {
                        val format = SimpleDateFormat("EEEE, d MMMM")
                        ul(classes = "lb-ul lb-card-4") {
                            ratings.forEach { rating ->
                                val userRating = userRatingRepository.getUserRating(leagueId, rating.fixtureId, cookie)
                                li(classes = "lb-bar lb-border lb-round-xlarge fade-in") {
                                    div(classes = "center") {
                                        img(classes = "lb-bar-item lb-circle lb-hide-small", src = rating.homeLogo) {
                                            style = "height:100px"
                                        }
                                        div(classes = "lb-bar-item") {
                                            span(classes = "lb-xxlarge match") { +"${rating.homeTeam} vs ${rating.awayTeam}" }
                                            br {}
                                            span(classes = "center subtitle") { +format.format(rating.date) }
                                            br {}
                                            h5 (classes = "fade-in subtitle tooltip") {
                                                +"Show score"
                                                span(classes = "tooltiptext") { +rating.score }
                                            }
                                        }
                                        img(classes = "lb-bar-item lb-circle lb-hide-small", src = rating.awayLogo) {
                                            style = "height:100px"
                                        }
                                    }
                                    div {
                                        var starsAdded = 0
                                        for (i in 1..rating.rating.toInt()) {
                                            if (i == rating.rating.toInt() && i % 2 != 0) {
                                                img(src = "/static/half_star.svg") { style = "height:50px" }
                                                starsAdded++
                                            } else if (i % 2 == 0) {
                                                img(src = "/static/star.svg") { style = "height:50px" }
                                                starsAdded++
                                            }
                                        }
                                        for (i in (starsAdded..4)) {
                                            img(src = "/static/empty_star.svg") { style = "height:50px" }
                                        }
                                        div (classes= "center") {
                                            form(
                                                encType = FormEncType.applicationXWwwFormUrlEncoded,
                                                method = FormMethod.post
                                            ) {
                                                p {
                                                    h5(classes = "fade-in subtitle") {
                                                        +"Submit your own rating:"
                                                    }
                                                    rangeInput(name = "rating") {
                                                        classes = setOf("rating", "center")
                                                        style = "--value:2.5; background: transparent;"
                                                        id = "rating-${rating.fixtureId}"
                                                        min = "1"; max = "5"; step = "0.5"
                                                        value = (userRating ?: 6).toString()
                                                        onInput =
                                                            "this.style.setProperty('--value', this.valueAsNumber)"
                                                        disabled = userRating != null
                                                    }
                                                }
                                                p {
                                                    button(type = ButtonType.button) {
                                                        classes = setOf("button", "fade-in")
                                                        id = "button-${rating.fixtureId}"
                                                        disabled = (userRating != null)
                                                        onClick =
                                                            "submitRating(\'${baseUrl}/${leagueId.path}/rating\', ${rating.fixtureId}, \'${
                                                                generateCsrfToken(
                                                                    rating.fixtureId.toString(),
                                                                    csrfSecret
                                                                )
                                                            }\')"; +"Submit"
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    br {}
                                }
                            }
                        }
                    } else {
                        h2(classes = "subtitle") {
                            style = "width: 100%; text-align:center"
                            +"No recent games, check back later!"
                        }
                    }
                }
            }
            span(classes = "subtitle center") { +"© ${SimpleDateFormat("YYYY").format(Date())} Laterball" }
        }
    }

    fun generateAbout(html: HTML) {
        generateHeader(html)
        html.body {
            div {
                style = "width: 100%; text-align:center"
                img(src = "/static/laterball_transparent.svg")
                h2(classes = "subtitle") { +"What is Laterball?" }
                div(classes = "center") {
                    style = "width:200px"
                    a(classes = "link", href = "./") { +"↞ Home" }
                }
                h3(classes = "subtitle center") {
                    +"Love to watch football on demand? Laterball tells you which games are the best to watch this week without spoiling the score for you."
                    br {}
                    br {}
                    +"Currently, Laterball lists the best English Premier League and Champions League games of the week, ranked by watchability."
                    br {}
                    br {}
                }
            }
            span(classes = "subtitle center") { +"feedback: email laterball at yann dot software" }
            span(classes = "subtitle center") {
                a(href = "https://twitter.com/laterball", target = "_blank") {
                    style = "margin-right: 10px;"
                    img(src = "/static/twitter.png") { style = "width: 50px; height: 50px"}
                }
                a(href = "https://github.com/y-a-n-n/laterball", target = "_blank") {
                    style = "margin-left: 10px;"
                    img(src = "/static/github.png") { style = "width: 50px; height: 50px"}
                }
            }
            span(classes = "subtitle center") { +"© ${SimpleDateFormat("YYYY").format(Date())} Laterball" }
            span(classes = "subtitle center") { +"Version 3.2.11" }
        }
    }
}