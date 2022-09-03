package com.laterball.server.data.mongo

import com.laterball.server.api.model.Bet

data class FixtureOdds(val fixtureId: Int, val odds: Bet?)
