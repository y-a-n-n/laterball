package com.laterball.server.api

import com.laterball.server.repository.Clock
import com.laterball.server.repository.SystemClock
import org.slf4j.LoggerFactory

class RequestThrottler(private val clock: Clock = SystemClock(), private val maxPerDay: Int = 100) {

    private val logger = LoggerFactory.getLogger(RequestThrottler::class.java)

    private var lastRolloverDay = clock.dayOfYear

    var requestsRemainingToday = maxPerDay

    val canRequest: Boolean get() {
        return if (lastRolloverDay == clock.dayOfYear) {
            requestsRemainingToday--
            if (requestsRemainingToday <= 0) logger.warn("Today: ${clock.dayOfYear}, rollover: $lastRolloverDay")
            if (requestsRemainingToday % 20 == 0) logger.info("$requestsRemainingToday requests remain")
            requestsRemainingToday > 0
        } else {
            lastRolloverDay = clock.dayOfYear
            requestsRemainingToday = maxPerDay - 1
            true
        }
    }
}