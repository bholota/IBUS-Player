# IBusPlayer
## CD changer emulator for older BMW cars

todo: motivation

todo: how it works

### IBus
##### Packet structure

`[SRC_ID] [LEN] [DEST_ID] [DATA] [XOR_CHECKSUM]`

* SRC_ID   - source device id
* LEN      - packet length (without SRC_ID and LEN fields)
* DEST_ID  - destination device id
* DATA     - message
* CHECKSUM - xor checksum to compare with computed one and reject if different

##### IBus checksum calculation
    1. Set XOR_VAR to 0x0
    2. For every message byte:
        1. XOR_VAR = XOR_VAR xor message_byte
    3. return XOR_VAR