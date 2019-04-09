package com.bholota.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

open class A2dpSinkActivity : AppCompatActivity() {

    private lateinit var model: A2dpSinkViewModel
    private lateinit var tls: Tls
    private lateinit var a2dpSink: A2dpSink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_present, Toast.LENGTH_SHORT).show()
            return
        }

        this.tls = Tls(this, this)
        this.model = ViewModelProviders.of(this).get(A2dpSinkViewModel::class.java)
        this.tls.speak(getString(R.string.bluetooth_init_intro))
        this.a2dpSink = A2dpSink(this, tls)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        a2dpSink.onActivityResult(requestCode, resultCode)
    }

    companion object {
        private const val TAG = "A2dpSinkActivity"
    }
}