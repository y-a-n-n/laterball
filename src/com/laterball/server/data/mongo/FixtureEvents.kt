package com.laterball.server.data.mongo

import com.laterball.server.api.model.ApiFixtureEvents

data class FixtureEvents(val fixtureId: Int, val events: ApiFixtureEvents)
