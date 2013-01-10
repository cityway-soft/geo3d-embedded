package org.avm.hmi.swt.management;

import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class DriverSelection extends Composite {
	private Combo _configs;
	private Combo _drivers;
	private Combo _ports;
	private Combo _speeds;
	private Combo _bitsperchar;
	private Combo _stopbits;
	private Combo _parity;
	private Button _validation;

	public DriverSelection(Composite arg0, int arg1) {
		super(arg0, arg1);
		initialize();
	}

	private void initialize() {
		Label label;
		GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		this.setLayout(layout);
		setBackground(DesktopStyle.getBackgroundColor());

		label = new Label(this, SWT.NONE);
		label.setText("Configuration");
		label.setBackground(DesktopStyle.getBackgroundColor());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = 40;
		gridData.horizontalSpan = 6;
		_configs = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		_configs.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Model");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 20;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Port");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 100;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Speed");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 60;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Bits/char");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 60;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Stopbits");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 60;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("Parity");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 60;
		label.setLayoutData(gridData);

		label = new Label(this, SWT.NONE);
		label.setText("");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = 60;
		label.setLayoutData(gridData);

		_drivers = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		_ports = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		_ports.add("comm:0");
		_ports.add("comm:1");
		_ports.add("comm:2");
		_ports.add("comm:3");
		_ports.add("comm:4");
		_ports.add("rs485:0");
		_ports.add("rs485:1");
		_ports.add("rs485:2");
		_ports.add("rs485:3");
		_ports.add("rs485:4");
		_ports.select(1);

		// gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = false;
		// gridData.grabExcessVerticalSpace = false;
		// gridData.heightHint = 40;
		// gridData.widthHint = 25;
		_speeds = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		// _speeds.setLayoutData(gridData);
		_speeds.add("1200");
		_speeds.add("4800");
		_speeds.add("9600");
		_speeds.select(0);

		// gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = false;
		// gridData.grabExcessVerticalSpace = false;
		// gridData.heightHint = 40;
		// gridData.widthHint = 25;
		_bitsperchar = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		// _bitsperchar.setLayoutData(gridData);
		_bitsperchar.add("8");
		_bitsperchar.add("7");
		_bitsperchar.select(0);

		// gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = false;
		// gridData.grabExcessVerticalSpace = false;
		// gridData.heightHint = 40;
		// gridData.widthHint = 3;
		_stopbits = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		// _stopbits.setLayoutData(gridData);
		_stopbits.add("1");
		_stopbits.add("2");
		_stopbits.select(0);

		// gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = false;
		// gridData.grabExcessVerticalSpace = false;
		// gridData.heightHint = 40;
		// gridData.widthHint = 35;
		_parity = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		// _parity.setLayoutData(gridData);
		_parity.add("none");
		_parity.add("even");
		_parity.add("odd");
		_parity.select(0);

		// gridData = new GridData();
		// gridData.horizontalAlignment = GridData.FILL;
		// gridData.grabExcessHorizontalSpace = false;
		// gridData.grabExcessVerticalSpace = false;
		// gridData.heightHint = 40;
		// gridData.widthHint = 40;
		_validation = new Button(this, SWT.NONE);
		_validation.setText("OK");
		// _validation.setLayoutData(gridData);
		layout();

	}

	public void addSelectionListener(SelectionListener listener) {
		_validation.addSelectionListener(listener);
		_configs.addSelectionListener(listener);
	}

	public void setConfigs(String[] configs) {
		for (int i = 0; i < configs.length; i++) {
			String value = configs[i];
			if (value != null) {
				_configs.add(value);
			}
		}
	}

	public String getConfig() {
		return _configs.getText();
	}

	public void setConfig(String name) {
		_configs.setText(name);
	}

	public void setDrivers(String[] drivers) {
		for (int i = 0; i < drivers.length; i++) {
			String value = drivers[i];
			_drivers.add(value);
		}
	}

	public String getDriver() {
		int idx = _drivers.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _drivers.getItem(idx);
		}
		return result;
	}

	public void setDriver(String driver) {
		_drivers.setText(driver);
	}

	public String getParity() {
		int idx = _parity.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _parity.getItem(idx);
		}
		return result;
	}

	public void setParity(String parity) {
		_parity.setText(parity);
	}

	public String getSpeed() {
		int idx = _speeds.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _speeds.getItem(idx);
		}
		return result;
	}

	public void setSpeed(String speed) {
		_speeds.setText(speed);
	}

	public String getBitsperchar() {
		int idx = _bitsperchar.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _bitsperchar.getItem(idx);
		}
		return result;
	}

	public void setBitsperchar(String bits) {
		_bitsperchar.setText(bits);
	}

	public String getStopbits() {
		int idx = _stopbits.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _stopbits.getItem(idx);
		}
		return result;
	}

	public void setStopbits(String bits) {
		_stopbits.setText(bits);
	}

	public String getPort() {
		int idx = _ports.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _ports.getItem(idx);
		}
		return result;
	}

	public void setPort(String port) {
		_ports.setText(port);
	}

}
