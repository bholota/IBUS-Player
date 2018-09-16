package com.bholota.ibusplayer.uart

interface UartSession {

    val devicesList: List<String>

    fun openDevice(deviceName: String)

    fun closeDevice()

    fun isOpen(): Boolean

    fun writeData(data: ByteArray)

    fun readData(): ByteArray

}