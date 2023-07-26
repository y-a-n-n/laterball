package com.laterball.server

import com.laterball.server.analytics.AnalyticsApi
import com.laterball.server.analytics.GoatCounterApi
import com.laterball.server.api.ApiFootball
import com.laterball.server.api.DataApi
import com.laterball.server.data.Database
import com.laterball.server.data.MongoDataStore
import com.laterball.server.repository.*
import com.laterball.server.scheduler.CoroutineScheduler
import com.laterball.server.scheduler.Scheduler
import com.laterball.server.twitter.Twitter4jApi
import com.laterball.server.twitter.TwitterApi
import com.laterball.server.twitter.TwitterBot
import com.typesafe.config.ConfigFactory
import io.ktor.config.ApplicationConfig
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

@OptIn(KtorExperimentalAPI::class)
val appModule = module(createdAtStart = true) {
    factory<ApplicationConfig> { HoconApplicationConfig(ConfigFactory.load()) }
    factory<DataApi> { ApiFootball(get()) }
    factory<AnalyticsApi> { GoatCounterApi(get()) }
    single<Scheduler> { CoroutineScheduler() }
    single<Database> { MongoDataStore(get()) }
    singleBy<TwitterApi, Twitter4jApi>()
    single { FixtureRepository(get(), get()) }
    single { EventsRepository(get(), get()) }
    single { StatsRepository(get(), get()) }
    single { OddsRepository(get(), get()) }
    single { TwitterBot(get(), get(), get(), get()) }
    single { RatingsRepository(get(), get(), get(), get(), get()) }
    single { UserRatingRepository(get()) }
}