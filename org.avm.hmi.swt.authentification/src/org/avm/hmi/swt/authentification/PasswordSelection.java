package org.avm.hmi.swt.authentification;


import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PasswordSelection extends Composite implements KeyboardListener {
	private ChoiceListener _listener;
	private Keyboard _keyboard;

	public PasswordSelection(Composite parent, int ctrl) {
		super(parent, ctrl);
		setBackground(DesktopStyle.getBackgroundColor());
		create(this);
	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}
	
	public void setEnabled(boolean arg0) {
		super.setEnabled(arg0);
		if (_keyboard != null){
			_keyboard.setEnabled(arg0);
		}
	}

	private void create(Composite composite) {

		setLayout(new GridLayout());
		_keyboard = new Keyboard(Messages.getString("PasswordSelection.code-secret"), this, SWT.NONE); //$NON-NLS-1$
		_keyboard.setBackground(DesktopStyle.getBackgroundColor());
		_keyboard.setTextColor(	Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		_keyboard.setPassword(true);
		_keyboard.setListener(this);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_keyboard.setLayoutData(gridData);
		_keyboard.setListener(this);
		//_keyboard.pack();
	}

	public void validation(String str) {
		_listener.validation(this, str);
	}
	
	public void reset(){
		_keyboard.setText("");
	}

}