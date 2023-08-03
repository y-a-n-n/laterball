package com.laterball.server.analytics

import com.laterball.server.analytics.model.Hit
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.util.*
import org.slf4j.LoggerFactory

class GoatCounterFeature {


    class Configuration {
        var analyticsApi: AnalyticsApi? = null
    }

    // Implements ApplicationFeature as a companion object.
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, GoatCounterFeature> {
        // Creates a unique key for the feature.
        override val key = AttributeKey<GoatCounterFeature>("GoatCounterFeature")
        private val logger = LoggerFactory.getLogger(GoatCounterFeature::class.java)

        // Code to execute when installing the plugin.
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): GoatCounterFeature {
            logger.info("Installing GoatCounterFeature");
            // It is responsibility of the install code to call the `configure` method with the mutable configuration.
            val configuration = Configuration().apply(configure)
            val api = configuration.analyticsApi!! // Copies a snapshot of the mutable config into an immutable property.

            // Create the plugin, providing the mutable configuration so the plugin reads it keeping an immutable copy of the properties.
            val feature = GoatCounterFeature()

            // Intercept a pipeline.
            pipeline.intercept(ApplicationCallPipeline.Call) {
                val path = this.context.request.path()
                if (path.startsWith("/static") || path.startsWith("/favicon.ico")) {
                    return@intercept
                }
                val hit = Hit(path, call.request.cookies["laterball"] ?: "ANON", this.call.request.origin.remoteHost, this.context.request.queryString(), this.call.request.userAgent() ?: "Unknown")
                api.incrementPageView(hit)
            }
            return feature
        }
    }
}