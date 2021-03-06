package com.bholota.ibus.module

import com.bholota.ibus.UartConnection
import com.bholota.ibus.frame.IBusFrame

class DefaultModule : IBusModule() {

    override fun onStart(connection: UartConnection) {

    }

    override fun onRequest(connection: UartConnection, frame: IBusFrame) {
        //L.log("DefaultModule --> onRequest: ${frame.toByteArray().prettyHex()}")
    }

    override fun onResponse(connection: UartConnection, frame: IBusFrame) {
//        L.log("DefaultModule <-- onResponse: $frame")
    }
}