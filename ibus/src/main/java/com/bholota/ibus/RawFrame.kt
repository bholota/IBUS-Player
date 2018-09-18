package com.bholota.ibus

data class RawFrame(val src: Byte, val len: Byte, val dst: Byte, val data: List<Byte>, val checkSum: Byte)