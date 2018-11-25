package com.bholota.ibus.module

import com.bholota.ibus.UartConnection
import com.bholota.ibus.frame.IBusFrame

abstract class IBusModule {

    abstract fun onStart(connection: UartConnection)

    abstract fun onRequest(connection: UartConnection, frame: IBusFrame)

    abstract fun onResponse(connection: UartConnection, frame: IBusFrame)
}