package com.bholota.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.things.bluetooth.BluetoothProfileManager
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Just kotlin version of com.example.androidthings.bluetooth.audio from sample-bluetooth-audio
 */
class A2dpSink(private val activity: AppCompatActivity, private val tls: Tls) : LifecycleObserver {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var a2DPSinkProxy: BluetoothProfile

    init {
        activity.lifecycle.addObserver(this)
    }

    private val adapterStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val oldState = A2dpSink.getPreviousAdapterState(intent)
            val newState = A2dpSink.getCurrentAdapterState(intent)
            Log.d(TAG, "Bluetooth Adapter changing state from $oldState to $newState")
            if (newState == BluetoothAdapter.STATE_ON) {
                Log.i(TAG, "Bluetooth Adapter is ready")
                initA2DPSink()
            }
        }
    }

    private val sinkProfileStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == A2dpSink.ACTION_CONNECTION_STATE_CHANGED) {
                val oldState = A2dpSink.getPreviousProfileState(intent)
                val newState = A2dpSink.getCurrentProfileState(intent)
                val device = A2dpSink.getDevice(intent)
                Log.d(TAG, "Bluetooth A2DP sink changing connection state from " + oldState +
                        " to " + newState + " device " + device)
                val deviceName = Objects.toString(device!!.getName(), "a device")
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    tls.speak("Connected to $deviceName")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    tls.speak("Disconnected from $deviceName")
                }
            }
        }
    }

    private val sinkProfilePlaybackChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == A2dpSink.ACTION_PLAYING_STATE_CHANGED) {
                val oldState = A2dpSink.getPreviousProfileState(intent)
                val newState = A2dpSink.getCurrentProfileState(intent)
                val device = A2dpSink.getDevice(intent)
                Log.d(TAG, "Bluetooth A2DP sink changing playback state from " + oldState +
                        " to " + newState + " device " + device)

                if (newState == A2dpSink.STATE_PLAYING) {
                    Log.i(TAG, "Playing audio from device " + device.address)
                } else if (newState == A2dpSink.STATE_NOT_PLAYING) {
                    Log.i(TAG, "Stopped playing audio from " + device.address)
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        activity.registerReceiver(adapterStateChangeReceiver, IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED))
        activity.registerReceiver(sinkProfileStateChangeReceiver, IntentFilter(
                ACTION_CONNECTION_STATE_CHANGED))
        activity.registerReceiver(sinkProfilePlaybackChangeReceiver, IntentFilter(
                ACTION_PLAYING_STATE_CHANGED))

        if (bluetoothAdapter.isEnabled) {
            Log.d(TAG, "Bluetooth Adapter is already enabled.")
            initA2DPSink()
        } else {
            Log.d(TAG, "Bluetooth adapter not enabled. Enabling.")
            bluetoothAdapter.enable()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        Log.d(TAG, "onDestroy")

        activity.unregisterReceiver(adapterStateChangeReceiver)
        activity.unregisterReceiver(sinkProfileStateChangeReceiver)
        activity.unregisterReceiver(sinkProfilePlaybackChangeReceiver)

        bluetoothAdapter.closeProfileProxy(A2dpSink.A2DP_SINK_PROFILE, a2DPSinkProxy)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_ENABLE_DISCOVERABLE) {
            Log.d(TAG, "Enable discoverable returned with result $resultCode")

            // ResultCode, as described in BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE, is either
            // RESULT_CANCELED or the number of milliseconds that the device will stay in
            // discoverable mode. In a regular Android device, the user will see a popup requesting
            // authorization, and if they cancel, RESULT_CANCELED is returned. In Android Things,
            // on the other hand, the authorization for pairing is always given without user
            // interference, so RESULT_CANCELED should never be returned.
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Enable discoverable has been cancelled by the user. " + "This should never happen in an Android Things device.")
                return
            }
            Log.i(TAG, "Bluetooth adapter successfully set to discoverable mode. " +
                    "Any A2DP source can find it with the name " + ADAPTER_FRIENDLY_NAME +
                    " and pair for the next " + DISCOVERABLE_TIMEOUT_MS + " ms. " +
                    "Try looking for it on your phone, for example.")

            // There is nothing else required here, since Android framework automatically handles
            // A2DP Sink. Most relevant Bluetooth events, like connection/disconnection, will
            // generate corresponding broadcast intents or profile proxy events that you can
            // listen to and react appropriately.

            tls.speak("Bluetooth audio sink is discoverable for " + DISCOVERABLE_TIMEOUT_MS +
                    " milliseconds. Look for a device named " + ADAPTER_FRIENDLY_NAME)
        }
    }

    private fun setupBTProfiles() {
        val bluetoothProfileManager = BluetoothProfileManager.getInstance()
        val enabledProfiles = bluetoothProfileManager.enabledProfiles
        if (!enabledProfiles.contains(A2dpSink.A2DP_SINK_PROFILE)) {
            Log.d(TAG, "Enabling A2dp sink mode.")
            val toDisable = Arrays.asList(BluetoothProfile.A2DP)
            val toEnable = Arrays.asList(
                    A2dpSink.A2DP_SINK_PROFILE,
                    A2dpSink.AVRCP_CONTROLLER_PROFILE)
            bluetoothProfileManager.enableAndDisableProfiles(toEnable, toDisable)
        } else {
            Log.d(TAG, "A2dp sink profile is enabled.")
        }
    }

    private fun initA2DPSink() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth adapter not available or not enabled.")
            return
        }
        setupBTProfiles()
        Log.d(TAG, "Set up Bluetooth Adapter name and profile")
        bluetoothAdapter.name = ADAPTER_FRIENDLY_NAME
        bluetoothAdapter.getProfileProxy(activity, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                a2DPSinkProxy = proxy
                enableDiscoverable()
            }

            override fun onServiceDisconnected(profile: Int) {}
        }, A2dpSink.A2DP_SINK_PROFILE)

        // todo: config interaction here like ui, buttons
    }

    private fun enableDiscoverable() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_TIMEOUT_MS)
        ActivityCompat.startActivityForResult(activity, discoverableIntent, REQUEST_CODE_ENABLE_DISCOVERABLE, null)
    }

    fun disconnectConnectedDevices() {
        if (!bluetoothAdapter.isEnabled) {
            return
        }
        tls.speak("Disconnecting devices")
        for (device in a2DPSinkProxy.connectedDevices) {
            Log.i(TAG, "Disconnecting device $device")
            A2dpSink.disconnect(a2DPSinkProxy, device)
        }
    }

    companion object {
        private const val TAG = "A2dpSink"
        /**
         * Profile number for A2DP_SINK profile.
         */
        const val A2DP_SINK_PROFILE = 11

        /**
         * Profile number for AVRCP_CONTROLLER profile.
         */
        const val AVRCP_CONTROLLER_PROFILE = 12

        /**
         * Intent used to broadcast the change in connection state of the A2DP Sink
         * profile.
         *
         *
         * This intent will have 3 extras:
         *
         *  *  [BluetoothProfile.EXTRA_STATE] - The current state of the profile.
         *  *  [BluetoothProfile.EXTRA_PREVIOUS_STATE]- The previous state of the
         * profile.
         *  *  [BluetoothDevice.EXTRA_DEVICE] - The remote device.
         *
         *
         *
         * [BluetoothProfile.EXTRA_STATE] or [BluetoothProfile.EXTRA_PREVIOUS_STATE]
         * can be any of [BluetoothProfile.STATE_DISCONNECTED],
         * [BluetoothProfile.STATE_CONNECTING], [BluetoothProfile.STATE_CONNECTED],
         * [BluetoothProfile.STATE_DISCONNECTING].
         *
         *
         * Requires [android.Manifest.permission.BLUETOOTH] permission to
         * receive.
         */
        const val ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED"

        /**
         * Intent used to broadcast the change in the Playing state of the A2DP Sink
         * profile.
         *
         *
         * This intent will have 3 extras:
         *
         *  *  [BluetoothProfile.EXTRA_STATE] - The current state of the profile.
         *  *  [BluetoothProfile.EXTRA_PREVIOUS_STATE]- The previous state of the
         * profile.
         *  *  [BluetoothDevice.EXTRA_DEVICE] - The remote device.
         *
         *
         *
         * [BluetoothProfile.EXTRA_STATE] or [BluetoothProfile.EXTRA_PREVIOUS_STATE]
         * can be any of [.STATE_PLAYING], [.STATE_NOT_PLAYING],
         *
         *
         * Requires [android.Manifest.permission.BLUETOOTH] permission to
         * receive.
         */
        const val ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.PLAYING_STATE_CHANGED"

        /**
         * A2DP sink device is streaming music. This state can be one of
         * [BluetoothProfile.EXTRA_STATE] or [BluetoothProfile.EXTRA_PREVIOUS_STATE] of
         * [.ACTION_PLAYING_STATE_CHANGED] intent.
         */
        const val STATE_PLAYING = 10

        /**
         * A2DP sink device is NOT streaming music. This state can be one of
         * [BluetoothProfile.EXTRA_STATE] or [BluetoothProfile.EXTRA_PREVIOUS_STATE] of
         * [.ACTION_PLAYING_STATE_CHANGED] intent.
         */
        const val STATE_NOT_PLAYING = 11

        private const val ADAPTER_FRIENDLY_NAME = "BMW Multimedia"
        private const val DISCOVERABLE_TIMEOUT_MS = 300
        private const val REQUEST_CODE_ENABLE_DISCOVERABLE = 100
        private const val UTTERANCE_ID = "com.example.androidthings.bluetooth.audio.UTTERANCE_ID"

        fun getPreviousAdapterState(intent: Intent): Int {
            return intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1)
        }

        fun getCurrentAdapterState(intent: Intent): Int {
            return intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
        }

        fun getPreviousProfileState(intent: Intent): Int {
            return intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, -1)
        }

        fun getCurrentProfileState(intent: Intent): Int {
            return intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
        }

        fun getDevice(intent: Intent): BluetoothDevice {
            return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        /**
         * Provides a way to call the disconnect method in the BluetoothA2dpSink class that is
         * currently hidden from the public API. Avoid relying on this for production level code, since
         * hidden code in the API is subject to change.
         *
         * @param profile
         * @param device
         * @return
         */
        fun disconnect(profile: BluetoothProfile, device: BluetoothDevice): Boolean {
            try {
                val m = profile.javaClass.getMethod("disconnect", BluetoothDevice::class.java)
                m.invoke(profile, device)
                return true
            } catch (e: NoSuchMethodException) {
                Log.w(TAG, "No disconnect method in the " + profile.javaClass.name +
                        " class, ignoring request.")
                return false
            } catch (e: InvocationTargetException) {
                Log.w(TAG, "Could not execute method 'disconnect' in profile " +
                        profile.javaClass.name + ", ignoring request.", e)
                return false
            } catch (e: IllegalAccessException) {
                Log.w(TAG, "Could not execute method 'disconnect' in profile " + profile.javaClass.name + ", ignoring request.", e)
                return false
            }
        }
    }
}