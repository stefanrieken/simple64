package simple64.decode;

import simple64.Processor;

public class Jump00 extends JumpTable {

	public Jump00(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) {
		// not used
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // BIT or JSR abs
		if (bbb != 0b000)
			p.alu.bit(p.a, p.resolveOperand(bbb,cc));
//		else
//			TODO JSR abs
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // JMP (ind) (unique form of indirection) or RTI
		if (bbb != 0b000) {
			int address = p.resolveAddress(bbb);
			p.pc = p.mem.get(p.word(p.mem.get(address), p.mem.get(address + 1)));
		}
//		else
//			TODO RTI
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // JMP (abs) or RTS
		if (bbb != 0b000)
			p.pc = p.resolveAddress(bbb);
//		else
//			TODO RTS
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // STY or BRK
		if (bbb != 0b000) // which is BRK
			p.mem.set(p.resolveAddress(bbb), p.y);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // LDY
		p.y = p.alu.check(p.resolveOperand(bbb, cc));
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // CPY
		p.alu.check((short) (p.y - p.resolveOperand(bbb, cc)));
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // CPX
		p.alu.check((short) (p.x - p.resolveOperand(bbb, cc)));
	}


}
