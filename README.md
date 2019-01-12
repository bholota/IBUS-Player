# IBusPlayer
## CD changer emulator for older BMW cars

I still own old 1996 5 series BMW e39, and probably it will stay with me as a 2nd car until it falls apart. I like how it drives, but my version lacks aux input or even a CD player (it wasn't that popular back then, and for sure it was expensive). I didn't like the idea of buying 1 DIN replacement with adapter - it wouldn't look well between wood parts of the dashboard. I could buy aftermarket OEM BMW player with cd and aux (it was present in newer cars produced after 2000) or try to fit cd changer (it should be supported by my unit and was an option even in 1996). However, it would be an easy way without the joy of hacking, so I decided to build my own, digital cd changer.

### Features

- [x] Emulates physical cd changer in car and allows to have AUX feature for old bmw car audio
- [ ] Allows to connect phone with bluetooth to old bmw car audio
- [ ] Allows to trigger google assistant on the phone with physical button in car

### Implementation notes

Most solutions for IBus communication and emulation are RBPI based. As I'm a professional android developer, it was much easier for me to use android of things (AoT) and my existing skills. The project required enough other knowledge apart from programming skills, so it was challenging enough.
In the current state, a project is split into two parts: app and ibus library. The app module contains AoT related code and uses the library module. The library can be reused on another platform - in future, I will try to use it directly on the phone without AoT SDK.

* `IBusParser` reads data array and decodes proper IBus packets
* `IBusPacketRouter` dispatches IBus packets to modules
* `CDPlayerModule` provides requests and responses necessary to emulate CD changer
* `DefaultModule` this module will grab packets that weren't dispatched to any other module - it can be used to provide additional logs

### Hardware
    1. NXP i.MX7D Starter Kit (I will try RBPI 3 later)
    2. Reslers.de USB IBUS adapter (http://www.reslers.de/IBUS/)
    3. 3.5mm jack to cd changer bmw connector (TODO: find schematics)
    4. Car 12V to USB connector
    5. Samsung S8 or Nexus 5 as a music source
    6. TODO: Custom circuit with battery that shuts down/starts AoT device with car

### IBus
##### Packet structure

[SRC_ID]|[LEN]|[DEST_ID]|[DATA]|[XOR_CHECKSUM]
--------|-----|---------|------|--------------
source device id|packet length (without SRC_ID and LEN fields)|destination device id|message|xor checksum to compare with computed one and reject if different
1 byte|1 byte|1 byte|n - bytes| 1 byte

##### IBus checksum calculation
```c
unsigned char checksum(unsigned char* msg, size_t msgSize) {
    unsigned char checkSum = 0x0;
    for (int i = 0; i < msgSize; i++) {
        checkSum ^= msg[i];
    }
    return checkSum;
}
```

### Useful sources and projects

* https://mono.software/2016/12/01/hacking-bmw-i-bus-with-raspberry-pi/ similar project for rbpi on node
* http://web.comhem.se/mulle2/IBUSInsideDRAFTREV5.pdf
* http://web.comhem.se/bengt-olof.swing/ibusdevicesandoperations.htm
* http://www.alextronic.de/bmw/projects_bmw_info_ibus.html
* https://medium.com/@zolotarev_k/elixir-nerves-for-controlling-your-car-part-1-3474afed4749
* http://web.comhem.se/bengt-olof.swing/IBus.htm
* https://electronics.stackexchange.com/questions/221311/why-there-is-a-resistor-and-a-capacitor-in-this-aux-cables-diagram
* http://pibus.info/download.html
* https://wiert.me/2018/10/05/raspberry-pi-as-cd-changer-in-pre-092002-e46-bmw-320i-touring/ decent research
* https://github.com/ezeakeal/pyBus helped me a lot


### Notes
To talk directly with ibus from laptop on MacOS:

```script -a -t 0 out.bin screen /dev/cu.SLAB_USBtoUART -f 9600,cs8,-parenb,-cstopb,-hupcl```