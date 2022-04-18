package com.laterball.server.model

data class TwitterData (
    val lastTweetTime: Long = 0,
    val lastFixtureTweeted: List<Int> = emptyList()
)