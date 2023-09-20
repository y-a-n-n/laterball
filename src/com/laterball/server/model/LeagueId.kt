package com.laterball.server.model

enum class LeagueId(val id: Int, val title: String, val path: String) {
    EPL(5267, "English Premier League", "epl"),
    CHAMPIONS_LEAGUE(5262, "Champions League", "champions_league")
}