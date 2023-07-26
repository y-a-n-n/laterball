package com.laterball.server.analytics

interface AnalyticsApi {
    fun incrementPageView(path: String, sessionId: String)
}