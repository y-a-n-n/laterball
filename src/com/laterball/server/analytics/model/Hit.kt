package com.laterball.server.analytics.model

data class Hit(val path: String, val sessionId: String, val ip: String, val query: String, val user_agent: String)