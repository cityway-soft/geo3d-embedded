package org.avm.hmi.swt.desktop;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DesktopStyle {

	private static boolean nightMode;
	
	private static final Color NIGHT_BG_COLOR = Display.getDefault().getSystemColor(Desktop.DEFAULT_NIGHT_BACKGROUND_COLOR);
	private static final Color DAY_BG_COLOR = Display.getDefault().getSystemColor(Desktop.DEFAULT_BACKGROUND_COLOR);

	public static final Color getBackgroundColor() {
		if (nightMode) {
			return NIGHT_BG_COLOR;
		} else {
			return DAY_BG_COLOR;
		}
	}

	public static boolean isNightMode() {
		return nightMode;
	}

	public static final void setNightMode(boolean b) {
		Color current = getBackgroundColor();
		if (b != nightMode) {
			nightMode = b;
			//System.out.println("===> NightMode CHANGED : " + nightMode);
			Color newColor = getBackgroundColor();
			Shell[] shells = Display.getDefault().getShells();
			for (int i = 0; i < shells.length; i++) {
				setColor(shells[i], current, newColor);
			}
		} 
//		else {
//			System.out
//					.println("===> NightMode NOT changed (" + nightMode + ")");
//		}

	}

	private static void setColor(Composite composite, Color oldcolor,
			Color newcolor) {
		if (composite.isDisposed() == false) {
			Control[] controls = composite.getChildren();
			Color color = composite.getBackground();
			if (color.equals(oldcolor)) {
				composite.setBackground(newcolor);
				composite.redraw();
			}
			for (int i = 0; i < controls.length; i++) {
				if (controls[i].isDisposed() == false) {
					if (controls[i] instanceof Composite) {
						setColor((Composite) controls[i], oldcolor, newcolor);
					} else {
						color = controls[i].getBackground();
						if (color.equals(oldcolor)) {
							controls[i].setBackground(newcolor);
							controls[i].redraw();
						}
					}
				}
			}
		}
	}

}
