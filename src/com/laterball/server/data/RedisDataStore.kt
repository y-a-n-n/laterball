package com.laterball.server.data

import com.google.gson.Gson
import com.laterball.server.api.model.ApiFixtureEvents
import com.laterball.server.api.model.ApiFixtureList
import com.laterball.server.api.model.Bet
import com.laterball.server.api.model.Statistics
import com.laterball.server.model.LeagueId
import com.laterball.server.model.TwitterData
import io.ktor.config.*
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPooled




class RedisDataStore(config: ApplicationConfig) : Database {

    private val logger = LoggerFactory.getLogger(RedisDataStore::class.java.name)

    private val host = config.property("ktor.mongo.host").getString()
    private val port = config.property("ktor.mongo.port").getString().toInt()

    private val jedis = JedisPooled(host, port)

    private val gson = Gson()

    override fun storeFixtures(fixturesByLeague: Map<LeagueId, ApiFixtureList>) {
        fixturesByLeague.forEach { f ->
            run {
                logger.debug("Storing {} fixtures for {}", f.value.fixtures?.size, f.key)
                jedis.set("FIXTURES::${f.key.name}", gson.toJson(f.value))
            }
        }
    }

    override fun getFixtures(): Map<LeagueId, ApiFixtureList> {
        val map = HashMap<LeagueId, ApiFixtureList>()
        jedis.keys("FIXTURES:*").forEach { k ->
            run {
                val leagueIdName = k.split("::")[1];
                map[LeagueId.valueOf(leagueIdName)] = gson.fromJson(jedis.get(k), ApiFixtureList::class.java)
            }
        }
        return map
    }

    override fun getStats(): Map<Int, Statistics> {
        val map = HashMap<Int, Statistics>()
        jedis.keys("STATS:*").forEach { k ->
            run {
                val fixtureId = k.split("::")[1].toInt();
                map[fixtureId] = gson.fromJson(jedis.get(k), Statistics::class.java)
            }
        }
        return map
    }

    override fun getEvents(): Map<Int, ApiFixtureEvents> {
        val map = HashMap<Int, ApiFixtureEvents>()
        jedis.keys("EVENTS:*").forEach { k ->
            run {
                val fixtureId = k.split("::")[1].toInt();
                map[fixtureId] = gson.fromJson(jedis.get(k), ApiFixtureEvents::class.java)
            }
        }
        return map
    }

    override fun getOdds(): Map<Int, Bet> {
        val map = HashMap<Int, Bet>()
        jedis.keys("ODDS:*").forEach { k ->
            run {
                val fixtureId = k.split("::")[1].toInt();
                map[fixtureId] = gson.fromJson(jedis.get(k), Bet::class.java)
            }
        }
        return map
    }

    override fun storeStats(stats: Map<Int, Statistics>) {
        stats.forEach { s ->
            run {
                logger.debug("Storing stats for fixture {}", s.key)
                jedis.set("STATS::${s.key}", gson.toJson(s.value))
            }
        }
    }

    override fun storeEvents(events: Map<Int, ApiFixtureEvents>) {
        TODO("Not yet implemented")
    }

    override fun storeOdds(odds: Map<Int, Bet>) {
        TODO("Not yet implemented")
    }

    override fun storeTwitterData(twitterData: TwitterData) {
        TODO("Not yet implemented")
    }

    override fun getTwitterData(): TwitterData {
        TODO("Not yet implemented")
    }
}