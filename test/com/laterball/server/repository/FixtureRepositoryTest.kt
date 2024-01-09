package com.laterball.server.repository

import com.laterball.server.api.DataApiMock
import com.laterball.server.api.model.*
import com.laterball.server.model.LeagueId
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FixtureRepositoryTest {

    private lateinit var dataApiMock: DataApiMock
    private lateinit var clockMock: ClockMock
    private lateinit var fixtureRepository: FixtureRepository
    private lateinit var databaseMock: DatabaseMock

    @Before
    fun setUp() {
        clockMock = ClockMock()
        dataApiMock = DataApiMock()
        databaseMock = DatabaseMock()
        fixtureRepository = FixtureRepository(dataApiMock, databaseMock, clockMock)
    }

    @Test
    fun testRequestNull() {
        assertNull(fixtureRepository.getFixturesForLeague(LeagueId.EPL).first)
    }

    @Test
    fun testCached() {
        dataApiMock.testFixtures = ApiFixtureList(listOf(randomFixture, randomFixture), 2)
        assertEquals(fixtureRepository.getFixturesForLeague(LeagueId.EPL)!!.first!!.fixtures!!.size, 2)
        dataApiMock.testFixtures = ApiFixtureList(listOf(randomFixture, randomFixture, randomFixture), 3)
        assertEquals(fixtureRepository.getFixturesForLeague(LeagueId.EPL)!!.first!!.fixtures!!.size, 2)
        clockMock.time += 86400001
        assertEquals(fixtureRepository.getFixturesForLeague(LeagueId.EPL)!!.first!!.fixtures!!.size, 3)
    }

    @Test
    fun testLoadFromDb() {
        val fixtures = randomFixture
        val data = ApiFixtureList(listOf(fixtures), 1)
        dataApiMock.testFixtures = data
        assertEquals(fixtureRepository.getFixturesForLeague(LeagueId.EPL)!!.first!!.fixtures!!.size, 1)

        dataApiMock.testFixtures = null
        val newRepo = FixtureRepository(dataApiMock, databaseMock, clockMock)
        assertEquals(fixtureRepository.getFixturesForLeague(LeagueId.EPL).first, newRepo.getFixturesForLeague(LeagueId.EPL).first)
    }

    companion object {
        val randomFixture: Fixture
            get() {
                return Fixture(
                    FixtureTeam(Math.random().toString(), (1000 * Math.random()).toInt(), Math.random().toString()),
                    (1000 * Math.random()).toInt(),
                    Date().toString(),
                    (1000 * Math.random()).toInt(),
                    (1000 * Math.random()).toInt(),
                    (1000 * Math.random()).toInt(),
                    (1000 * Math.random()).toInt(),
                    FixtureTeam(Math.random().toString(), (1000 * Math.random()).toInt(), Math.random().toString()),
                    League(
                        Math.random().toString(),
                        Math.random().toString(),
                        Math.random().toString(),
                        Math.random().toString()
                    ),
                    LeagueId.EPL.id,
                    Math.random().toString(),
                    Score(
                        Math.random().toString(),
                        Math.random().toString(),
                    ),
                    Math.random().toString(),
                    Math.random().toString(),
                    Math.random().toString()
                )
            }
    }
}