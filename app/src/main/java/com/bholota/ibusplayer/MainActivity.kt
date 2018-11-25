package com.bholota.ibusplayer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bholota.ibus.IBusDevice
import com.bholota.ibus.IBusPacketRouter
import com.bholota.ibus.IBusParser
import com.bholota.ibus.frame.IBusFrame
import com.bholota.ibusplayer.uart.AndroidUartConnection
import com.bholota.ibusplayer.uart.UartConfig
import com.bholota.ibusplayer.utils.L


/**
 * Notes:
 * The UART name assigned to a USB UART is determined only by the physical port that it is plugged
 * into. This name is consistent upon reboots and plug/unplug order.
 *
 * More info here: https://developer.android.com/things/sdk/pio/uart
 */
class MainActivity : AppCompatActivity() {

    init {
        com.bholota.ibus.L.log = { log.d(it) }
    }

    private val log = L("MainActivity")
    lateinit var logsView: TextView
    lateinit var packetView: TextView

    private var logText = StringBuffer()
    private var packetText = StringBuffer()

    private val parser = IBusParser()
    private val router = IBusPacketRouter()
    private var ibusUart = AndroidUartConnection { connection, data ->
        parser.push(data).forEach { router.routePacket(connection, IBusFrame.fromRaw(it)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logsView = findViewById(R.id.logs)
        logsView.movementMethod = ScrollingMovementMethod()
        packetView = findViewById(R.id.packets)
        packetView.movementMethod = ScrollingMovementMethod()

        ibusUart.openDevice(UartConfig.DEVICE_NAME)
        ibusUart.writeData(IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x1)).toByteArray())

    }

    override fun onDestroy() {
        super.onDestroy()
        ibusUart.closeDevice()
    }
}
