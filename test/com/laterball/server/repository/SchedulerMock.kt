package com.laterball.server.repository

import com.laterball.server.model.LeagueId
import com.laterball.server.scheduler.Scheduler

class SchedulerMock : Scheduler {

    public val map = HashMap<LeagueId, Long>()

    override fun schedule(waitTime: Long, leagueId: LeagueId, callback: (LeagueId) -> Unit) {
        map[leagueId] = waitTime
    }
}