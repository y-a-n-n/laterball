package com.laterball.server.data.mongo

import com.laterball.server.api.model.ApiFixtureList
import com.laterball.server.model.LeagueId

data class LeagueFixtures(val leagueId: LeagueId, val fixtureList: ApiFixtureList)
