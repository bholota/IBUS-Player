package com.bholota.ibusplayer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bholota.ibus.IBusParser
import com.bholota.ibusplayer.uart.MockedUartConnection
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

    private val log = L("MainActivity")
    lateinit var logsView: TextView
    lateinit var packetView: TextView

    var logText = StringBuffer()
    var packetText = StringBuffer()
    val parser = IBusParser()

    private var ibusUart = MockedUartConnection { data ->

        val packetString = data.joinToString { String.format("%02X", (it.toInt() and 0xFF)) }
        log.w("Packet: $packetString")
        logText.append(packetString)
        logText.append('\n')

        runOnUiThread {
            logsView.text = logText.toString()
        }
        // parse
        val packets = parser.push(data)
        packets.forEach { packetText.append(it).append('\n') }

        runOnUiThread {
            packetView.text = packetText.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logsView = findViewById(R.id.logs)
        logsView.movementMethod = ScrollingMovementMethod()
        packetView = findViewById(R.id.packets)
        packetView.movementMethod = ScrollingMovementMethod()

        ibusUart.openDevice(UartConfig.DEVICE_NAME)
    }

    override fun onDestroy() {
        super.onDestroy()
        ibusUart.closeDevice()
    }
}
