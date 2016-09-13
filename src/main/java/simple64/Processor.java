package simple64;

public class Processor {
	
	// all are implemented as shorts to easier treat them as unsigned bytes
	short a;
	short x;
	short y;
	
	// stack pointer
	short sp;

	short SIGN = 0b10000000;
	short OVFL = 0b01000000;
	short ZERO = 0b00000010;
	short CARY = 0b00000001;

	int pc;
	
	private Memory mem;
	private ArithmeticLogicUnit alu;

	public Processor (Memory memory, ArithmeticLogicUnit alu) {
		this.mem = memory;
		this.alu = alu;
	}

	public void reset() {
		pc = word(mem.get(0xFFFC), mem.get(0xFFFD));
	}

	public void run() {
		 short opcode = mem.get(pc++);
		
		// using the description on http://www.llx.com/~nparker/a2/opcodes.html,
		// split opcode in aaabbbcc
		byte aaa = (byte) (opcode >> 4);
		byte bbb = (byte) ((opcode & 0b1100) >> 2);
		byte cc = (byte) (opcode & 0b11);

		if (cc == 00) run00(aaa, bbb, cc);
		else if (cc == 01) run01(aaa, bbb, cc);
		else if (cc == 10) run10(aaa, bbb, cc);
		// cc == 11 is nonexistent
	}

	private void run00 (byte aaa, byte bbb, byte cc) {
		if (bbb == 0b000) { // break, subroutine, interrupt instructions
			breakSubroutineInterrupt(aaa);
		} else if (bbb == 0b100) { // branch instructions
			branch(aaa);
		} else if (bbb == 0b010) {
			other(aaa);
		}
		else if (aaa == 0b001) { // BIT
			alu.bit(a, resolveOperand(bbb,cc));
		} else if (aaa == 0b010) { // JMP (ind) (unique form of indirection)
			int address = resolveAddress(bbb);
			pc = mem.get(word(mem.get(address), mem.get(address + 1)));
		} else if (aaa == 0b011) { // JMP (abs)
			pc = resolveAddress(bbb);
		} else if (aaa == 0b100) { // STY
			mem.set(resolveAddress(bbb), y);
		} else if (aaa == 0b101) { // LDY
			y = alu.check(mem.get(resolveAddress(bbb)));
		} else if (aaa == 0b110) { // CPY
			alu.check((short) (y - resolveOperand(bbb, cc)));
		} else if (aaa == 0b111) { // CPX
			alu.check((short) (x - resolveOperand(bbb, cc)));
		}
	}
	
	// Accumulator operations
	//
	private void run01 (byte aaa, byte bbb, byte cc) {
		if (irregular(aaa, bbb, cc))
			return;

		if (aaa == 0b000) { // ORA
			a = alu.or (a, resolveOperand(bbb,cc));
		} else if (aaa == 0b001) { // AND
			a = alu.and(a, resolveOperand(bbb,cc));
		} else if (aaa == 0b010) { // EOR
			a = alu.xor(a, resolveOperand(bbb,cc));
		} else if (aaa == 0b011) { // ADC
			a = alu.adc(a, resolveOperand(bbb,cc));
		} else if (aaa == 0b100) { // STA
			mem.set(resolveAddress(bbb), a);
		} else if (aaa == 0b101) { // LDA
			a = alu.check(mem.get(resolveAddress(bbb)));
		} else if (aaa == 0b110) { // CMP
			alu.cmp(a, resolveOperand(bbb,cc));
		} else if (aaa == 0b111) { // SBC (affects N,Z,C,V)
			a = alu.sbc(a, resolveOperand(bbb,cc));
		}
	}

	// Memory (sometimes: accumulator) operations
	//
	private void run10 (byte aaa, byte bbb, byte cc) {
		if (aaa == 0b000 || aaa == 0b001) { // ASL, ROL (affects N,Z,C)
			if (bbb == 0b010) // accumulator
				a = alu.aslRol(a, aaa == 0b001);
			else {
				// read and store in one operation
				int address = resolveAddress(bbb);
				mem.set(address, alu.aslRol(mem.get(address), aaa == 0b001));
			}
		} else if (aaa == 0b010 || aaa == 0b011) { // LSR, (affects Z,C) ROR (affects N,Z,C)
			if (bbb == 0b010) // accumulator
				a = alu.lsrRor(a, aaa == 0b001);
			else {
				// read and store in one operation
				int address = resolveAddress(bbb);
				mem.set(address, alu.lsrRor(mem.get(address), aaa == 0b001));
			}
		} else if (aaa == 0b100) { // STX
			mem.set(resolveAddress(bbb), x);
		} else if (aaa == 0b101) { // LDX
			x = alu.check(mem.get(resolveAddress(bbb)));
		} else if (aaa == 0b110) { // DEC
			 // read and store in one operation
			int address = resolveAddress(bbb);
			mem.set(address, alu.dec((short) (mem.get(address))));
		} else if (aaa == 0b111) { // INC
			int address = resolveAddress(bbb);
			mem.set(address, alu.inc((short) (mem.get(address))));
		}
	}

	private void breakSubroutineInterrupt(byte aaa) {
		if (aaa == 0b000) { // BRK
			
		} else if (aaa == 0b001) { // JSR abs
			
		} else if (aaa == 0b010) { // RTI
			
		} else if (aaa == 0b011) { // RTS
			
		}

		// values with aaa == 0b1xx seem unassigned
	}

	private void branch(byte aaa) {
		// xx gives the status bit to test:
		// 00=SIGN,01=OVFL,10=CARY,11=ZERO
		// y gives the value to compare to
		byte xx = (byte) (aaa >> 1);
		byte y = (byte) (aaa & 0b1);
		
		// all branch instructions have two's complement relative addressing.
		byte relative = (byte) mem.get(pc++);
		
		boolean test = false;
		if (xx == 00) test = (alu.sr & SIGN) != 0;
		if (xx == 01) test = (alu.sr & OVFL) != 0;
		if (xx == 10) test = (alu.sr & CARY) != 0;
		if (xx == 11) test = (alu.sr & ZERO) != 0;

		if ((test && y == 1) || (!test && y == 0))
			pc += relative;
	}

	// TODO oa registers
	private void other(byte aaa) {
		if (aaa == 0b000) { // PHP
			
		} else if (aaa == 0b001) { // PLP
			
		} else if (aaa == 0b010) { // PHA
			
		} else if (aaa == 0b011) { // PLA
			
		} else if (aaa == 0b100) { // DEY
			y--;
		} else if (aaa == 0b101) { // TAY
			a = y;
		} else if (aaa == 0b110) { // INX
			x++;
		} else if (aaa == 0b111) { // INY
			y++;
		}
	}

	// run irregularly encoded calls
	private boolean irregular(byte aaa, byte bbb, byte cc) {
		if (aaa == 0b100) {
			if (bbb == 0b010) { // TXA
				a = alu.check(x);
				return true;
			} else if (bbb == 0b110) { // TXS
				// TODO
				return true;
			}
		} else if (aaa == 0b101) {
			if (bbb == 0b010) { // TAX
				x = alu.check(a);
				return true;
			} else if (bbb == 0b110) { // TSX (affects N,Z)
				// TODO
				return true;
			}
		} else if (aaa == 0b110 && bbb == 0b010) { // DEX (affects N,Z)
			x = alu.dec(x);
			return true;
		} else if (aaa == 0b111 && bbb == 0b010) { // NOP
			return true;
		}
		return false;
	}

	private short resolveOperand(byte bbb, byte cc) {
		if (cc == 0b01) {
			if (bbb == 0b010)
				return mem.get(pc++); // immediate
			else
				return mem.get(resolveAddress(bbb));
		} else {
			if (bbb == 0b000)
				return mem.get(pc++); // immmediate
			else
				return mem.get(resolveAddress(bbb));
		}
	}
	
	private int resolveAddress(byte bbb) {
		if (bbb == 0b000) { // (zero page,X)
			// TODO does this wrap?
			return mem.get(word((short) 0, (short) (mem.get(pc++) + x)));
		} else if (bbb == 0b001) { // zero page
			return mem.get(word((short) 0, mem.get(pc++)));
		
		} else if (bbb == 0b010) {
			return mem.get(pc++);
		} else if (bbb == 0b011) { // absolute
			return word(mem.get(pc++), mem.get(pc++));
		} else if (bbb == 0b100) { // (zero page), y
			return mem.get(word((short) 0, (short) (mem.get(pc++)))) + y;
		} else if (bbb == 0b110) { // absolute, y
			return word(mem.get(pc++), mem.get(pc++)) + y;
		} else if (bbb == 0b111) { // absolute, x
			return word(mem.get(pc++), mem.get(pc++)) + x;
		}
		
		return 0; // shouldn't get here
	}

	private int word(short lo, short hi) {
		return 256 * hi + lo;
	}
}
