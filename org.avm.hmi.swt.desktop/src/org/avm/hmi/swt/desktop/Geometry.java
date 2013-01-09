package org.avm.hmi.swt.desktop;

import java.util.NoSuchElementException;

import org.eclipse.swt.graphics.Rectangle;

public class Geometry {

	public static Rectangle parse(Rectangle screen, String geometry){
		Rectangle window = new Rectangle(0,0,0,0);
		try {
			int[] val;

			val = new int[4];
			StringBuffer buf = new StringBuffer();
			int ii = 0;
			boolean rightOrigin = true, topOrigin = true;
			for (int i = 0; i < geometry.length(); i++) {
				char c = geometry.charAt(i);
				if (Character.isDigit(c)) {
					buf.append(c);
				} else {
					switch (c) {
					case 'x':
						break;
					case '+':
						if (ii == 1) {
							rightOrigin = true;
						} else if (ii == 2) {
							topOrigin = true;
						}
						break;
					case '-':
						if (ii == 1) {
							rightOrigin = false;
						} else if (ii == 2) {
							topOrigin = false;
						}
						break;

					default:
						throw new NoSuchElementException("separator: " + c);
					}
					val[ii] = Integer.parseInt(buf.toString());
					buf = new StringBuffer();
					ii++;
				}

			}
			val[ii] = Integer.parseInt(buf.toString());
			// System.out.println("width:" + val[0]);
			// System.out.println("heigth:" + val[1]);
			// System.out.println("x:" + (rightOrigin ? "+" : "-") + val[2]);
			// System.out.println("y:" + (topOrigin ? "+" : "-") + val[3]);
			window.width = val[0];
			window.height = val[1];
			window.x = (rightOrigin) ? val[2] : (screen.width - val[2]);
			window.y = (topOrigin) ? val[3] : (screen.height - val[3]);

		} catch (Exception e) {
			e.printStackTrace();

			window.width = screen.width;
			window.height = screen.height + 25;// !!!
			window.x = (screen.width - window.width) / 2;
			window.y = (screen.height - window.height) / 2;
		}
		return window;
	}


}
