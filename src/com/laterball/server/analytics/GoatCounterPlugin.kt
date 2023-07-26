package com.laterball.server.analytics

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.*

class GoatCounterFeature(configuration: Configuration) {
    val prop = configuration.prop // Copies a snapshot of the mutable config into an immutable property.

    class Configuration {
        var prop = "value" // Mutable property.
    }

    // Implements ApplicationFeature as a companion object.
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, GoatCounterFeature.Configuration, GoatCounterFeature> {
        // Creates a unique key for the feature.
        override val key = AttributeKey<GoatCounterFeature>("GoatCounterFeature")

        // Code to execute when installing the plugin.
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): GoatCounterFeature {

            // It is responsibility of the install code to call the `configure` method with the mutable configuration.
            val configuration = GoatCounterFeature.Configuration().apply(configure)

            // Create the plugin, providing the mutable configuration so the plugin reads it keeping an immutable copy of the properties.
            val feature = GoatCounterFeature(configuration)

            // Intercept a pipeline.
            pipeline.intercept(ApplicationCallPipeline.Call) {
                 val path = this.context.request.path()
                // Perform things in that interception point.
            }
            return feature
        }
    }
}