package simple64.peripheral;

/**
 * A peripheral device can do either of two things (at will):
 * - Listen to memory 'get' and 'set' commands and act accordingly
 * - Intercept those commands.
 * 
 * If get returns non-null, Memory.get is intercepted.
 * If set returns true, Memory.set is intercepted.
 * 
 * This construction is probably not suitable for implementing banking.
 */
public interface Peripheral {

	Short get(int address);
	boolean set(int address, short value);
}
