package com.bholota.ibusplayer.uart

import com.bholota.ibus.UartConnection
import com.bholota.ibusplayer.utils.L
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import com.google.android.things.pio.UartDeviceCallback
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class AndroidUartConnection(override val dataListener: (UartConnection, ByteArray) -> Unit) : UartConnection {

    private val log = L("AndroidUartConnection")
    private var lastReadTime = 0L
    private var uartDevice: UartDevice? = null
    private var writerQueue = ArrayList<ByteArray>()

    private lateinit var writerThread: Thread

    private val uartDataCallback = object : UartDeviceCallback {

        override fun onUartDeviceError(uart: UartDevice?, error: Int) {
            log.w("Error event $error")
        }

        override fun onUartDeviceDataAvailable(uart: UartDevice?): Boolean {
            try {
                dataListener(this@AndroidUartConnection, readData())
            } catch (e: IOException) {
                log.w("Unable to access UART device", e)
            }
            return true
        }
    }

    override val devicesList: List<String>
        get() {
            log.d("List of available devices ${PeripheralManager.getInstance().uartDeviceList}")
            return PeripheralManager.getInstance().uartDeviceList
        }

    override fun openDevice(deviceName: String) {
        // it could be synchronized with read()
        writerThread = createWriterThread()
        uartDevice = try {
            PeripheralManager.getInstance().openUartDevice(deviceName).apply {
                log.d("Connected to: $deviceName")
                setBaudrate(UartConfig.BAUD_RATE)
                setDataSize(UartConfig.DATA_BITS)
                setParity(UartConfig.PARITY)
                setStopBits(UartConfig.STOP_BITS)
                registerUartDeviceCallback(uartDataCallback)
            }
        } catch (e: IOException) {
            log.w("Unable to open UART device", e)
            log.w("Available devices: $devicesList")
            null
        }
    }

    private fun createWriterThread(): Thread = thread(start = true) {
        val waitTime = 50L //ms
        try {
            while (true) {
                val now = Date().time
                if (now - lastReadTime < waitTime || writerQueue.isEmpty()) {
                    Thread.sleep(waitTime)
                    continue
                }
                synchronized(writerQueue) {
                    val packet = writerQueue.first()
                    val count = uartDevice?.run {
                        write(packet, packet.size)
                    }
                    log.d("Wrote $count bytes")
                    writerQueue.remove(packet)
                }
                Thread.sleep(waitTime)
            }
        } catch (_: InterruptedException) {
            // do nothing
        }
    }

    override fun closeDevice() {
        writerThread.interrupt()
        uartDevice?.unregisterUartDeviceCallback(uartDataCallback)
        uartDevice?.close()
        uartDevice = null
    }

    override fun isOpen() = uartDevice != null

    override fun writeData(data: ByteArray) {
        synchronized(writerQueue) {
            writerQueue.add(data)
        }
    }

    @Synchronized
    override fun readData(): ByteArray {
        uartDevice?.apply {
            val buffer = ByteArray(UartConfig.CHUNK_SIZE)
            val result = ArrayList<Byte>()
            var count: Int
            while (true) {
                count = read(buffer, buffer.size)
                if (count == 0) break
                result.addAll(buffer.copyOfRange(0, count).toList())
            }
            lastReadTime = Date().time
            return result.toByteArray()
        }
        lastReadTime = Date().time
        return ByteArray(0)
    }
}