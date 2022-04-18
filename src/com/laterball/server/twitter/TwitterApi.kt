package com.laterball.server.twitter

interface TwitterApi {
    fun sendTweet(text: String)
}