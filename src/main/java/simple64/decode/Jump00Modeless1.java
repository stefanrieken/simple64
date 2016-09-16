package simple64.decode;

import simple64.Processor;

public class Jump00Modeless1 extends JumpTable {

	public Jump00Modeless1(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) { // PHP
		p.mnemonic = "PHP";
		p.alu.push(p.sr);
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // PLP
		p.mnemonic = "PLP";
		p.sr = p.alu.pull();
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // PHA
		p.mnemonic = "PHA";
		p.alu.push(p.a);
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // PLA
		p.mnemonic = "PLA";
		p.a = p.alu.pull();
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // DEY
		p.mnemonic = "DEY";
		p.y = p.alu.dec(p.y);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // TAY
		p.mnemonic = "TAY";
		p.y = p.alu.check(p.a);
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // INY
		p.mnemonic = "INY";
		p.y = p.alu.inc(p.y);
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // INX
		p.mnemonic = "INX";
		p.x = p.alu.inc(p.x);
	}

}
