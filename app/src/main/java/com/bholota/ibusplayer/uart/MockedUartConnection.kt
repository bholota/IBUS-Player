package com.bholota.ibusplayer.uart

import android.os.Handler
import com.bholota.ibus.IBusDebugData

class MockedUartConnection(override val dataListener: (ByteArray) -> Unit) : UartConnection {

    override val devicesList: List<String> = listOf("DEBUG_DEVICE")

    private var isDeviceOpen = false

    private var thread: Thread? = null

    private var position = 0

    private var handler = Handler()

    private val packets = IBusDebugData.packets

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
}