package org.avm.hmi.swt.desktop;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class StateButton extends Composite implements SelectionListener {



	private static final int LARGEUR = 40;
	private Button _button;
	private Label _label;
	private boolean _state=false;

	private Vector _list = new Vector();
	private Color _activeColor;
	
	private  static Color DISABLE_COLOR;
	private static Color DEFAULT_ACTIVE_COLOR;

	public StateButton(Composite parent, int style) {
		super(parent, style);
		DEFAULT_ACTIVE_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);
		DISABLE_COLOR = getDisplay().getSystemColor(SWT.COLOR_GRAY);
		_activeColor=DEFAULT_ACTIVE_COLOR;
		initialize();
	}
	
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		_button.setEnabled(b);
		if (b){
			setState(_state);
		}
		else{
			this.setBackground(DISABLE_COLOR);
			_label.setBackground(DISABLE_COLOR);
		}
	}

	public void setActiveColor(Color color) {
		if (color == null) {
			color = DEFAULT_ACTIVE_COLOR;
		} else {
			_activeColor = color;
		}
	}

	public void setText(String text) {
		_label.setText(text);
	}

	public void setState(boolean state) {
		_state = state;
		_button.setSelection(state);
		_button
				.setText(state ? Messages.getString("StateButton.oui") : Messages.getString("StateButton.non")); //$NON-NLS-1$ //$NON-NLS-2$
		if (state) {
			_button.setBackground(_activeColor);
			_label.setBackground(_activeColor);
			this.setBackground(_activeColor);
		} else {
			_button.setBackground(DesktopStyle.getBackgroundColor());
			_label.setBackground(DesktopStyle.getBackgroundColor());
			this.setBackground(	DesktopStyle.getBackgroundColor());
		}
	}

	private void initialize() {
		setBackground(DesktopStyle.getBackgroundColor());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		// gridData.heightHint=150;
		setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.numColumns = 2;
		setLayout(layout);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		_label = new Label(this, SWT.NONE | SWT.CENTER);
		_label.setBackground(DesktopStyle.getBackgroundColor());
		_label.setLayoutData(gridData);

		gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = LARGEUR;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		_button = new Button(this, SWT.TOGGLE);
		_button.addSelectionListener(this);
		_button.setLayoutData(gridData);
		setState(false);
		pack();
		layout();
	}

	private void fireSelectionChanged() {
		Event detail = new Event();
		detail.widget = this;

		setState(_button.getSelection());
		SelectionEvent event = new SelectionEvent(detail);

		Enumeration e = _list.elements();
		while (e.hasMoreElements()) {
			SelectionListener listener = (SelectionListener) e.nextElement();
			listener.widgetSelected(event);
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		_list.add(listener);

	}

	public void removeSelectionListener(SelectionListener listener) {
		_list.remove(listener);

	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		fireSelectionChanged();
	}

	public void widgetSelected(SelectionEvent arg0) {
		fireSelectionChanged();
	}

	public void setSelection(boolean b) {
		_button.setSelection(b);
		setState(b);
	}

	public boolean getSelection() {
		return _button.getSelection();
	}

	public String getText() {
		return _label.getText();
	}

}
