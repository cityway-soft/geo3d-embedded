package org.avm.hmi.swt.alarm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.ProducerManager;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.StateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class AlarmIhmImpl extends Composite implements AlarmIhm {

	private static final int INDENT = Desktop.DEFAULT_FONTSIZE / 5;

	private static final int NORMAL = 0;

	private static final int WARNING = 1;

	private static final int ALARM = 2;

	private ProducerManager _producer;

	private Alarm _alarm;

	private Desktop _desktop;

	private Image _imageAlarm;

	private Image _imageWarning;

	private HashMap _buttons;

	private String _tabName;

	private boolean _tabNotRead;

	private void send(Alarm alarm) {
		if (_producer != null) {
			_producer.publish(alarm);
		}
	}

	private boolean isAlarm() {
		Iterator iter = _buttons.values().iterator();
		while (iter.hasNext()) {
			StateButton b = (StateButton) iter.next();
			if (b.isDisposed() == false && b.getSelection() == true) {
				return true;
			}

		}
		return false;
	}

	private boolean isCurrentTabVisible() {
		if (_desktop.getMainPanel().isDisposed() == false) {
			TabItem item[] = ((TabFolder) (_desktop.getMainPanel())).getItems();
			int index = ((TabFolder) (_desktop.getMainPanel()))
					.getSelectionIndex();
			return (item == null || index == -1) ? false : item[index].getText()
					.equals(_tabName);
		} else {
			return false;
		}
	}

	public void update(Alarm alarm) {
		StateButton button = (StateButton) _buttons.get(alarm.getIndex());

		if (button == null) {
			button = addButton(alarm);
		}

		button.setState(alarm.isStatus());
		if (isAlarm()) {
			if (_tabNotRead || !isCurrentTabVisible()) {
				setState(ALARM);
			} else {
				setState(WARNING);
			}
		} else {
			setState(NORMAL);
		}
	}

	public void refresh() {

	}

	private StateButton addButton(final Alarm alarm) {
		GridData gridData;
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = INDENT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 70;

		StateButton button = new StateButton(this, SWT.BORDER);
		_buttons.put(alarm.getIndex(), button);
		button.setNotActiveLabel(BUTTON_DESACTIVATED);
		button.setActiveLabel(BUTTON_ACTIVATED);
		button.setNotActiveColor(DesktopImpl.VERT);
		button.setActiveColor(DesktopImpl.ROUGE);
		button.setLayoutData(gridData);
		button.setText(alarm.getName());
		button.setEnabled(alarm.isReadOnly()==false);
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				StateButton b = (StateButton) e.getSource();
				alarm.setStatus(b.getSelection());
				send(alarm);
				update(alarm);
			}
		});
		this.layout();
		return button;
	}

	public void setState(int state) {
		if (_desktop != null) {
			switch (state) {
			case NORMAL:
				_desktop.setTabItemImage(_tabName, null);
				break;
			case WARNING:
				_desktop.setTabItemImage(_tabName, _imageWarning);
				break;
			case ALARM:
				_desktop.setTabItemImage(_tabName, _imageAlarm);
				break;
			default:
				break;
			}
		}
	}

	public AlarmIhmImpl(Composite parent, int style, String tabName) {
		super(parent, style);
		_tabName = tabName;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;

		this.setLayout(gridLayout);
		setBackground(DesktopStyle.getBackgroundColor());

		_imageAlarm = new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/resources/picto-alarm.png"));
		_imageWarning = new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/resources/picto-warning.png"));
		_buttons = new HashMap();

		this.addListener(SWT.Hide, new Listener() {

			public void handleEvent(Event event) {
				_tabNotRead = !isCurrentTabVisible();
				if (isAlarm() && !_tabNotRead) {
					setState(WARNING);
				}
			}
		});
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public Collection getButtons() {
		return _buttons.values();
	}

	public String getProducerPID() {
		return AlarmIhm.class.getName();
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
	}

	public String getTabName() {
		return _tabName;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
