package org.avm.hmi.swt.desktop;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class StateButton extends Composite implements SelectionListener,
		MouseListener {

	private static final int LARGEUR = 80;
	private Button _button;
	private Label _label;
	private boolean _state = false;

	private Vector _list = new Vector();
	private Color _activeColor;
	private Color _notactiveColor;
	private static Color DISABLE_COLOR;
	private static Color DEFAULT_NOTACTIVE_COLOR;
	private static Color DEFAULT_ACTIVE_COLOR;

	private String labelButtonOn = Messages.getString("StateButton.oui");
	private String labelButtonOff = Messages.getString("StateButton.non");

	public StateButton(Composite parent, int style) {
		super(parent, style);
		DEFAULT_ACTIVE_COLOR = DesktopIhm.ROUGE;
		DEFAULT_NOTACTIVE_COLOR = DesktopIhm.VERT;
		DISABLE_COLOR = getDisplay().getSystemColor(SWT.COLOR_GRAY);
		_activeColor = DEFAULT_ACTIVE_COLOR;
		_notactiveColor = DEFAULT_NOTACTIVE_COLOR;
		initialize();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		_button.setEnabled(b);
		if (b) {
			setState(_state);
		} else {
			this.setBackground(DISABLE_COLOR);
			_label.setBackground(DISABLE_COLOR);
		}
	}

	public void setActiveColor(Color color) {
		if (color == null) {
			_activeColor = DEFAULT_ACTIVE_COLOR;
		} else {
			_activeColor = color;
		}
		if (isEnabled()) {
			setState(_state);
		}
	}

	public void setNotActiveColor(Color color) {
		if (color == null) {
			_notactiveColor = DEFAULT_NOTACTIVE_COLOR;
		} else {
			_notactiveColor = color;
		}
		if (isEnabled()) {
			setState(_state);
		}
	}

	public void setActiveLabel(String text) {
		if (text == null) {
			this.labelButtonOn = Messages.getString("StateButton.oui");
		} else {
			this.labelButtonOn = text;
		}
	}

	public void setNotActiveLabel(String text) {
		if (text == null) {
			this.labelButtonOff = Messages.getString("StateButton.non");
		} else {
			this.labelButtonOff = text;
		}

	}

	public void setState(boolean state) {
		_state = state;
		_button.setSelection(state);
		_button.setText(state ? labelButtonOff : labelButtonOn); //$NON-NLS-1$ //$NON-NLS-2$
		if (state) {
			// _button.setBackground(_activeColor);
			_label.setBackground(_activeColor);
			this.setBackground(_activeColor);
		} else {
			// _button.setBackground(_notactiveColor);
			_label.setBackground(_notactiveColor);
			this.setBackground(_notactiveColor);
		}
	}

	private void initialize() {
		setBackground(DesktopStyle.getBackgroundColor());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
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
		_label.addMouseListener(this);
		Font font = DesktopImpl.getFont(10, SWT.NORMAL);
		_label.setFont(font);

		gridData = new GridData();
		gridData.widthHint = LARGEUR;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		 font = DesktopImpl.getFont(8, SWT.NORMAL);
		_button = new Button(this, SWT.TOGGLE);
		_button.setFont(font);
		_button.addSelectionListener(this);
		_button.setLayoutData(gridData);
		setState(false);
		
		addMouseListener(this);
		
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

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		_button.setSelection(!_button.getSelection());
		fireSelectionChanged();
	}

	public void mouseUp(MouseEvent e) {

	}


	public void setText(String text) {
		_label.setText(text);
	}

}
