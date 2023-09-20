package com.laterball.server.model

enum class LeagueId(val id: Int, val title: String, val path: String, val enabled: Boolean) {
    EPL(5267, "English Premier League", "epl", true),
    CHAMPIONS_LEAGUE(5262, "Champions League", "champions_league", true),
    WWC23(5111, "Women's World Cup", "wwc23", false),
}