package com.laterball.server.repository

import java.util.*

class SystemClock : Clock {
    override val dayOfYear: Int
        get() {
            return Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        }
    override val time: Long
        get() = System.currentTimeMillis()
}