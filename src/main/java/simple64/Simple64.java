package simple64;

import java.io.IOException;
import java.io.InputStream;

import simple64.peripheral.SquareDisplay;

public class Simple64 {

	public static void main (String ... args) {
		MemoryWithPeripherals mem = new MemoryWithPeripherals();
		mem.peripherals.add(new SquareDisplay());
		
		Simple64 s = new Simple64();
		s.load(mem);
		s.run(mem);
	}
	
	private void load(Memory mem) {
		InputStream stream = getClass().getResourceAsStream("/rainbow.o65");
		
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
			
			mem.set(0xFFFC, (short) 0x00);
			mem.set(0xFFFD, (short) 0x06);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void run(Memory mem) {
		Processor p = new Processor(mem, new ArithmeticLogicUnit());
		
		p.reset();
		
		while(p.run()) { }
		
		System.out.println("Exited on BRK");
	}
}
