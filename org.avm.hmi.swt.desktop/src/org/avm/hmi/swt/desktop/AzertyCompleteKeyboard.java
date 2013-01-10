package org.avm.hmi.swt.desktop;


import org.eclipse.swt.widgets.Composite;

public class AzertyCompleteKeyboard extends Keyboard {
	protected static String[][] KEYS = {
		{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" },
		{ "Aa", "Zz", "Ee", "Rr", "Tt", "Yy", "Uu", "Ii", "Oo", "Pp" },
		{ "Qq", "Ss", "Dd", "Ff", "Gg", "Hh", "Jj", "Kk", "Ll", "Mm" },
		{ "-", "Ww", "Xx", "Cc", "Vv", "Bb", "Nn", "?", ".", "/" },
		{ CANCEL, CLEAR, " ", null, null, null, null, BACKSPACE, OK, null } };


	public AzertyCompleteKeyboard(Composite parent, int ctrl) {
		super(parent, ctrl);
	}

	public AzertyCompleteKeyboard(String name, Composite parent, int ctrl) {
		super(name, parent, ctrl);
	}

	public String[][] getKeys() {
		return KEYS;
	}

}
