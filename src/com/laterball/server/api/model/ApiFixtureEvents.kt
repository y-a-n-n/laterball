package com.laterball.server.api.model

data class ApiFixtureEvents(
    val events: List<Event>,
    val results: Int = 0
)