package com.bholota.ibus

import org.junit.Test

class UtilsTest {

    @Test
    fun testPrettyHex() {
        val arr = byteArrayOf(0xff.toByte(), 0x1, 0x0)
        println(arr.prettyHex())
    }
}