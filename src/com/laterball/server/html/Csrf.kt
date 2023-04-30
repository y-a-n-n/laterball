package com.laterball.server.html

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getSalt(): String {
    return List(8) { charPool.random() }.joinToString("")
}

fun generateHmac(payload: String, salt: String, secret: String): String {
    val secretKeySpec = SecretKeySpec(secret.toByteArray(), "SHA-512")
    val mac = Mac.getInstance("SHA-512")
    mac.init(secretKeySpec)
    return mac.doFinal("$payload:$salt".toByteArray()).toHex()
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }