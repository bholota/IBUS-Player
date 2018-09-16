package com.bholota.ibusplayer.uart

import com.bholota.ibusplayer.utils.L
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import com.google.android.things.pio.UartDeviceCallback
import java.io.IOException

class BaseUartSession(val dataListener: (ByteArray) -> Unit) : UartSession {

    private val log = L("BaseUartSession")
    private var uartDevice: UartDevice? = null
    private val maxReadSize = 64

    private val uartDataCallback = object: UartDeviceCallback {

        override fun onUartDeviceError(uart: UartDevice?, error: Int) {
            log.w("Error event $error")
        }

        override fun onUartDeviceDataAvailable(uart: UartDevice?): Boolean {
            try {
                dataListener(readData())
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
        uartDevice = try {
            PeripheralManager.getInstance().openUartDevice(deviceName).apply {
                registerUartDeviceCallback(uartDataCallback)
            }
        } catch (e: IOException) {
            log.w("Unable to open UART device", e)
            null
        }
    }

    override fun closeDevice() {
        uartDevice?.unregisterUartDeviceCallback(uartDataCallback)
        uartDevice?.close()
        uartDevice = null
    }

    override fun isOpen() = uartDevice != null

    override fun writeData(data: ByteArray) {
        val count = uartDevice?.run {
            write(data, data.size)
        }
        log.d("Wrote $count bytes")
    }

    override fun readData(): ByteArray {
        uartDevice?.apply {
            val buffer = ByteArray(maxReadSize)
            var count: Int = read(buffer, buffer.size)
            while (count > 0) {
                count = read(buffer, buffer.size)
            }
            return buffer
        }
        return ByteArray(0)
    }
}