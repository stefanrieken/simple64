package simple64.decode;

import simple64.Processor;

// an abstract way to structure the forest of if / elses 
// otherwise used in the decoding.

// jump to (max) one of 8 options
// may be used to decode 'aaa' as well as 'bbb' entries

public abstract class JumpTable {
	
	protected Processor p;

	public JumpTable(Processor p) {
		this.p = p;
	}

	public void jump(byte basedOn, byte aaa, byte bbb, byte cc) {
		if (basedOn == 0b000) {
			if000(aaa, bbb, cc);
		} else if (basedOn == 0b001) {
			if001(aaa, bbb, cc);
		} else if (basedOn == 0b010) {
			if010(aaa, bbb, cc);
		} else if (basedOn == 0b011) {
			if011(aaa, bbb, cc);
		} else if (basedOn == 0b100) {
			if100(aaa, bbb, cc);
		} else if (basedOn == 0b101) {
			if101(aaa, bbb, cc);
		} else if (basedOn == 0b110) {
			if110(aaa, bbb, cc);
		} else if (basedOn == 0b111) {
			if111(aaa, bbb, cc);
		}
	}

	public abstract void if000(byte aaa, byte bbb, byte cc);
	public abstract void if001(byte aaa, byte bbb, byte cc);
	public abstract void if010(byte aaa, byte bbb, byte cc);
	public abstract void if011(byte aaa, byte bbb, byte cc);
	public abstract void if100(byte aaa, byte bbb, byte cc);
	public abstract void if101(byte aaa, byte bbb, byte cc);
	public abstract void if110(byte aaa, byte bbb, byte cc);
	public abstract void if111(byte aaa, byte bbb, byte cc);
}
