package com.laterball.server.html

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsrfTest {

    @Test
    fun generateCsrfToken() {
        val expected = "ac6fa5be17a835313f528eb95a4d482cfd656e53927860f488e6aff4d349e13b:Igi7ckXe"
        val payload = "868193"
        val actual = generateCsrfToken(payload, "adfR%45^$%3sdf", "Igi7ckXe")
        assertEquals(expected, actual)
    }

    @Test
    fun checkCsrfToken() {
        val payload = "868193"
        val actual = checkCsrfToken(
            payload,
            "ac6fa5be17a835313f528eb95a4d482cfd656e53927860f488e6aff4d349e13b:Igi7ckXe",
            "adfR%45^$%3sdf")
        assertTrue(actual)
    }
}