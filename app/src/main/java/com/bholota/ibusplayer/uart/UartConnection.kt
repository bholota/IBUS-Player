package com.bholota.ibusplayer.uart

interface UartConnection {

    val devicesList: List<String>

    val dataListener: (ByteArray) -> Unit

    fun openDevice(deviceName: String)

    fun closeDevice()

    fun isOpen(): Boolean

    fun writeData(data: ByteArray)

    fun readData(): ByteArray

}