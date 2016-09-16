package simple64;

import simple64.decode.Jump00;
import simple64.decode.Jump00Modeless1;
import simple64.decode.Jump01;
import simple64.decode.Jump10;
import simple64.decode.Jump10Accumulator;
import simple64.decode.JumpTable;

// Most of the work is in the decoding.
// Wherever 8 different instructions can be decoded, a JumpTable is used.
public class Processor {
	
	// debug info
	public short opcode = 0;
	public String mnemonic = "";

	// all are implemented as shorts to easier treat them as unsigned bytes
	public short a;
	public short x;
	public short y;
	
	// stack pointer
	public short sp;

	// status register
	public short sr;

	public short SIGN = 0b10000000;
	public short OVFL = 0b01000000;
	public short BREA = 0b00010000;
	public short DECI = 0b00001000;
	public short INTR = 0b00000100;
	public short ZERO = 0b00000010;
	public short CARY = 0b00000001;

	public int pc;
	
	public Memory mem;
	public ArithmeticLogicUnit alu;
	
	private JumpTable jump00 = new Jump00(this);
	private JumpTable jump01 = new Jump01(this);
	private JumpTable jump10 = new Jump10(this);

	private JumpTable jump00modeless1 = new Jump00Modeless1(this);

	private JumpTable jump10accumulator = new Jump10Accumulator(this);

	public Processor (Memory memory) {
		this.mem = memory;
		this.alu = new ArithmeticLogicUnit(this);
	}

	public void reset() {
		pc = word(mem.get(0xFFFC), mem.get(0xFFFD));
	}

	public boolean run() {
		System.out.printf("pc: %04x\n", pc);
		mnemonic = "";
		opcode = mem.get(pc++);
			System.out.printf("opcode: %02x\n", opcode);
		
		if (opcode == 0x60 && sp == 0xFF) {
			System.out.println("Exiting on final RTS");
			return false;
		}

		// using the description on http://www.llx.com/~nparker/a2/opcodes.html,
		// split opcode in aaabbbcc
		byte aaa = (byte) (opcode >> 5);
		byte bbb = (byte) ((opcode & 0b11100) >> 2);
		byte cc = (byte) (opcode & 0b11);

		if (cc == 0b00) run00(aaa, bbb, cc);
		else if (cc == 0b01) run01(aaa, bbb, cc);
		else if (cc == 0b10) run10(aaa, bbb, cc);
		// cc == 11 is nonexistent

		System.out.println("mnemonic: " + mnemonic);
		
		if (opcode == 0) System.out.println("Exiting on BRK");
		return opcode != 0; // option to, like 6502asm.com, stop on BRK
	}

	private void run00 (byte aaa, byte bbb, byte cc) {
		if (bbb == (byte) 0b010) { // group of modeless instructions
			jump00modeless1.jump(aaa, aaa, bbb, cc);
		} else if (bbb == 0b100) { // branch instructions
			branch(aaa);
		} else if (bbb == 0b110) {
			setBitsInSr(aaa);
		} else {
			jump00.jump(aaa, aaa, bbb, cc);
		}
	}
	
	private void setBitsInSr(short aaa) {
		if (aaa == 100) { // exception
			mnemonic = "TYA";
			a = alu.check(y);
			return;
		}

		// xx gives the status bit to clear or set:
		// 00=CARY,01=INTR,10=SIGN,11=DECI
		// y indicates set or clear
		byte xx = (byte) (aaa >> 1);
		byte y = (byte) (aaa & 0b1);
		short which = 0;

		if (xx == 0b00) { which = CARY; mnemonic = y == 1 ? "SEC" : "CLC"; };
		if (xx == 0b01) { which = INTR; mnemonic = y == 1 ? "SEI" : "CLI"; };
		if (xx == 0b10) { which = OVFL; mnemonic = y == 1 ? "SEV" : "CLV"; }; 
		if (xx == 0b11) { which = DECI; mnemonic = y == 1 ? "SED" : "CLD"; };

		if (y == 1) sr |= which;
		else sr &= (0xFF - which);
	}
	
	// ALU operations (mostly)
	//
	private void run01 (byte aaa, byte bbb, byte cc) {
		jump01.jump(aaa, aaa, bbb, cc);
	}

	// Memory (sometimes: accumulator) operations
	//
	private void run10 (byte aaa, byte bbb, byte cc) {
		if (bbb == 0b010) {
			jump10accumulator.jump(aaa, aaa, bbb, cc);
		} else if (bbb == 0b110) {
			modeless2(aaa);
		} else {
			jump10.jump(aaa, aaa, bbb, cc);
		}
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
		if (xx == 0b00) { test = (sr & SIGN) != 0; mnemonic = y == 1 ? "BMI" : "BPL"; };
		if (xx == 0b01) { test = (sr & OVFL) != 0; mnemonic = y == 1 ? "BVS" : "BVC"; };
		if (xx == 0b10) { test = (sr & CARY) != 0; mnemonic = y == 1 ? "BCS" : "BCC"; }; 
		if (xx == 0b11) { test = (sr & ZERO) != 0; mnemonic = y == 1 ? "BEQ" : "BNE"; };

		if ((test && y == 1) || (!test && y == 0))
			pc += relative;
	}
	
	// cc=10
	private void modeless2(byte aaa) {
		if (aaa == 0b100) {
			mnemonic = "TXS";
			sp = alu.check(x);
		} else if (aaa == 0b101) {
			mnemonic = "TSX";
			x = alu.check(sp);
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
			// The difference between this one and (zero page), y
			// is very subtle and lies only in the interpretation further up
			int zeroPageAddress = word(mem.get(pc++), (short) 0);
			return word(mem.get(zeroPageAddress), mem.get(zeroPageAddress+1)) + x;
		} else if (bbb == 0b001) { // zero page
			return word(mem.get(pc++), (short) 0);
		} else if (bbb == 0b010) {
			return mem.get(pc++);
		} else if (bbb == 0b011) { // absolute
			return word(mem.get(pc++), mem.get(pc++));
		} else if (bbb == 0b100) { // (zero page), y
			int zeroPageAddress = word(mem.get(pc++), (short) 0);
			// looking for the word stored at the two consecutive zero-page locations
			return word(mem.get(zeroPageAddress), mem.get(zeroPageAddress+1)) + y;
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

	public short hi (int word) {
		return (short) (word >> 8);
	}
	
	public short lo (int word) {
		return (short) (word & 0xFF);
	}
}
