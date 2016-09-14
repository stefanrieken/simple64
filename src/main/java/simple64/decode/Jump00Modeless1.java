package simple64.decode;

import simple64.Processor;

public class Jump00Modeless1 extends JumpTable {

	public Jump00Modeless1(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) { // PHP
		// TODO Auto-generated method stub
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // PLP
		// TODO Auto-generated method stub
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // PHA
		// TODO Auto-generated method stub
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) { // PLA
		// TODO Auto-generated method stub
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // DEY
		p.y = p.alu.dec(p.y);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // TAY
		p.y = p.alu.check(p.a);
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // INX
		p.x = p.alu.inc(p.x);
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // INY
		p.y = p.alu.inc(p.y);
	}

}
