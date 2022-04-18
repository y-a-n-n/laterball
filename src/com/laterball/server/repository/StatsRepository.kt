package com.laterball.server.repository

import com.laterball.server.api.DataApi
import com.laterball.server.api.model.Fixture
import com.laterball.server.api.model.Statistics
import com.laterball.server.data.Database

class StatsRepository(private val dataApi: DataApi, private val database: Database) : DataRepository<Statistics>() {
    override fun fetch(fixture: Fixture): Statistics? {
        return dataApi.getStats(fixture.fixture_id)?.statistics
    }

    override val storedData: Map<Int, Statistics>?
        get() = database.getStats()

    override fun syncDatabase() {
        database.storeStats(cache)
    }
}