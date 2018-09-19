package com.bholota.ibusplayer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bholota.ibus.IBusParser
import com.bholota.ibusplayer.uart.BaseUartConnection
import com.bholota.ibusplayer.uart.UartConfig
import com.bholota.ibusplayer.utils.L
import kotlin.concurrent.thread


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

    private var ibusUart = BaseUartConnection { data ->

        val packetString = data.joinToString { String.format("%02X", (it.toInt() and 0xFF)) }

        logText.append(packetString)
        logText.append('\n')

        runOnUiThread {
            logsView.text = logText.toString()
        }
        // parse
        val packets = parser.push(data)
        packets.forEach {
            log.w("<-- Packet: $it")
            packetText.append(it).append('\n')
        }

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

        thread(start = true) {
            for (i in 0..10) {
//                ibusUart.writeData(byteArrayOf(0x68, 0x05, 0x18, 0x38, 0x00, 0x00, 0x4D)) // register cd changer
                ibusUart.writeData(byteArrayOf(0x18, 0x04, 0xFF.toByte(), 0x02, 0x00, 0xE1.toByte())) // register cd changer
                Thread.sleep(2000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ibusUart.closeDevice()
    }
}
