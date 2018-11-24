package com.bholota.ibus.module

import com.bholota.ibus.IBusDevice
import com.bholota.ibus.L
import com.bholota.ibus.UartConnection
import com.bholota.ibus.frame.IBusFrame

/**
 * 1. every 30s announce until pool
 * 2. if pool msg received send pool response, go to 2.
 */
class CDPlayerModule : IBusModule() {

    private val midToCdPoolingRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x1))
    private val cdToMidPoolingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x0))

    //private val midToCdStatusRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x0, 0x0)) // 0x38, 0x0, 0x0
    //private val cdToMidPlayingResponse = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x1))    // 0x38, 0x3, 0x0
//    if playing {
//        message = []byte{0x39, 0x00, 0x09, 0x00, 0x3f, 0x00}
//    } else {
//        message = []byte{0x39, 0x00, 0x02, 0x00, 0x3f, 0x00}
//    }

    private val midToCdStatusRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x38, 0x0, 0x0))
    private val cdToMidPlayingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x0, 0x9, 0x0, 0x3f, 0x0))

    override fun onRequest(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule --> onRequest: $frame")

        when(frame) {
            midToCdPoolingRequest -> connection.writeData(cdToMidPoolingResponse.toByteArray())
            midToCdStatusRequest -> connection.writeData(cdToMidPlayingResponse.toByteArray())
        }
    }

    override fun onResponse(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule <-- onResponse: $frame")
    }
}