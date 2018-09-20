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

    private val midToCdPooling = IBusFrame(IBusDevice.MID, IBusDevice.CDPlayer, listOf(0x1))

    //private val cdToMidPoolingResponse = IBusFrame(IBusDevice.CDPlayer, )

    override fun onRequest(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule --> onRequest: $frame")

        when(frame) {
            //midToCdPooling -> connection.writeData(cdToMidPoolingResponse.toByteArray())
        }
    }

    override fun onResponse(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule <-- onResponse: $frame")
    }
}