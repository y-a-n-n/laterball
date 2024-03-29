package com.laterball.server.data

import com.laterball.server.api.model.ApiFixtureEvents
import com.laterball.server.api.model.ApiFixtureList
import com.laterball.server.api.model.Bet
import com.laterball.server.api.model.Statistics
import com.laterball.server.data.mongo.FixtureEvents
import com.laterball.server.data.mongo.FixtureOdds
import com.laterball.server.data.mongo.FixtureStats
import com.laterball.server.data.mongo.LeagueFixtures
import com.laterball.server.model.LeagueId
import com.laterball.server.model.LeagueUpdateTime
import com.laterball.server.model.TwitterData
import com.laterball.server.model.UserRating
import com.mongodb.ConnectionString
import com.mongodb.client.model.Filters.and
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.upsert
import org.slf4j.LoggerFactory

class MongoDataStore(config: ApplicationConfig) : Database {

    private val logger = LoggerFactory.getLogger(MongoDataStore::class.java.name)

    private val dbUser = config.property("ktor.mongo.user").getString()
    private val dbPassword = config.property("ktor.mongo.password").getString()
    private val host = config.property("ktor.mongo.host").getString()

    private val connectionString =
        "mongodb://$dbUser:$dbPassword@$host:27017/?authSource=admin&readPreference=primary&appname=laterball&directConnection=true&ssl=false"

    private val client = KMongo.createClient(ConnectionString(connectionString))

    private val database = client.coroutine.getDatabase("laterball")

    override fun storeFixtures(fixtures: Map<LeagueId, ApiFixtureList>) {
        logger.debug("Storing ${fixtures.values.size} fixtures for ${fixtures.keys.size} leagues")
        runBlocking {
            val col = database.getCollection<LeagueFixtures>("fixtures")
            val data = fixtures.map { f -> LeagueFixtures(f.key, f.value) }
            data.forEach { f -> col.updateOne(LeagueFixtures::leagueId eq f.leagueId, f, upsert()) }
        }
    }

    override fun getFixtures(): Map<LeagueId, ApiFixtureList> {
        return runBlocking {
            val col = database.getCollection<LeagueFixtures>("fixtures")
            val data = col.find().toList()
            val map = HashMap<LeagueId, ApiFixtureList>()
            data.forEach { if (it.fixtureList != null) map[it.leagueId] = it.fixtureList }
            return@runBlocking map
        }
    }

    override fun getStats(): Map<Int, Statistics> {
        return runBlocking {
            val col = database.getCollection<FixtureStats>("stats")
            val data = col.find().toList()
            val map = HashMap<Int, Statistics>()
            data.forEach { if (it.stats != null) map[it.fixtureId] = it.stats }
            return@runBlocking map
        }
    }

    override fun getEvents(): Map<Int, ApiFixtureEvents> {
        return runBlocking {
            val col = database.getCollection<FixtureEvents>("events")
            val data = col.find().toList()
            val map = HashMap<Int, ApiFixtureEvents>()
            data.forEach { if (it.events != null) map[it.fixtureId] = it.events }
            return@runBlocking map
        }
    }

    override fun getOdds(): Map<Int, Bet> {
        return runBlocking {
            val col = database.getCollection<FixtureOdds>("odds")
            val data = col.find().toList()
            val map = HashMap<Int, Bet>()
            data.forEach { if (it.odds != null) map[it.fixtureId] = it.odds }
            return@runBlocking map
        }
    }

    override fun storeStats(stats: Map<Int, Statistics>) {
        runBlocking {
            val col = database.getCollection<FixtureStats>("stats")
            val data = stats.map { f -> FixtureStats(f.key, f.value) }
            col.insertMany(data)
        }
    }

    override fun storeEvents(events: Map<Int, ApiFixtureEvents>) {
        runBlocking {
            val col = database.getCollection<FixtureEvents>("events")
            val data = events.map { f -> FixtureEvents(f.key, f.value) }
            col.insertMany(data)
        }
    }

    override fun storeOdds(odds: Map<Int, Bet>) {
        runBlocking {
            val col = database.getCollection<FixtureOdds>("odds")
            val data = odds.map { f -> FixtureOdds(f.key, f.value) }
            col.insertMany(data)
        }
    }

    override fun storeTwitterData(twitterData: TwitterData) {
        runBlocking {
            val col = database.getCollection<TwitterData>("twitter")
            col.insertOne(twitterData)
        }
    }

    override fun storeLastUpdatedMap(lastUpdated: List<LeagueUpdateTime>) {
        runBlocking {
            val col = database.getCollection<LeagueUpdateTime>("lastUpdated")
            lastUpdated.forEach { f -> col.updateOne(LeagueUpdateTime::leagueId eq f.leagueId, f, upsert()) }
        }
    }

    override fun getLastUpdatedMap(): List<LeagueUpdateTime> {
        return runBlocking {
            val col = database.getCollection<LeagueUpdateTime>("lastUpdated")
            return@runBlocking col.find().toList()
        }
    }

    override fun storeNextUpdatedMap(lastUpdated: List<LeagueUpdateTime>) {
        runBlocking {
            val col = database.getCollection<LeagueUpdateTime>("nextUpdated")
            lastUpdated.forEach { f -> col.updateOne(LeagueUpdateTime::leagueId eq f.leagueId, f, upsert()) }
        }
    }

    override fun getNextUpdatedMap(): List<LeagueUpdateTime> {
        return runBlocking {
            val col = database.getCollection<LeagueUpdateTime>("nextUpdated")
            return@runBlocking col.find().toList()
        }
    }

    override fun getTwitterData(): TwitterData {
        return runBlocking {
            val col = database.getCollection<TwitterData>("twitter")
            return@runBlocking col.find().first() ?: TwitterData()
        }
    }

    override fun storeUserRating(leagueId: LeagueId, fixtureId: Int, rating: Int, cookie: String) {
        return runBlocking {
            val col = database.getCollection<UserRating>("userRatings")
            val data = UserRating(leagueId, fixtureId, rating, cookie)
            val filter =
                and(
                    and(
                        UserRating::leagueId eq leagueId,
                        UserRating::fixtureId eq fixtureId
                    ),
                    UserRating::cookie eq cookie
                )
            // upsert the data into the collection, matching on league id, fixture id and cookie
            col.updateOne(filter, data, upsert())
        }
    }

    override fun getUserRating(leagueId: LeagueId, fixtureId: Int, cookie: String): Int? {
        return runBlocking {
            val col = database.getCollection<UserRating>("userRatings")
            val data = col.find(
                UserRating::leagueId eq leagueId,
                UserRating::fixtureId eq fixtureId,
                UserRating::cookie eq cookie
            ).first()
            return@runBlocking data?.rating
        }
    }

    override val isHealthy: Boolean
        get() = try {
            runBlocking {
                database.runCommand<Document>("{ping:1}")?.get("ok") == 1.0
            }
        } catch (e: Exception) {
            logger.error("Error checking database health", e)
            false
        }
}

