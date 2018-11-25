package com.bholota.ibus

import com.bholota.ibus.frame.RawFrame
import org.junit.Test

class IBusParserTest {

    @Test
    fun single_packet_parsing() {
        val parser = IBusParser()
        val count = IBusDebugData.packets.size
        val result = mutableListOf<RawFrame>()

        for (i in 0 until count) {
            result += parser.push(IBusDebugData.packets[i].map { (it and 0xFF).toByte() }.toByteArray())
        }

        assert(result.size == 37)
    }

    @Test
    fun multiple_packet_parsing() {
        val parser = IBusParser()
        val parsedPackets = parser.push(byteArrayOf(
                0xC0.toByte(), 0x3, 0xC8.toByte(), 0x1, 0x0A,
                /*end of first packet*/
                0xC8.toByte(), 0x4, 0xFF.toByte(), 0x2, 0x0, 0x31,
                /*end of second packet*/
                0x18, 0x5, 0xFF.toByte(), 0x2, 0x0, 0x1, -31
        ))
        assert(parsedPackets.size == 3)
    }

    @Test
    fun multiple_packet_parsing_with_bad_packets() {
        val parser = IBusParser()
        val parsedPackets = parser.push(byteArrayOf(
                /*some garbage*/
                0x0, 0xFF.toByte(),
                /*first packet*/
                0xC0.toByte(), 0x3, 0xC8.toByte(), 0x1, 0x0A,
                /*end of first packet*/
                0xC8.toByte(), 0x4, 0xFF.toByte(), 0x2, 0x0, 0x31,
                /*end of second packet*/
                0x18, 0x5, 0xFF.toByte(), 0x2, 0x0, 0x1, -31,
                0x0, 0xFF.toByte() // those should stay in parser and 'wait' for next packet push
        ))
        assert(parsedPackets.size == 3)
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