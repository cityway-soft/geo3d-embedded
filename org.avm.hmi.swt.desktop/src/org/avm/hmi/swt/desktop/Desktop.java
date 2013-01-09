package org.avm.hmi.swt.desktop;

import org.avm.hmi.swt.application.display.AVMDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public interface Desktop  {
	public static final String APPLICATION_NAME="AVM - Automatic Vehicule Monitoring";
	
	public static final int DEFAULT_FONTSIZE = AVMDisplay.DEFAULT_FONTSIZE;

	public static final String DEFAULT_FONT = AVMDisplay.DEFAULT_FONT;

	public static final int DEFAULT_NIGHT_BACKGROUND_COLOR = SWT.COLOR_DARK_GRAY;
	
	public static int DEFAULT_BACKGROUND_COLOR=SWT.COLOR_WHITE;//COLOR_WIDGET_BACKGROUND;
	
	public Display getDisplay();
		
	Composite getRightPanel();

	Composite getMiddlePanel();

	void addTabItem(String string, Composite avmihm);

	void addTabItem(String string, Composite avmihm, int index);

	void removeTabItem(String string);

	void activateItem(String name);

	Object[] getItems();

	void setMessageBox(String title, String string, int type);

	void setInformation(String msg);
}
