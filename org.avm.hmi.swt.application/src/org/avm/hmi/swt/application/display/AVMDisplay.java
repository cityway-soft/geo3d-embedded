package org.avm.hmi.swt.application.display;

import org.eclipse.swt.widgets.Display;

public interface AVMDisplay {
	public Display getDisplay();

	public static final int DEFAULT_FONTSIZE = Integer.parseInt(System
			.getProperty("org.avm.hmi.swt.fontsize", "7"));

	public static final String DEFAULT_FONT = System.getProperty(
			"org.avm.hmi.swt.font", "Liberation Sans");

	public static final boolean DEBUG = !System.getProperty("org.avm.debug",
			"false").equals("false");

	public static final String TABFOLDER_FONT = System.getProperty(
			"org.avm.hmi.swt.tabfolder.font", "Liberation Sans");

	public static final int TABFOLDER_FONTSIZE_DELTA = Integer.parseInt(System
			.getProperty("org.avm.hmi.swt.tabfolder.fontsize.delta", "6"));

}
