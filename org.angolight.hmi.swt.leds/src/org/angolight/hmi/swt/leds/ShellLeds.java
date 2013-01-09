package org.angolight.hmi.swt.leds;

import org.apache.log4j.Logger;
import org.avm.hmi.swt.desktop.Geometry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ShellLeds  {
	private Display _display;
	private Shell _shell;
	private Logger _log = Logger.getInstance(this.getClass().getName());

	public ShellLeds(Display display, int style) {
		_shell = new Shell(display, style);
		_display = display;
		initialize();
	}

	private void initialize() {
		String geometry = System
				.getProperty("org.angolight.hmi.swt.leds.geometry");
		if (geometry == null) {
			geometry = System.getProperty("org.avm.hmi.swt.geometry");
		}

		Rectangle window = Geometry.parse(_display.getClientArea(), geometry);
		_shell.setLocation(new Point(window.x, window.y /* + window.height */));
		_shell.setSize(new Point(window.width, window.height));
		_shell.setText("ANGO");

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		_shell.setLayout(layout);
		_shell.open();
	}



	public Composite getShell() {
		return _shell;
	}

}
