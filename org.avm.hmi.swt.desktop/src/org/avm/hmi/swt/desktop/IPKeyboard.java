package org.avm.hmi.swt.desktop;


import org.eclipse.swt.widgets.Composite;

public class IPKeyboard extends Keyboard {

	protected static String[][] KEYS = { { "1", "2", "3", CANCEL }, { "4", "5", "6", CLEAR },
			{ "7", "8", "9", BACKSPACE }, {"0",".", EMPTY ,OK } };

	public IPKeyboard(Composite parent, int ctrl) {
		super(parent, ctrl);
	}

	public IPKeyboard(String name, Composite parent, int ctrl) {
		super(name, parent, ctrl);
	}

	public String[][] getKeys() {
		return KEYS;
	}

}
