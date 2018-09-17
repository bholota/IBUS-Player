package com.bholota.ibusplayer.uart

import android.os.Handler

class MockedUartConnection(override val dataListener: (ByteArray) -> Unit) : UartConnection {

    override val devicesList: List<String> = listOf("DEBUG_DEVICE")

    private var isDeviceOpen = false

    private var thread: Thread? = null

    private var position = 0

    private var handler = Handler()

    override fun openDevice(deviceName: String) {
        thread = Thread {
            try {
                while (true) {
                    val frame = packets[position % packets.size]
                    handler.post { dataListener(frame.map { (it and 0xFF).toByte() }.toByteArray()) }
                    Thread.sleep(1000)
                    position++
                }
            } catch (_: InterruptedException) {
            }
        }
        thread!!.start()
        isDeviceOpen = true
    }

    override fun closeDevice() {
        thread!!.interrupt()
        isDeviceOpen = false
    }

    override fun isOpen(): Boolean = isDeviceOpen

    override fun writeData(data: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun readData(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val packets = listOf(
                listOf(0xC0, 0x03, 0xC8, 0x01, 0x0A),
                listOf(0xC8, 0x04, 0xFF, 0x02, 0x00, 0x31),
                listOf(0xC0, 0x03, 0x80, 0x01),
                listOf(0x42, 0x80, 0x04, 0xBF, 0x02, 0x00, 0x39),
                listOf(0xC0, 0x03, 0x68, 0x01),
                listOf(0xAA, 0x68, 0x04, 0xBF, 0x02, 0x0, 0xD1),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32),
                listOf(0x11, 0x8F),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x03),
                listOf(0xC8, 0x01, 0x0A, 0xC8, 0x04, 0xFF, 0x02, 0x0, 0x31),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68, 0x32),
                listOf(0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68, 0x32),
                listOf(0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x11, 0x8F),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04, 0x68),
                listOf(0x32, 0x10, 0x8E),
                listOf(0xC0, 0x04),
                listOf(0x68, 0x32, 0x10, 0x8E),
                listOf(0xC0, 0x03),
                listOf(0x80, 0x01, 0x42, 0x80, 0x04, 0xBF),
                listOf(0x02, 0x00, 0x39))
    }
}