package com.laterball.server.scheduler

import com.laterball.server.model.LeagueId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Schedules a recalculation of fixtures.
 * This will only really work well in a single instance scenario
 */
class CoroutineScheduler : Scheduler {

    private val logger = LoggerFactory.getLogger(CoroutineScheduler::class.java)
    private val callbackMap = ConcurrentHashMap<LeagueId, Job>()

    override fun schedule(waitTime: Long, leagueId: LeagueId, callback: (LeagueId) -> Unit) {
        logger.info("Scheduling recalculation of ratings for $leagueId in $waitTime milliseconds")
        callbackMap[leagueId]?.cancel()
        callbackMap[leagueId] = CoroutineScope(Job()).launch {
            delay(waitTime)
            logger.info("Performing scheduled recalculation for $leagueId")
            callback.invoke(leagueId)
        }
    }
}