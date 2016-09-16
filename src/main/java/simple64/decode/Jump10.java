package simple64.decode;

import simple64.Processor;

public class Jump10 extends JumpTable {

	public Jump10(Processor p) {
		super(p);
	}

	@Override
	public void if000(byte aaa, byte bbb, byte cc) { // ASL
		p.mnemonic = "ASL";
		// read and store in one operation
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.aslRol(p.mem.get(address), false));
	}

	@Override
	public void if001(byte aaa, byte bbb, byte cc) { // ROL
		p.mnemonic = "ROL";
		// read and store in one operation
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.aslRol(p.mem.get(address), true));
	}

	@Override
	public void if010(byte aaa, byte bbb, byte cc) { // LSR
		p.mnemonic = "LSR";
		// read and store in one operation
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.lsrRor(p.mem.get(address), false));
	}

	@Override
	public void if011(byte aaa, byte bbb, byte cc) {
		p.mnemonic = "ROR";
		// read and store in one operation
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.lsrRor(p.mem.get(address), true));
	}

	@Override
	public void if100(byte aaa, byte bbb, byte cc) { // STX
		p.mnemonic = "STX";
		p.mem.set(p.resolveAddress(bbb), p.x);
	}

	@Override
	public void if101(byte aaa, byte bbb, byte cc) { // LDX
		p.mnemonic = "LDX";
		p.x = p.alu.check(p.resolveOperand(bbb, cc));
	}

	@Override
	public void if110(byte aaa, byte bbb, byte cc) { // DEC
		p.mnemonic = "DEC";
		 // read and store in one operation
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.dec((short) (p.mem.get(address))));
	}

	@Override
	public void if111(byte aaa, byte bbb, byte cc) { // INC
		p.mnemonic = "INC";
		int address = p.resolveAddress(bbb);
		p.mem.set(address, p.alu.inc((short) (p.mem.get(address))));
	}

}
