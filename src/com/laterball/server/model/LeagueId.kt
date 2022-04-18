package com.laterball.server.model

enum class LeagueId(val id: Int, val title: String, val path: String) {
    EPL(3456, "English Premier League", "epl"),
    CHAMPIONS_LEAGUE(3431, "Champions League", "champions_league")
}