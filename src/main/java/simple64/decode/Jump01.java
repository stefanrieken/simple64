package simple64.decode;

import simple64.Processor;

public class Jump01 extends JumpTable {

	public Jump01(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) { // ORA
		p.mnemonic = "ORA";
		p.a = p.alu.or (p.a, p.resolveOperand(bbb,cc));
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // AND
		p.mnemonic = "AND";
		p.a = p.alu.and(p.a, p.resolveOperand(bbb,cc));
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // EOR
		p.mnemonic = "EOR";
		p.a = p.alu.xor(p.a, p.resolveOperand(bbb,cc));
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // ADC
		p.mnemonic = "ADC";
		p.a = p.alu.adc(p.a, p.resolveOperand(bbb,cc));
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // STA
		p.mnemonic = "STA";
		p.mem.set(p.resolveAddress(bbb), p.a);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // LDA
		p.mnemonic = "LDA";
		p.a = p.alu.check(p.resolveOperand(bbb,cc));
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // CMP
		p.mnemonic = "CMP";
		p.alu.cmp(p.a, p.resolveOperand(bbb,cc));
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // SBC
		p.mnemonic = "SBC";
		p.a = p.alu.sbc(p.a, p.resolveOperand(bbb,cc));
	}

}
