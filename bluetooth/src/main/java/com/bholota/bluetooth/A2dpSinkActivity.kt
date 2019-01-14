package com.bholota.bluetooth

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

class A2dpSinkActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var tls: Tls
    private lateinit var model: A2dpSinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Bluetooth config activity"

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_present, Toast.LENGTH_SHORT).show()
            return
        }

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        this.tls = Tls(this)
        this.model = ViewModelProviders.of(this).get(A2dpSinkViewModel::class.java)
    }
}