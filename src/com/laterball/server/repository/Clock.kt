package com.laterball.server.repository

interface Clock {
    val dayOfYear: Int
    val time: Long
}