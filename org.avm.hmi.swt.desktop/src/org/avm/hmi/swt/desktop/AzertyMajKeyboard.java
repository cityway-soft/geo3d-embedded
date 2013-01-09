package org.avm.hmi.swt.desktop;


import org.eclipse.swt.widgets.Composite;

public class AzertyMajKeyboard extends Keyboard {
	protected static String[][] KEYS = {
			{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"  },
			{ "A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"  },
			{ "Q", "S", "D", "F", "G", "H", "J", "K", "L", "M" },
			{ "-", "W", "X", "C", "V", "B", "N", "?", ".", "/" },
			{ CANCEL, CLEAR, " ", null, null, null, null, BACKSPACE, OK, null } };

	public AzertyMajKeyboard(Composite parent, int ctrl) {
		super(parent, ctrl);
	}

	public AzertyMajKeyboard(String name, Composite parent, int ctrl) {
		super(name, parent, ctrl);
		setCursorEnable(true);
	}

	public String[][] getKeys() {
		return KEYS;
	}

}
