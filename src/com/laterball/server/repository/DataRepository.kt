package com.laterball.server.repository

import com.laterball.server.api.model.Fixture
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

abstract class DataRepository<T> {

    protected lateinit var cache: ConcurrentHashMap<Int, T>

    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private var cacheHits = 0
    private var cacheMisses = 0

    private var initialised = false

    abstract val storedData: Map<Int, T>?

    fun getData(fixture: Fixture): T? {
        if (!initialised) loadData()
        return if (cache.containsKey(fixture.fixture_id)) {
            cacheHits++
            cache[fixture.fixture_id]
        } else {
            cacheMisses++
            logger.info("Cache hits/misses: $cacheHits/$cacheMisses")
            fetchAndCache(fixture)
        }
    }

    private fun loadData() {
        initialised = true
        val stored = storedData
        cache = if (stored != null) ConcurrentHashMap(stored) else  ConcurrentHashMap()
        logger.info("Initialised with cache size ${cache.size}")
    }

    fun removeFromCache(fixtureId: Int) {
        if (!initialised) loadData()
        cache.remove(fixtureId)
        // Caller's rsponsbility to sync database
    }

    private fun fetchAndCache(fixture: Fixture): T? {
        val result = fetch(fixture)
        result?.let { cache[fixture.fixture_id] = it }
        syncDatabase()
        return result
    }

    abstract fun syncDatabase()

    abstract fun fetch(fixture: Fixture): T?
}