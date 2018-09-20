package com.bholota.ibus

enum class IBusDevice(val code: Byte) {
    Broadcast(0x0),
    SHD(0x8),
    CDPlayer(0x18),
    HKM(0x24),
    FUM(0x28),
    CCM(0x30),
    NAV(0x3B),
    DIA(0x3F),
    FBZV(0x40),
    MenuScreen(0x43),
    EWS(0x44),
    CID(0x46),
    FMBT(0x47),
    MFL(0x50),
    MML(0x51),
    IHK(0x5B),
    PDC(0x60),
    CDCD(0x66),
    Radio(0x68),
    DSP(0x6A),
    RDC(0x70),
    SM(0x72),
    SDRS(0x73),
    CDCD2(0x76),
    NAVE(0x7F),
    IKE(0x80.toByte()),
    MMR(0x9B.toByte()),
    CVM(0x9C.toByte()),
    FMID(0xA0.toByte()),
    ACM(0xA4.toByte()),
    FHK(0xA7.toByte()),
    NAVC(0xA8.toByte()),
    EHC(0xAC.toByte()),
    SES(0xB0.toByte()),
    TV(0xBB.toByte()),
    LCM(0xBF.toByte()),
    MID(0xC0.toByte()),
    Phone(0xC8.toByte()),
    LKM(0xD0.toByte()),
    CUSTOM1(0xD7.toByte()),
    CUSTOM2(0xD8.toByte()),
    SMAD(0xDA.toByte()),
    IRIS(0xE0.toByte()),
    OBC(0xE7.toByte()),
    ISP(0xE8.toByte()),
    MemorySeats(0xED.toByte()),
    BoardMonitorButtons(0xF0.toByte()),
    CSU(0xF5.toByte()),
    Broadcast2(0xFF.toByte());

    companion object {
        fun fromByte(value: Byte): IBusDevice? {
            return enumValues<IBusDevice>().firstOrNull { value == it.code }
        }
    }
}
