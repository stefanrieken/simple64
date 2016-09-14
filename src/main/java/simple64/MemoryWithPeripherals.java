package simple64;

import java.util.ArrayList;
import java.util.List;

import simple64.peripheral.Peripheral;

public class MemoryWithPeripherals extends Memory {

	public List<Peripheral> peripherals = new ArrayList<>();

	@Override
	public short get(int address) {
		for (Peripheral p : peripherals) {
			Short result = p.get(address);
			if (result != null) return result.shortValue();
		}

		return super.get(address);
	}
	
	@Override
	public void set(int address, short value) {
		for (Peripheral p : peripherals) {
			if (p.set(address, value)) return;
		}
		super.set(address, value);
	}
}
