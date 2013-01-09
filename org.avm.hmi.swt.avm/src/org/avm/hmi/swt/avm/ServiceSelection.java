package org.avm.hmi.swt.avm;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.avm.hmi.swt.desktop.ChoiceListener;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ServiceSelection extends Composite implements KeyboardListener,
		SelectionListener {
	private ChoiceListener _listener;

	private String _matriculeRemplacement = null;
	private Display _display;
	private SimpleDateFormat DF = new SimpleDateFormat(Messages.getString("ServiceSelection.date_format")); //$NON-NLS-1$
	private ServiceSelection _instance=null;
	private Keyboard _keyboard;

	public ServiceSelection(Composite parent, int ctrl) {
		super(parent, ctrl);
		_instance=this;
		_display = parent.getDisplay();
		create();
	}

	public String getMatriculeRemplacement() {
		return _matriculeRemplacement;
	}
	
	public void setEnabled(boolean b){
		super.setEnabled(b);
		if (_keyboard != null){
			_keyboard.setEnabled(b);
		}
		
	}

	private void create() {
		GridLayout layout = new GridLayout();
		this.setLayout(layout);
		setBackground(DesktopStyle.getBackgroundColor());
		_keyboard = new Keyboard(Messages
				.getString("ServiceSelection.service"), this, SWT.NONE); //$NON-NLS-1$
		_keyboard.setBackground(
				DesktopStyle.getBackgroundColor());
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		_keyboard.setLayoutData(data);
		_keyboard.setListener(this);
		layout();

	}

	public void setSelectionListener(ChoiceListener listener) {
		_listener = listener;
	}

	public void validation(String str) {
		if (str == null || str.trim().length() == 0) {
			return;
		}
		try {
			MessageBox.setMessage(DF.format(new Date()),
					Messages.getString("ServiceSelection.recherche_des_courses_valides"), //$NON-NLS-1$
					MessageBox.MESSAGE_NORMAL, SWT.NONE);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Integer data = Integer.valueOf(str);
		_listener.validation(this, data);

		MessageBox.setMessage(null, null, MessageBox.MESSAGE_NORMAL, SWT.NONE);
	}

	public void reset(){
		_keyboard.setText("");
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public void widgetSelected(SelectionEvent arg0) {
		Object obj = arg0.getSource();
		if (obj instanceof Button) {
			Button b = (Button) obj;
			Integer data = (Integer) b.getData();
			_listener.validation(this, data);
		}
	}


	public void setVersion(final int datasourceVersion) {
			_display.syncExec(new Runnable() {
				public void run() {
					try {
						_instance.setToolTipText(Messages.getString("ServiceSelection.version-base-de-donnee")+ " " + datasourceVersion); //$NON-NLS-1$
					} catch (Throwable t) {
						t.printStackTrace();
					}

				}
			});
		
	}
}