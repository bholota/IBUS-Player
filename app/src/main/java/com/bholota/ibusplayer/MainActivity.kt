package com.bholota.ibusplayer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bholota.ibusplayer.uart.BaseUartSession
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

    lateinit var logView: TextView
    var logText = StringBuffer()
    private var ibusUart = BaseUartSession { data ->
        logText.append(data.joinToString { String.format("%02X", (it.toInt() and 0xFF)) })
        logText.append('\n')
        logView.text = logText.toString()
    }

    private val log = L("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logView = findViewById(R.id.log)
        logView.movementMethod = ScrollingMovementMethod()
        ibusUart.openDevice(UartConfig.DEVICE_NAME)
    }

    override fun onDestroy() {
        super.onDestroy()
        ibusUart.closeDevice()
    }
}
