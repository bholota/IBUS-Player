package com.bholota.ibusplayer.ibus

import java.util.*

data class RawFrame(val src: Byte, val len: Byte, val dst: Byte, val data: ArrayList<Byte>, val crc: Byte)