package com.bholota.ibus

import com.bholota.ibus.frame.IBusFrame
import com.bholota.ibus.module.CDPlayerModule
import com.bholota.ibus.module.DefaultModule
import com.bholota.ibus.module.IBusModule

class IBusPacketRouter {

    private val modules = hashMapOf<IBusDevice, IBusModule>(
        IBusDevice.CDPlayer to CDPlayerModule()
    )

    private val defaultModule = DefaultModule()

    fun routePacket(connection: UartConnection, frame: IBusFrame) {
        (modules[frame.dst] ?: defaultModule).onRequest(connection, frame)
        (modules[frame.src] ?: defaultModule).onResponse(connection, frame)
    }
}