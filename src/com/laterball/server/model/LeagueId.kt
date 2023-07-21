package com.laterball.server.model

enum class LeagueId(val id: Int, val title: String, val path: String) {
    WWC23(5111, "Women's World Cup", "wwc23"),
    EPL(4335, "English Premier League", "epl"),
    CHAMPIONS_LEAGUE(4314, "Champions League", "champions_league")
}