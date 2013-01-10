package org.avm.hmi.swt.desktop;


import org.eclipse.swt.widgets.Composite;

public class ABCKeyboard extends Keyboard {

	protected static String[][] KEYS = {{ "1", "2ABC", "3DEF"}, {"4GHI", "5JKL",
			"6MNO"}, {"7PQRS", "8TUV", "9WXYZ"}, { BACKSPACE, "0 *", OK }};



	public ABCKeyboard(Composite parent, int ctrl) {
		super(parent, ctrl);
	}

	public ABCKeyboard(String name, Composite parent, int ctrl) {
		super(name, parent, ctrl);
	}

	public String[][] getKeys() {
		return KEYS;
	}



}
