package com.bholota.ibus

import com.bholota.ibus.frame.IBusFrame
import com.bholota.ibus.module.CDPlayerModule
import com.bholota.ibus.module.DefaultModule
import com.bholota.ibus.module.IBusModule

class IBusPacketRouter {

    val cdPlayerModule = CDPlayerModule()

    private val modules = hashMapOf<IBusDevice, IBusModule>(
            IBusDevice.CDPlayer to cdPlayerModule
    )

    private val defaultModule = DefaultModule()

    fun routePacket(connection: UartConnection, frame: IBusFrame) {
        (modules[frame.dst] ?: defaultModule).onRequest(connection, frame)
        (modules[frame.src] ?: defaultModule).onResponse(connection, frame)
    }

    fun initModules(connection: UartConnection) {
        modules.forEach { _, iBusModule ->
            iBusModule.onStart(connection)
        }
    }
}