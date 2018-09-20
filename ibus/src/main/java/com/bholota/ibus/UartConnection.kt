package com.bholota.ibus

interface UartConnection {

    val devicesList: List<String>

    val dataListener: (UartConnection, ByteArray) -> Unit

    fun openDevice(deviceName: String)

    fun closeDevice()

    fun isOpen(): Boolean

    fun writeData(data: ByteArray)

    fun readData(): ByteArray

}