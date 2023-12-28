package com.laterball.server.html

import io.ktor.server.application.*
import io.ktor.http.*

fun ensureCookieSet(call: ApplicationCall, domain: String?): String {
    var cookie = call.request.cookies["laterball"]
    if (cookie == null) {
        cookie = java.util.UUID.randomUUID().toString()
        call.response.cookies.append(Cookie("laterball", cookie, maxAge = 60 * 60 * 24 * 365, domain = domain))
    }
    return cookie
}