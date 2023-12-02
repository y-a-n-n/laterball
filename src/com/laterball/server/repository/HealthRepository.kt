package com.laterball.server.repository

import com.laterball.server.data.Database

class HealthRepository(private val database: Database, private val clock: Clock = SystemClock())  {

    private var cache: Boolean = false

    @Volatile
    private var lastCheckTime = 0L

    val isHealthy: Boolean
        get() {
            if (clock.time - lastCheckTime > 30000) {
                lastCheckTime = clock.time
                cache = database.isHealthy
            }
            return cache
        }
}