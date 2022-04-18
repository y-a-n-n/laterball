package com.laterball.server.repository

import com.laterball.server.api.DataApiMock
import com.laterball.server.api.model.ApiFixtureStats
import com.laterball.server.api.model.HomeAwayStat
import com.laterball.server.api.model.Statistics
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


internal class StatsRepositoryTest {
    private lateinit var statsRepository: StatsRepository
    private lateinit var dataApiMock: DataApiMock
    private lateinit var databaseMock: DatabaseMock

    @Before
    fun setUp() {
        dataApiMock = DataApiMock()
        databaseMock = DatabaseMock()
        statsRepository = StatsRepository(dataApiMock, databaseMock)
    }

    @Test
    fun testReload() {
        val fixture = FixtureRepositoryTest.randomFixture
        val stats = Statistics(
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString()),
            HomeAwayStat(Math.random().toString(), Math.random().toString())
        )
        val remoteResult = ApiFixtureStats(1, stats)
        dataApiMock.testStats = remoteResult

        val result = statsRepository.getData(fixture)
        assertEquals(remoteResult.statistics, result as Statistics)

        val newRepo = StatsRepository(dataApiMock, databaseMock)

        dataApiMock.testStats = null
        assertEquals(remoteResult.statistics, newRepo.getData(fixture) as Statistics)
    }
}