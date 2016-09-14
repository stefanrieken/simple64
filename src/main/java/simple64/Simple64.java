package simple64;

import java.io.IOException;
import java.io.InputStream;

public class Simple64 {

	public static void main (String ... args) {
		Memory mem = new Memory();
		
		Simple64 s = new Simple64();
		s.load(mem);
		s.run(mem);
	}
	
	private void load(Memory mem) {
		InputStream stream = getClass().getResourceAsStream("/rainbow2.o64");
		
		int start = 0x0600;

		try {
			
			int ch = stream.read();
			while (ch != -1) {
				mem.set(start+1, (short) ch);
			
				ch = stream.read();
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
		
		while(true) {
			p.run();
		}
	}
}
