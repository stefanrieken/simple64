package simple64.peripheral;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//The keyboard generator a la from 6502asm.com

public class Keyboard implements Peripheral, KeyListener {

	short keyCode = 0;

	@Override
	public void keyPressed(KeyEvent evt) {
		keyCode = (short) evt.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		keyCode = 0;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public Short get(int address) {
		if (address == 0x00FF)
			return keyCode;
		return null;
	}

	@Override
	public boolean set(int address, short value) {
		return false;
	}

}
