package simple64.peripheral;

import java.util.Random;

// The random generator a la from 6502asm.com

public class RandomGenerator implements Peripheral {

	Random random = new Random();

	@Override
	public Short get(int address) {
		if (address == 0x00FE) {
			return (short) random.nextInt(256);
		}
		return null;
	}

	@Override
	public boolean set(int address, short value) {
		return false;
	}

}
