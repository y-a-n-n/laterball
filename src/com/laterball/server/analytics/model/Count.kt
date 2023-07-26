package com.laterball.server.analytics.model

data class Count(val hits: List<Hit>, val noSessions: Boolean = false)