package com.laterball.server.html

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

private fun getSalt(): String {
    return List(8) { charPool.random() }.joinToString("")
}

private fun generateHmac(payload: String, salt: String, secret: String): String {
    val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKeySpec)
    val bytes = mac.doFinal((payload + salt).toByteArray())
    return bytes.toHex()
}

fun generateCsrfToken(payload: String, secret: String, providedSalt: String? = null): String {
    val salt = providedSalt ?: getSalt()
    val hmac = generateHmac(payload, salt, secret)
    return "$hmac:$salt"
}

// Not caring about timing attacks at this stage--we're not important enough
fun checkCsrfToken(payload: String, token: String, secret: String): Boolean {
    val (hmac, salt) = token.split(":")
    val expectedHmac = generateHmac(payload, salt, secret)
    return hmac == expectedHmac
}

private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }