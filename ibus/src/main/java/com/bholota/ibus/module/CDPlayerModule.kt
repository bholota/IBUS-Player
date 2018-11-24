package com.bholota.ibus.module

import com.bholota.ibus.IBusDevice
import com.bholota.ibus.L
import com.bholota.ibus.UartConnection
import com.bholota.ibus.frame.IBusFrame

/**
 * 1. every 30s announce until first pool listOf(0x2, 0x1)
 * 2. if pool msg received send pool response, go to 2.
 */
class CDPlayerModule : IBusModule() {

    private val cdAnnounce = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x1)) // todo trigger every 30s until first pooling
    private val cdPoolingRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x1))
    private val cdPoolingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x0))
    private val cdStatusRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x38, 0x0, 0x0))
    private val cdPlayingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x0, 0x9, 0x0, 0x3f, 0x0, /*disk index 1-6*/0x1))
    private val cdStoppedResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x0, 0x2, 0x0, 0x3f, 0x0, /*disk index 1-6*/0x1))

    override fun onRequest(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule --> onRequest: $frame")

        when(frame) {
            cdPoolingRequest -> connection.writeData(cdPoolingResponse.toByteArray())
            cdStatusRequest -> connection.writeData(cdPlayingResponse.toByteArray())
        }
    }

    override fun onResponse(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule <-- onResponse: $frame")
    }
}