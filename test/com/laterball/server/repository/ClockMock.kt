package com.laterball.server.repository

import java.util.*

class ClockMock : Clock {
    override var dayOfYear = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
    override var time = System.currentTimeMillis()
}