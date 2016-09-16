package simple64;

import java.io.IOException;
import java.io.InputStream;

import simple64.peripheral.Keyboard;
import simple64.peripheral.RandomGenerator;
import simple64.peripheral.SquareDisplay;

public class Simple64 {

	public static void main (String ... args) {
		MemoryWithPeripherals mem = new MemoryWithPeripherals();
		mem.peripherals.add(new SquareDisplay(mem));
		mem.peripherals.add(new RandomGenerator());
		mem.peripherals.add(new Keyboard());
		
		Simple64 s = new Simple64();
		s.load(mem);
		s.run(mem);
	}
	
	private void load(Memory mem) {
		InputStream stream = getClass().getResourceAsStream("/compiled/demoscene.o65");
		
		int start = 0x0600;

		try {
			
			int ch = stream.read();
			int i=0;
			while (ch != -1) {
				mem.set(start+i, (short) ch);
			
				ch = stream.read();
				i++;
			}

			stream.close();
			
			// The ROM reset vector
			mem.set(0xFFFC, (short) 0x00);
			mem.set(0xFFFD, (short) 0x06);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void run(Memory mem) {
		Processor p = new Processor(mem);
		
		// Reset the stack pointer to 0xFF
		// This kind of setup should be done by the reset routine,
		// but there we jump straight into the program
		p.sp = 0xFF;

		p.reset();
		
		long time = System.nanoTime();
		long timePassed;

		while(p.run()) {
			do { timePassed = System.nanoTime() - time; } while (timePassed < 50000);
			time = System.nanoTime();
		}
	}
}
