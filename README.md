Simple64
========

Simple64 is a 6502asm.com inspired emulation of a minimalistic 64kB
6502-based computer. The idea is to supply a similar level of 6502
compatibility (that is, at least initially no interrupts) and 'peripherals'
(the 32x32, 16 colour screen memory, keyboard and entropy inputs), so that it
can be tested against the examples already available from 6502asm.com .

The peripherals are configurable, so in theory a different setup can be created.


Current status
--------------

In this initial setup, most opcodes are implemented.
The test cases 'rainbow' and 'rainbow2' run and provide display results.
Decimal mode is not yet implemented.

