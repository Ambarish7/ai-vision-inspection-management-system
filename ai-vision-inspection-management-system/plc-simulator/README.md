# PLC simulator integration point

This starter repository documents the PLC handshake but does not pretend to implement a specific vendor's PLC protocol or actual device addresses. Your real project used a PLC integration; add the exact protocol adapter and address mapping that matches your machine (for example MC Protocol, Modbus TCP, or another supported protocol).

Suggested development interface:

```text
Inspection request -> set BUSY -> run inference -> write coordinates + OK/NG -> set DONE -> wait for PLC acknowledgement
```

Before adding real PLC writes, confirm bit/register addresses, word size, signedness, coordinate scaling, timeout, acknowledgement and reset sequence with the PLC ladder logic. Never run the simulator against production hardware.
