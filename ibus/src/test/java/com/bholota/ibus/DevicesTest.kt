package com.bholota.ibus

import org.junit.Test

class DevicesTest {

    @Test
    fun conversion() {
        val raw: Byte = 0x18
        val device = IBusDevice.fromByte(raw)

        System.out.println("Found device: $device")

        assert(device == IBusDevice.CDPlayer)
    }
}