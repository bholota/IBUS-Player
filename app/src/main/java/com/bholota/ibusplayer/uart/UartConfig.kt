package com.bholota.ibusplayer.uart

import com.google.android.things.pio.UartDevice

object UartConfig {
    /** UART Configuration Parameters */
    const val DEVICE_NAME = "USB-1:1.0"
    const val BAUD_RATE = 9600
    const val DATA_BITS = 8
    const val STOP_BITS = 1

    const val CHUNK_SIZE = 16

    const val PARITY = UartDevice.PARITY_NONE
}