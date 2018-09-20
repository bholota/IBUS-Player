package com.bholota.ibus.frame

import com.bholota.ibus.IBusDevice
import kotlin.experimental.xor


class IBusFrame(val src: IBusDevice, val dst: IBusDevice, val data: List<Byte>) {

    val len: Byte
        get() = (2 + data.size).toByte()

    val checkSum: Byte
        get() {
            var sum = src.code xor len xor dst.code
            for (d in data) {
                sum = sum xor d
            }
            return sum
        }

    fun toRaw(): RawFrame = RawFrame(src.code, len, dst.code, data, checkSum)

    fun toByteArray(): ByteArray {
        return byteArrayOf(src.code, len, dst.code, *data.toByteArray(), checkSum)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IBusFrame

        if (src != other.src) return false
        if (dst != other.dst) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = src.hashCode()
        result = 31 * result + dst.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }

    companion object {
        fun fromRaw(rawFrame: RawFrame): IBusFrame {
            return IBusFrame(
                    IBusDevice.fromByte(rawFrame.src)!!,
                    IBusDevice.fromByte(rawFrame.dst)!!,
                    rawFrame.data)
        }
    }
}