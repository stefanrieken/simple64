package simple64.peripheral;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

// The display setup from 6502asm.com

public class SquareDisplay implements Peripheral {

	private Color[] colors = {
			new Color(0x000000),
			new Color(0xffffff),
			new Color(0x880000),
			new Color(0xaaffee),
			new Color(0xcc44cc),
			new Color(0x00cc55),
			new Color(0x0000aa),
			new Color(0xeeee77),
			new Color(0xdd8855),
			new Color(0x664400),
			new Color(0xff7777),
			new Color(0x333333),
			new Color(0x777777),
			new Color(0xaaff66),
			new Color(0x0088ff),
			new Color(0xbbbbbb)
	};

	private int width = 32;
	private int pixelSize = 5;

	private Canvas canvas;

	public SquareDisplay() {
		makeCanvasWindow();
	}

	private void makeCanvasWindow() {
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width * pixelSize, width * pixelSize));
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

	@Override
	public Short get(int address) {
		return null;
	}

	@Override
	public boolean set(int address, short value) {
		// this should be probably be done in an overwritten paint()
		// based on existing mem values (plus the last new one)
		// to avoid repainting artefacts.
		// for now, at least it shows something!
		if (address >= 0x0200 && address < 0x0600) {
			int base = address - 0x0200;
			int x = base % 32;
			int y = base / 32;

			Graphics g = canvas.getGraphics();
			
			g.setColor(colors[value & 0x0F]);
			g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
		}

//		try {
//			// watch paint dry
//			Thread.sleep(50);
//		} catch (Exception e) {
//			throw new RuntimeException (e);
//		}

		return false; // do not 'intercept' set call
	}

}
