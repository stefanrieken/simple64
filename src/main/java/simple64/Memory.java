package simple64;

public class Memory {

	// resolve sign issues through get() and set()
	private byte[] mem = new byte[65536];
	
	public short get(int address) {
		return (short) (mem[address] & 0xFF);
	}
	
	public void set(int address, short value) {
		mem[address] = (byte) value;
	}
}
