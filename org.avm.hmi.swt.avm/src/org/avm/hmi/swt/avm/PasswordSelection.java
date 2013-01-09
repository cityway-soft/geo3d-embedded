package org.avm.hmi.swt.avm;


import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PasswordSelection extends Composite implements KeyboardListener {
	private ChoiceListener _listener;

	public PasswordSelection(Composite parent, int ctrl) {
		super(parent, ctrl);
		create(this);
	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}

	private void create(Composite composite) {
		setLayout(new GridLayout());
		Keyboard kb = new Keyboard("Code secret", this, SWT.NONE);
		kb.setBackground(
				DesktopStyle.getBackgroundColor());
		kb.setPassword(true);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		kb.setLayoutData(gridData);
		kb.setListener(this);
		kb.pack();
	}

	public void validation(String str) {
		_listener.validation(this, str);
	}

}