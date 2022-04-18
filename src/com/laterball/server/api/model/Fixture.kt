package com.laterball.server.api.model

data class Fixture(
    val awayTeam: FixtureTeam,
    val elapsed: Int = 0,
    val event_date: String = "",
    val event_timestamp: Int = 0,
    val firstHalfStart: Int = 0,
    val fixture_id: Int = 0,
    val goalsAwayTeam: Int = 0,
    val goalsHomeTeam: Int = 0,
    val homeTeam: FixtureTeam,
    val league: League,
    val league_id: Int = 0,
    val referee: Any?,
    val round: String = "",
    val score: Score,
    val secondHalfStart: Int = 0,
    val status: String = "",
    val statusShort: String = "",
    val venue: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fixture

        if (fixture_id != other.fixture_id) return false
        if (league_id != other.league_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fixture_id
        result = 31 * result + league_id
        return result
    }
}