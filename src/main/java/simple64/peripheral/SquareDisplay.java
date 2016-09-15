package simple64.peripheral;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

import simple64.Memory;

// The display setup from 6502asm.com

public class SquareDisplay extends Canvas implements Peripheral {

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

	private Memory mem;

	public SquareDisplay(Memory mem) {
		this.mem = mem;
		makeCanvasWindow();
	}

	@Override
	public void paint(Graphics g) {
		for (int i = 0x0200; i < 0x0600; i++) {
			paintPixel(g, i, mem.get(i));
		}
	};

	private void paintPixel(Graphics g, int address, int value) {
			int base = address - 0x0200;
			int x = base % 32;
			int y = base / 32;

			g.setColor(colors[value & 0x0F]);
			g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
	}

	private void makeCanvasWindow() {
		setPreferredSize(new Dimension(width * pixelSize, width * pixelSize));
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

	@Override
	public Short get(int address) {
		return null;
	}

	@Override
	public boolean set(int address, short value) {
		paintPixel(getGraphics(), address, value);
//		try {
//			// watch paint dry
//			Thread.sleep(50);
//		} catch (Exception e) {
//			throw new RuntimeException (e);
//		}

		return false; // do not 'intercept' set call
	}

}
