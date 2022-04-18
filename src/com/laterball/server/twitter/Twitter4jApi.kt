package com.laterball.server.twitter

import io.ktor.config.ApplicationConfig
import org.slf4j.LoggerFactory
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class Twitter4jApi(private val config: ApplicationConfig) : TwitterApi {

    private val logger = LoggerFactory.getLogger(Twitter4jApi::class.java)

    private fun consumerKey() = config.propertyOrNull("ktor.twitter.apikey")?.getString() ?: ""
    private fun consumerSecret() = config.propertyOrNull("ktor.twitter.apikeysecret")?.getString() ?: ""
    private fun accessToken() = config.propertyOrNull("ktor.twitter.accesstoken")?.getString() ?: ""
    private fun accessTokenSecret() = config.propertyOrNull("ktor.twitter.accesstokensecret")?.getString() ?: ""

    private val twitter = TwitterFactory(
            ConfigurationBuilder()
                    .setOAuthConsumerKey(consumerKey())
                    .setOAuthConsumerSecret(consumerSecret())
                    .setOAuthAccessToken(accessToken())
                    .setOAuthAccessTokenSecret(accessTokenSecret())
                    .build()
    ).instance

    override fun sendTweet(text: String) {
        try {
            twitter.updateStatus(text)
        } catch (e: TwitterException) {
            logger.error("Failed to send tweet", e)
        }
    }
}