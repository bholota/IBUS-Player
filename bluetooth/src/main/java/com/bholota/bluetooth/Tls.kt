package com.bholota.bluetooth

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.*

class Tls(context: Context): LifecycleObserver {

    private val engine: TextToSpeech = TextToSpeech(context) { status -> init(status) }
    private var isInitialized = false

    private val beforeInitQueue = mutableListOf<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun init(status: Int) {
        when (status) {
            TextToSpeech.SUCCESS -> {
                engine.language = Locale.US
                isInitialized = true

                beforeInitQueue.forEach { speak(it) }
                beforeInitQueue.clear()
            }
            else -> {
                isInitialized = false
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        engine.stop()
        engine.shutdown()
    }

    fun speak(text: String) {
        if(isInitialized) {
            engine.speak(text, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
        } else {
            beforeInitQueue.add(text)
        }
    }

    companion object {
        private const val UTTERANCE_ID = "com.bholota.bluetooth.UTTERANCE_ID"
    }
}