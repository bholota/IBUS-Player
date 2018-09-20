package com.bholota.ibus

import com.bholota.ibus.frame.IBusFrame
import com.bholota.ibus.frame.RawFrame
import org.junit.Test

class FrameTest {

    @Test
    fun frameConversionTest() {

        val raw = RawFrame(0xC0.toByte(), 0x03, 0xC8.toByte(), listOf(0x01), 0x0A)
        val packetFromRaw = IBusFrame.fromRaw(raw)
        val ibusPacket = IBusFrame(IBusDevice.MID, IBusDevice.Phone, listOf(0x01))

        assert(packetFromRaw == ibusPacket)
        assert(raw == ibusPacket.toRaw())
    }
}