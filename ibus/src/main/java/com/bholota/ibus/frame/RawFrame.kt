package com.bholota.ibus.frame

data class RawFrame(val src: Byte, val len: Byte, val dst: Byte, val data: List<Byte>, val checkSum: Byte)