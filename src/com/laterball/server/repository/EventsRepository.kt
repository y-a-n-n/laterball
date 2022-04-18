package com.laterball.server.repository

import com.laterball.server.api.DataApi
import com.laterball.server.api.model.ApiFixtureEvents
import com.laterball.server.api.model.Fixture
import com.laterball.server.data.Database

class EventsRepository(private val dataApi: DataApi, private val database: Database) : DataRepository<ApiFixtureEvents>() {
    override fun fetch(fixture: Fixture): ApiFixtureEvents? {
        return dataApi.getEvents(fixture.fixture_id)
    }

    override val storedData: Map<Int, ApiFixtureEvents>?
        get() = database.getEvents()

    override fun syncDatabase() {
        database.storeEvents(cache)
    }
}