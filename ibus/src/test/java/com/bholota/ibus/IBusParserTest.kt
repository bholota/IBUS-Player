package com.bholota.ibus

import org.junit.Test

class IBusParserTest {

    @Test
    fun single_packet_parsing() {
        val parser = IBusParser()
        val count = IBusDebugData.packets.size
        val result = mutableListOf<RawFrame>()

        for(i in 0 until count) {
            result += parser.push(IBusDebugData.packets[i].map { (it and 0xFF).toByte() }.toByteArray())
        }

        assert(result.size == 37)
    }

    @Test
    fun single_packet_checksum() {
        val parser = IBusParser()
        var packet = RawFrame(0xC0.toByte(), 0x3, 0xC8.toByte(), listOf(0x1), 0x0A)
        assert(parser.isFrameValid(packet))

        packet = RawFrame(0xC8.toByte(), 0x4, 0xFF.toByte(), listOf(0x2, 0x0), 0x31)
        assert(parser.isFrameValid(packet))

        // modified data, packet invalid
        packet = RawFrame(0xC8.toByte(), 0x4, 0xFF.toByte(), listOf(0x2, 0x0, 0x1), 0x31)
        assert(!parser.isFrameValid(packet))

        packet = RawFrame(0xC0.toByte(), 0x3, 0x80.toByte(), listOf(0x1), 0x31)
        assert(!parser.isFrameValid(packet))
    }
}