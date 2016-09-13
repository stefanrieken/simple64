package simple64;

// perform the operations that influcence status flags
// implemented register-transparent to avoid repeats

public class ArithmeticLogicUnit {
	// status register
	// 7 6 5 4 3 2 1 0
	// S V   B D I Z C
	// (Sign, oVerflow, Break (software interrupt), Decimal mode, Interrupt disable, Zero, Carry
	// Let's start off with Sign, oVerflow, Zero, Carry
	// See also http://www.6502.org/tutorials/vflag.html
	//
	// Sign: if result is negative (bit 7 is enabled)
	// oVerflow: if result affects bit 7 (= overflows from bit 6 = two's complement signed overflow)
	// Zero: if result is zero
	// Carry: if result overflows from bit 7 (= unsigned overflow)
	
	short sr;

	short SIGN = 0b10000000;
	short OVFL = 0b01000000;
	short ZERO = 0b00000010;
	short CARY = 0b00000001;

	public short or (short reg, short arg) {
		return check((short) (reg | arg));
	}

	public short and (short reg, short arg) {
		return check((short) (reg & arg));
	}

	public short xor (short reg, short arg) {
		return check((short) (reg ^ arg));
	}

	public short adc (short reg, short arg) {
		short result = (short) (reg + arg);
		result = check((short) (result + (sr & CARY) == 0 ? 0 : 1));
		set(OVFL, (reg & 0x80) == 0 && (result & 0x80) != 0); // TODO is overflow not set when sign bit changes any direction?
		return result;
	}

	public short sbc (short reg, short arg) {
		short result = (short) (reg - arg);
		result = check((short) (result - (sr & CARY) == 0 ? 0 : 1));
		set(OVFL, (reg & 0x80) == 0 && (result & 0x80) != 0); // TODO is overflow not set when sign bit changes any direction?
		return result;
	}

	public short inc(short reg) {
		// does not affect overflow
		short result = (short) (reg+1);
		// also doesn't affect carry; so chop before check
		return check((short)(result & 0xFF));
	}

	public short dec(short reg) {
		// does not affect overflow
		short result = (short) (reg-1);
		// also doesn't affect carry; so chop before check
		return check((short)(result & 0xFF));
	}

	public void cmp (short reg, short arg) {
		check((short) (reg - arg));
	}
	
	// a real 'arithmethic' shift should preserve the sign bit
	// but 6502 doesn't -- it just calls shift-left 'arithmetic' :)
	public short aslRol(short reg, boolean rotate) {
		boolean oldCarry = (sr & CARY) != 0;
		short result = (short) (reg << 1);
		boolean newCarry = result > 0xFF;
		set(CARY, newCarry);
		if (rotate && oldCarry) result &= 0x01; // otherwise it is zero anyway
		return (short) (result & 0xFF);
	}

	public short lsrRor(short reg, boolean rotate) {
		boolean oldCarry = (sr & CARY) != 0;
		short result = (short) (reg >> 1);
		boolean newCarry = (reg & 0x01) != 0;
		set (CARY, newCarry);
		if (rotate && oldCarry) result &= 0x80; // otherwise it is zero anyway
		return result;
	}

	public void bit (short reg, short arg) {
		set(ZERO, (reg & arg) == 0);
		set(OVFL, (reg & 0x06) != 0);
		set(SIGN, (reg & 0x07) != 0);
	}

	// test value for flags.
	// in case of overflow, chop off remainder
	public short check(short arg) {
		set(SIGN, (arg & 0x80) != 0);
		set(ZERO, arg == 0);
		// set(OVFL, arg > 255); // can only be checked for if old value is known
		set(CARY, arg > 255);

		return (short) (arg & 0xFF);
	}

	private void set(short bit, boolean condition) {
		if (condition) sr |= bit;
		else sr &= (0xFF - bit);
	}
}
