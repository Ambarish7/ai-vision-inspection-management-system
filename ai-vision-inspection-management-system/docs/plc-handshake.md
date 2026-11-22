# PLC handshake design (illustrative)

| Signal | Direction | Meaning |
|---|---|---|
| AI_REQUEST | PLC -> adapter | New inspection requested |
| AI_BUSY | adapter -> PLC | Inspection in progress |
| AI_OK | adapter -> PLC | Inspection passed |
| AI_NG | adapter -> PLC | Inspection failed |
| AI_DONE | adapter -> PLC | Result is ready |
| X1/Y1/X2/Y2 | adapter -> PLC | Defect bounding-box coordinates (if supported) |

This is a logical signal contract, not a universal PLC address map. Confirm actual device addresses, data types, scaling, signedness, endianness, acknowledgement and reset sequence with the PLC ladder logic and controls engineer.
