package com.bholota.ibusplayer.uart

import com.bholota.ibusplayer.utils.L
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import com.google.android.things.pio.UartDeviceCallback
import java.io.IOException

class BaseUartSession(val dataListener: (ByteArray) -> Unit) : UartSession {

    private val log = L("BaseUartSession")
    private var uartDevice: UartDevice? = null

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
            val buffer = ByteArray(UartConfig.CHUNK_SIZE)
            val result = ArrayList<Byte>()
            var count: Int
            while (true) {
                count = read(buffer, buffer.size)
                if (count == 0) break
                result.addAll(buffer.copyOfRange(0, count).toList())
            }
            return result.toByteArray()
        }
        return ByteArray(0)
    }
}