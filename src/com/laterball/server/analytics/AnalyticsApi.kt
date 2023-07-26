package com.laterball.server.analytics

import com.laterball.server.analytics.model.Hit

interface AnalyticsApi {
    fun incrementPageView(hit: Hit)
}