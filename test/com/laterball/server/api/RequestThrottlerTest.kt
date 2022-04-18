package com.laterball.server.api

import com.laterball.server.repository.ClockMock
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RequestThrottlerTest {

    private lateinit var requestThrottler: RequestThrottler
    private lateinit var clock: ClockMock

    @Before
    fun setUp() {
        clock = ClockMock()
        requestThrottler = RequestThrottler(clock, 3)
    }

    @Test
    fun testRollover() {
        for (i in 0..200) {
            assertEquals(requestThrottler.canRequest, i < 2)
        }
        for (i in 0..200) {
            clock.dayOfYear += 1
            assertTrue(requestThrottler.canRequest)
        }
    }
}