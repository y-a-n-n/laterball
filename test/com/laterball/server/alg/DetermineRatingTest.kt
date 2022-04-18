package com.laterball.server.alg

import com.google.gson.Gson
import com.laterball.server.api.model.*
import org.junit.Assert.*
import org.junit.Test

class DetermineRatingTest {

    @Test
    fun testHighScoringFavouriteWin() {
        val gson = Gson()
        val fixture = gson.fromJson(readText("test/manu_leeds_fixture.json"), Fixture::class.java)
        val stats = gson.fromJson(readText("test/manu_leeds_stats.json"), Statistics::class.java)
        val odds = gson.fromJson(readText("test/manu_leeds_odds.json"), Bet::class.java)
        val events = gson.fromJson(readText("test/manu_leeds_events.json"), ApiFixtureEvents::class.java)

        val result = determineRating(fixture, odds, stats, events)

        assertEquals(885.0f, result.rating)
    }

    private fun readText(fileName: String): String {
        return DetermineRatingTest::class.java.classLoader.getResource(fileName).readText()
    }
}