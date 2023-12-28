package com.laterball.server

import io.ktor.server.config.*

class MockConfig(val values: Map<String, String>) : ApplicationConfig {

    override fun config(path: String): ApplicationConfig {
        return this
    }

    override fun configList(path: String): List<ApplicationConfig> {
        return listOf(this)
    }

    override fun property(path: String): ApplicationConfigValue {
        return object : ApplicationConfigValue {
            override fun getList(): List<String> {
                return values[path]?.let { listOf(it) } ?: emptyList()
            }

            override fun getString(): String {
                return values[path] ?: ""
            }

        }
    }

    override fun propertyOrNull(path: String): ApplicationConfigValue? {
        return values[path]?.let { property(path) }
    }
}