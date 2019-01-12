package com.bholota.ibus.module

import com.bholota.ibus.IBusDevice
import com.bholota.ibus.L
import com.bholota.ibus.UartConnection
import com.bholota.ibus.frame.IBusFrame
import com.bholota.ibus.prettyHex
import kotlin.concurrent.thread

/**
 * 1. every 30s announce until first pool listOf(0x2, 0x1)
 * 2. if pool msg received send pool response, go to 2.
 */
class CDPlayerModule : IBusModule() {

    /*

        onInit announce every 10 seconds disable on d_cdPoolResponse (triggered when pooling requested first)


        d_cdStartPlaying -> pause periodic writes, writeCurrentTrack
        d_cdSendStatus -> same
        d_cdPollResponse -> stop announce, stop pool response for 30 seconds and WRITER.writeBusPacket('68', 'c0', ['21', '40', '00', '09', '05', '05', '4D', '50', '53'])
            1. stop announce
            2. call pollResponseEvery 30 seconds (maybe more)
            3. write WRITER.writeBusPacket('68', 'c0', ['21', '40', '00', '09', '05', '05', '4D', '50', '53'])

        def writeCurrentTrack():
          cdSongHundreds, cdSong = _getTrackNumber()
          WRITER.writeBusPacket('c8', '80', ['23', '42', '32', '1e']) #clear IKEConsole LCD of messages before filling it with MPD info
          WRITER.writeBusPacket('18', '68', ['39', '02', '09', '00', '3F', '00', cdSongHundreds, cdSong])

        def pollResponse():
            WRITER.writeBusPacket('18', 'FF', ['02','00'])

        def announce():
          WRITER.writeBusPacket('18', 'FF', ['02', '01'])

     */

    // handled packets:

    private val cdAnnounce = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x1)) // todo trigger every 30s until first pooling
    private val cdPoolingRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x1)) // d_cdPollResponse
    private val cdPoolingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Broadcast2, listOf(0x2, 0x0))
    private val cdPoolingResponse2 = IBusFrame(IBusDevice.Radio, IBusDevice.MID, listOf(0x21, 0x40, 0x0, 0x9, 0x5, 0x5, 0x4d, 0x50, 0x53))

    private val cdPoolingRequest2 = IBusFrame(IBusDevice.MFL, IBusDevice.Phone, listOf(0x1)) // d_cdPollResponse
    private val cdReset = IBusFrame(IBusDevice.MFL, IBusDevice.Phone, listOf(0x3B, 0x40)) // d_cdPollResponse

    private val cdStatusRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x38, 0x0, 0x0))//d_cdSendStatus // Request CD and track info
    private val cdPlayRequest = IBusFrame(IBusDevice.Radio, IBusDevice.CDPlayer, listOf(0x38, 0x3, 0x0)) //d_cdStartPlaying

    private val cdPlayingResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x0, 0x9, 0x0, 0x3f, 0x0, /*disk index 1-6*/0x1, /*track index*/0x1))

    private val cdTrackInfoResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x2, 0x9, 0x0, 0x3f, 0x0, /*disk index 1-6*/0x1, /*track index*/0x1))

    private val cdStoppedResponse = IBusFrame(IBusDevice.CDPlayer, IBusDevice.Radio, listOf(0x39, 0x0, 0x2, 0x0, 0x3f, 0x0, /*disk index 1-6*/0x1, /*track index*/0x1))

    private var isAnnounced = false
    private var isPlaying = false

    override fun onStart(connection: UartConnection) {
        startAnnounce(connection)
//        startPlaying(connection)
    }

    override fun onRequest(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule <-- onRequest: ${frame.toByteArray().prettyHex()}")

        when(frame) {
            cdPoolingRequest, cdPoolingRequest2 -> {
                isAnnounced = true
                connection.writeData(cdPoolingResponse.toByteArray())
                connection.writeData(cdTrackInfoResponse.toByteArray())
            }
            cdStatusRequest -> connection.writeData(cdTrackInfoResponse.toByteArray())
            cdPlayRequest -> connection.writeData(cdTrackInfoResponse.toByteArray())
            else -> L.log(frame.toByteArray().prettyHex())
        }
    }

    override fun onResponse(connection: UartConnection, frame: IBusFrame) {
        L.log("CDPlayerModule --> onResponse: ${frame.toByteArray().prettyHex()}")
    }

    private fun startAnnounce(connection: UartConnection) {
        thread(start = true) {
            while (!isAnnounced && connection.isOpen()) {
                connection.writeData(cdAnnounce.toByteArray())
                Thread.sleep(1000L) // try announce with bigger interval
            }
        }
    }

//    private fun startPlaying(connection: UartConnection) {
//        if (!isPlaying) {
//            isPlaying = true
//            thread(start=true) {
//                while (isPlaying && connection.isOpen()) {
//                    //connection.writeData(cdTrackInfoResponse.toByteArray())
//                    Thread.sleep(5000L)
//                }
//            }
//        }
//    }
}