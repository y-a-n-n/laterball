package com.laterball.server.repository

import com.laterball.server.api.DataApi
import com.laterball.server.api.model.Bet
import com.laterball.server.api.model.Fixture
import com.laterball.server.data.Database

class OddsRepository(private val dataApi: DataApi, private val database: Database) : DataRepository<Bet>() {
    override fun fetch(fixture: Fixture): Bet? {
        return dataApi.getOdds(fixture.fixture_id)?.odds
            ?.map { it.bookmakers }?.flatten()
            ?.map { it.bets }?.flatten()
            ?.find { it.label_id == WINNER_LABEL }
    }

    companion object {
        private const val WINNER_LABEL = 1
    }

    override val storedData: Map<Int, Bet>?
        get() = database.getOdds()

    override fun syncDatabase() {
        database.storeOdds(cache)
    }
}