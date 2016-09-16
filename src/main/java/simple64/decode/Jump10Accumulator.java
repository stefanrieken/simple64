package simple64.decode;

import simple64.Processor;

// calls in the 'accumulator' semi-addressing mode, plus other modeless calls
public class Jump10Accumulator extends JumpTable {

	public Jump10Accumulator(Processor p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) { // ASL
		p.mnemonic = "ASL";
		p.a = p.alu.aslRol(p.a, false);
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // ROL
		p.mnemonic = "ROL";
		p.a = p.alu.aslRol(p.a, true);
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // LSR
		p.mnemonic = "LSR";
		p.a = p.alu.lsrRor(p.a, false);
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // ROR
		p.mnemonic = "ROR";
		p.a = p.alu.lsrRor(p.a, true);
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // TXA
		p.mnemonic = "TXA";
		p.a = p.alu.check(p.x);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // TAX
		p.mnemonic = "TAX";
		p.x = p.alu.check(p.a);
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // DEX
		p.mnemonic = "DEX";
		p.x = p.alu.dec(p.x);
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // NOP
		p.mnemonic = "NOP";
		// nope
	}

}
