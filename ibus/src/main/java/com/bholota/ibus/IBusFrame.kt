package com.bholota.ibus

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

    companion object {
        fun fromRaw(rawFrame: RawFrame): IBusFrame? {
            return IBusFrame(
                    IBusDevice.fromByte(rawFrame.src)!!,
                    IBusDevice.fromByte(rawFrame.dst)!!,
                    rawFrame.data)
        }
    }
}