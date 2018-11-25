package com.bholota.ibus

fun ByteArray.prettyHex(): String {
    return this.toUByteArray().map {
        "0x" + it.toString(16).toUpperCase()
    }.joinToString(", ", "[", "]")
}