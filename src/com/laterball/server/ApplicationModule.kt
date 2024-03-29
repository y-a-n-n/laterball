package com.laterball.server

import com.laterball.server.api.ApiFootball
import com.laterball.server.api.DataApi
import com.laterball.server.data.Database
import com.laterball.server.data.MongoDataStore
import com.laterball.server.repository.*
import com.laterball.server.scheduler.CoroutineScheduler
import com.laterball.server.scheduler.Scheduler
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import org.koin.dsl.module

val appModule = module(createdAtStart = true) {
    factory<ApplicationConfig> { HoconApplicationConfig(ConfigFactory.load()) }
    factory<DataApi> { ApiFootball(get()) }
    single<Scheduler> { CoroutineScheduler() }
    single<Database> { MongoDataStore(get()) }
    single { FixtureRepository(get(), get()) }
    single { EventsRepository(get(), get()) }
    single { StatsRepository(get(), get()) }
    single { OddsRepository(get(), get()) }
    single { RatingsRepository(get(), get(), get(), get(), get()) }
    single { UserRatingRepository(get()) }
    single { HealthRepository(get()) }
}