package simple64;

import simple64.decode.Jump00;
import simple64.decode.Jump00Modeless1;
import simple64.decode.Jump00Modeless2;
import simple64.decode.Jump01;
import simple64.decode.JumpTable;

// Most of the work is in the decoding.
// Wherever 8 different instructions can be decoded, a JumpTable is used.
// In other cases, currently, an if-construction is still setup
public class Processor {
	
	// all are implemented as shorts to easier treat them as unsigned bytes
	public short a;
	public short x;
	public short y;
	
	// stack pointer
	public short sp;

	short SIGN = 0b10000000;
	short OVFL = 0b01000000;
	short ZERO = 0b00000010;
	short CARY = 0b00000001;

	public int pc;
	
	public Memory mem;
	public ArithmeticLogicUnit alu;
	
	private JumpTable jump00 = new Jump00(this);
	private JumpTable jump01 = new Jump01(this);

	private JumpTable jump00modeless1 = new Jump00Modeless1(this);
	private JumpTable jump00modeless2 = new Jump00Modeless2(this);

	public Processor (Memory memory, ArithmeticLogicUnit alu) {
		this.mem = memory;
		this.alu = alu;
	}

	public void reset() {
		pc = word(mem.get(0xFFFC), mem.get(0xFFFD));
	}

	public boolean run() {
		System.out.printf("pc: %04x\n", pc);
		 short opcode = mem.get(pc++);
			System.out.printf("opcode: %02x\n", opcode);
		
		// using the description on http://www.llx.com/~nparker/a2/opcodes.html,
		// split opcode in aaabbbcc
		byte aaa = (byte) (opcode >> 5);
		byte bbb = (byte) ((opcode & 0b11100) >> 2);
		byte cc = (byte) (opcode & 0b11);

		if (cc == 00) run00(aaa, bbb, cc);
		else if (cc == 01) run01(aaa, bbb, cc);
		else if (cc == 10) run10(aaa, bbb, cc);
		// cc == 11 is nonexistent
		
		return opcode != 0; // option to, like 6502asm.com, stop on BRK
	}

	private void run00 (byte aaa, byte bbb, byte cc) {
		if (bbb == 0b000) { // break, subroutine, interrupt instructions
			breakSubroutineInterrupt(aaa);
		} else if (bbb == (byte) 0b010) { // group of modeless instructions
			jump00modeless1.jump(aaa, aaa, bbb, cc);
		} else if (bbb == 0b100) { // branch instructions
			branch(aaa);
		} else if (bbb == 0b110) { // group of modeless instructions
			jump00modeless2.jump(aaa, aaa, bbb, cc);
		} else {
			jump00.jump(aaa, aaa, bbb, cc);
		}
	}
	
	// Accumulator based calculations (mostly)
	//
	private void run01 (byte aaa, byte bbb, byte cc) {
		jump01.jump(aaa, aaa, bbb, cc);
	}

	// Memory (sometimes: accumulator) operations
	//
	private void run10 (byte aaa, byte bbb, byte cc) {
		if (bbb == 0b010) {
			accumulator(aaa);
		} else if (bbb == 0b110) {
			modeless3(aaa);
		}
		
		else if (aaa == 0b000 || aaa == 0b001) { // ASL, ROL (affects N,Z,C)
			// read and store in one operation
			int address = resolveAddress(bbb);
			mem.set(address, alu.aslRol(mem.get(address), aaa == 0b001));
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
		if (xx == 0b00) test = (alu.sr & SIGN) != 0;
		if (xx == 0b01) test = (alu.sr & OVFL) != 0;
		if (xx == 0b10) test = (alu.sr & CARY) != 0;
		if (xx == 0b11) test = (alu.sr & ZERO) != 0;

		if ((test && y == 1) || (!test && y == 0))
			pc += relative;
	}
	
	// cc=10 TODO stack
	private void modeless3(byte aaa) {
		if (aaa == 0b100) { // TXS
			
		} else if (aaa == 0b101) { // TSX
			
		}
	}

	// calls in the 'accumulator' semi-addressing mode, plus other modeless calls
	private void accumulator(byte aaa) {
		if (aaa == 0b000 || aaa == 0b001) { // ASLacc / ROLacc
			a = alu.aslRol(a, aaa == 0b001);
		} else if (aaa == 0b010 || aaa == 0b011) { // LSRacc / RORacc
			a = alu.lsrRor(a, aaa == 0b011);
		} else if (aaa == 0b100) { // TXA
			a = alu.check(x);
		} else if (aaa == 0b101) { // TAX
			x = alu.check(a);
		} else if (aaa == 0b110) { // DEX
			x = alu.dec(x);
		} else if (aaa == 0b111) { // NOP
			// nope
		}
		
	}

	public short resolveOperand(byte bbb, byte cc) {
		if (cc == 0b01) {
			if (bbb == 0b010)
				return mem.get(pc++); // immediate
			else
				return mem.get(resolveAddress(bbb));
		} else { // cc == 00, cc == 10
			if (bbb == 0b000)
				return mem.get(pc++); // immmediate
			else
				return mem.get(resolveAddress(bbb));
		}
	}
	
	public int resolveAddress(byte bbb) {
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

	public int word(short lo, short hi) {
		return 256 * hi + lo;
	}
}
