package com.laterball.server.twitter

class TwitterApiMock : TwitterApi {

    var sent = ArrayList<String>()

    override fun sendTweet(text: String) {
        sent.add(text)
    }
}