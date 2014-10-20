package org.avm.hmi.swt.phony;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ButtonContactView extends ContactView {

	private Vector _buttons;

	private Button _currentCallButton;

	private ContactModel _model;

	private Phony _phony;

	private Font _font;

	private static int COLOR_CALLBUTTON = SWT.COLOR_DARK_BLUE;

	private Logger _log = Logger.getInstance(this.getClass());

	private boolean calling = false;

	public ButtonContactView(Composite arg0, int arg1) {
		super(arg0, arg1);
		_buttons = new Vector();
	}

	public void setFont(Font arg0) {
		if (arg0 != null) {
			super.setFont(arg0);
			_font = arg0;
		} else {
			_log.error("Font is null!");
		}
	}

	public void hangup() {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				try {

					_currentCallButton = null;
					Enumeration e = _buttons.elements();
					calling = false;
					while (e.hasMoreElements()) {
						Button bt = (Button) e.nextElement();
						// bt.setEnabled(true);
						bt.setForeground(Display.getCurrent().getSystemColor(
								SWT.COLOR_WHITE));
						bt.setBackground(Display.getCurrent().getSystemColor(
								COLOR_CALLBUTTON));
					}
					layout();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void ringing(String phonenumber) {
		if (_model != null) {
			calling = true;
			Enumeration e = _model.elements();
			if (e != null) {
				String name = null;

				boolean found = false;
				while (e.hasMoreElements()) {
					name = (String) e.nextElement();
					String phone = _model.getPhoneNumber(name);
					if (phone.equals(phonenumber)) {
						found = true;
						break;
					}
					// addCallButton(name);
				}

				if (!found)
					return;

				_log.info("Found " + phonenumber + ": " + name);

				e = _buttons.elements();
				while (e.hasMoreElements()) {
					Button bt = (Button) e.nextElement();
					// bt.setEnabled(false);
					if (bt.getText().equals(name)) {
						bt.setBackground(Display.getCurrent().getSystemColor(
								SWT.COLOR_BLACK));
						bt.setForeground(Display.getCurrent().getSystemColor(
								SWT.COLOR_WHITE));
					} else {
						bt.setForeground(Display.getCurrent().getSystemColor(
								SWT.COLOR_WHITE));
						bt.setBackground(Display.getCurrent().getSystemColor(
								COLOR_CALLBUTTON));
					}
				}

			}
		}
	}

	public void update(ContactModel contacts) {
		_model = contacts;

		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				if (_model != null) {
					Enumeration e;
					e = _buttons.elements();
					while (e.hasMoreElements()) {
						Button button = (Button) e.nextElement();
						_buttons.remove(button);
						e = _buttons.elements();
						button.dispose();
					}
					e = _model.elements();
					if (e != null) {
						while (e.hasMoreElements()) {
							String name = (String) e.nextElement();
							addCallButton(name);
						}
					}
					layout();
				}
			}
		});
	}

	private void addCallButton(final String contactName) {
		Button callButton = new Button(this, SWT.PUSH);
		_buttons.add(callButton);
		callButton.setText(contactName);
		if (_font != null) {
			callButton.setFont(_font);
		}
		callButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		callButton.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		GridData data = new GridData(GridData.FILL_BOTH);
		callButton.setLayoutData(data);

		callButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				if (_phony != null) {
					try {
						if (!calling) {
							_phony.call(contactName);
							_currentCallButton = (Button) arg0.getSource();
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	public void online() {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				Enumeration e = _buttons.elements();
				while (e.hasMoreElements()) {
					Button bt = (Button) e.nextElement();
					bt.setBackground(Display.getCurrent().getSystemColor(
							SWT.COLOR_GRAY));
				}

				if (_currentCallButton != null) {
					_currentCallButton.setBackground(Display.getCurrent()
							.getSystemColor(SWT.COLOR_GREEN));
				}
			}
		});
	}

	public void setPhony(Phony phony) {
		_phony = phony;
	}

	public void dialing() {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				Enumeration e = _buttons.elements();
				while (e.hasMoreElements()) {
					Button bt = (Button) e.nextElement();
					bt.setBackground(Display.getCurrent().getSystemColor(
							SWT.COLOR_GRAY));
				}
			}
		});
	}

}
