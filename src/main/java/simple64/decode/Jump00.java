package simple64.decode;

import simple64.Processor;

public class Jump00 extends JumpTable {

	public Jump00(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) {
		// if bbb == 000
		p.mnemonic = "BRK";
		// TODO jump to interrupt routine
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // BIT or JSR abs
		if (bbb == 0b000) { // JSR abs
			p.mnemonic = "JSR";
			int returnAddress = p.pc + 2;
			p.alu.push(p.hi(returnAddress));
			p.alu.push(p.lo(returnAddress));
			p.pc = p.resolveAddress((byte) 0b011); // explicitly absolute, because instruction is irregular
		} else { // BIT
			p.mnemonic = "BIT";
			p.alu.bit(p.a, p.resolveOperand(bbb,cc));
		}
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // JMP (abs) or RTI
		if (bbb == 0b000) { // RTI
			p.mnemonic = "RTI";
			p.sr = p.alu.pull();
			p.pc = p.alu.pullWord();
		} else { // JMP (abs)
			p.mnemonic = "JMP";
			p.pc = p.resolveAddress((byte) 0b011); // explicitly absolute, because instruction is irregular
		}
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // JMP (ind) (unique form of indirection) or RTS
		if (bbb == 0b000) { // RTS
			p.mnemonic = "RTS";
			p.pc = p.alu.pullWord();
		} else { // JMP (ind)
			p.mnemonic = "JMP";
			int address = p.resolveAddress(bbb);
			p.pc = p.word(p.mem.get(address), p.mem.get(address + 1));
		}
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // STY
		p.mnemonic = "STY";
		p.mem.set(p.resolveAddress(bbb), p.y);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // LDY
		p.mnemonic = "LDY";
		p.y = p.alu.check(p.resolveOperand(bbb, cc));
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // CPY
		p.mnemonic = "CPY";
		p.alu.check((short) (p.y - p.resolveOperand(bbb, cc)));
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // CPX
		p.mnemonic = "CPX";
		p.alu.check((short) (p.x - p.resolveOperand(bbb, cc)));
	}


}
