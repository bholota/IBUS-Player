package com.bholota.ibus

import com.bholota.ibus.frame.RawFrame
import java.lang.IllegalStateException
import kotlin.experimental.xor

class IBusParser {

    private val buffer = ArrayList<Byte>() // contains unprocessed data

    @Synchronized
    fun push(data: ByteArray): List<RawFrame> {
        buffer.addAll(data.toList())

        val result = mutableListOf<RawFrame>() // already parsed packets
        var badDataDetected = false

        while (true) {
            if (buffer.size < MIN_PACKET_LEN) return result // we have to wait for packet data
            if (buffer.size > MAX_BUFFER_LEN) {
                // if this scenario occurs too often it should be handled by removing 1,2,3,4 etc.
                // bytes from begging of the buffer to remove a garbage and retry parsing
                L.log("Buffer filled out, clearing...")
                buffer.clear()
                return result
            }
            val src = buffer[0]
            val len = buffer[1]

            if (len < 3) {
                L.log("Bad packet, try to find correct one ${buffer.toByteArray().prettyHex()}")
                if (buffer.size >= MIN_PACKET_LEN) {
                    buffer.removeAt(0) // remove first and start again
                    continue
                }
                return result // basically wait for more
            }

            // len suggest that we have to wait for more data
            if (buffer.size < (2 + len)) return result // 2 because (src + len)

            val dst = buffer[2]
            val data = buffer.subList(3, 1 + len).toList()
            val checkSum = buffer[1 + len]

            val frame = RawFrame(src, len, dst, data.toList(), checkSum)

            if (isFrameValid(frame)) {
                result += frame
                buffer.subList(0, 2 + len).clear() // remove processed data from buffer
            } else {
                L.log("Invalid frame: $frame")
                if (buffer.size >= MIN_PACKET_LEN) {
                    buffer.removeAt(0) // remove first and start again
                }
            }
        }
    }

    fun isFrameValid(frame: RawFrame): Boolean {
        var sum = frame.src xor frame.len xor frame.dst
        for (d in frame.data) {
            sum = sum xor d
        }
        return sum == frame.checkSum
    }

    companion object {
        const val MIN_PACKET_LEN = 5
        const val MAX_BUFFER_LEN = 64
    }
}