package com.laterball.server.scheduler

import com.laterball.server.model.LeagueId

interface Scheduler {
    fun schedule(waitTime: Long, leagueId: LeagueId, callback: (LeagueId) -> Unit)
}