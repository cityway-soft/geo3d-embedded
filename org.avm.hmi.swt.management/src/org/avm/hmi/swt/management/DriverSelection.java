package org.avm.hmi.swt.management;

import java.util.Iterator;
import java.util.List;

import org.avm.hmi.swt.desktop.DesktopImpl;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class DriverSelection extends Composite {
	private Combo _configs;
	private Combo _drivers;
	private Combo _ports;
	private Combo _speeds;
	private Combo _bitsperchar;
	private Combo _stopbits;
	private Combo _parity;
	private Combo _protocol;
	private Button _validationButton;
	private Button _testButton;
	private Label _testResult;

	private static final int HEIGHT = 40;

	public DriverSelection(Composite arg0, int arg1) {
		super(arg0, arg1);
		initialize();
	}

	private void initialize() {
		Label label;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 0;
		this.setLayout(layout);
		setBackground(DesktopStyle.getBackgroundColor());
		Font fontText = DesktopImpl.getFont(8, SWT.NORMAL);
		Font fontCombo = DesktopImpl.getFont(4, SWT.NORMAL);

		label = new Label(this, SWT.NONE);
		label.setText("Profil");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
	    gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = HEIGHT;
		//gridData.widthHint = 100;
		_configs = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		_configs.setLayoutData(gridData);
		_configs.setData("configs");
		_configs.setFont(fontCombo);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		gridData.horizontalSpan = 2;
		Group group = new Group(this, SWT.NONE | SWT.V_SCROLL);
		group.setLayoutData(gridData);
		layout = new GridLayout();
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth=true;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		group.setLayout(layout);
		group.setText("Details");
		group.setFont(fontText);
		group.setBackground(DesktopStyle.getBackgroundColor());

		label = new Label(group, SWT.NONE);
		label.setText("Model");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 60;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		//gridData.widthHint = 100;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_drivers = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_drivers.setLayoutData(gridData);
		_drivers.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Port");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 100;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_ports = new Combo(group, SWT.NONE | SWT.READ_ONLY);
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
		_ports.setLayoutData(gridData);
		_ports.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Speed");
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 60;
		label.setLayoutData(gridData);
		label.setFont(fontText);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_speeds = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_speeds.setLayoutData(gridData);
		_speeds.add("1200");
		_speeds.add("4800");
		_speeds.add("9600");
		_speeds.select(0);
		_speeds.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Bits/char");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 130;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_bitsperchar = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_bitsperchar.setLayoutData(gridData);
		_bitsperchar.add("8");
		_bitsperchar.add("7");
		_bitsperchar.select(0);
		_bitsperchar.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Stopbits");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 60;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
//		gridData.widthHint = 60;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_stopbits = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_stopbits.setLayoutData(gridData);
		_stopbits.add("1");
		_stopbits.add("2");
		_stopbits.select(0);
		_stopbits.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Parity");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 60;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		//gridData.widthHint = 60;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_parity = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_parity.setLayoutData(gridData);
		_parity.add("none");
		_parity.add("even");
		_parity.add("odd");
		_parity.select(0);
		_parity.setFont(fontCombo);

		label = new Label(group, SWT.NONE);
		label.setText("Protocol");
		label.setFont(fontText);
		label.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		//gridData.widthHint = 60;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		//gridData.widthHint = 100;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_protocol = new Combo(group, SWT.NONE | SWT.READ_ONLY);
		_protocol.setLayoutData(gridData);
		_protocol.add("default");
		_protocol.select(0);
		_protocol.setData("protocol");
		_protocol.setFont(fontCombo);
		
		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan=2;
		_testResult = new Label(this, SWT.NONE);
		//Font font = DesktopImpl.getFont(20, SWT.NONE);
		_testResult.setFont(fontText);
		_testResult.setText("-");
		_testResult.setEnabled(false);
		_testResult.setLayoutData(gridData);
		
		
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth=true;
		layout.verticalSpacing = 0;
		layout.marginBottom=0;		layout.marginRight=0;		layout.marginLeft=0;		layout.marginTop=0;
		Composite panelButtons = new Composite(this,SWT.NONE);
		panelButtons.setLayout(layout);
		panelButtons.setBackground(DesktopStyle.getBackgroundColor());
		gridData = new GridData();
		gridData.verticalAlignment=GridData.FILL;
		gridData.grabExcessVerticalSpace=true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;	
		gridData.horizontalSpan=2;
		panelButtons.setLayoutData(gridData);

	
		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_validationButton = new Button(panelButtons, SWT.NONE);
		_validationButton.setText("Appliquer");
		_validationButton.setFont(fontText);
		_validationButton.setLayoutData(gridData);
		_validationButton.setData("configs");
		// _validationButton.setEnabled(false);

		gridData = new GridData();
		gridData.heightHint = HEIGHT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		_testButton = new Button(panelButtons, SWT.NONE);
		_testButton.setText("Test");
		_testButton.setEnabled(false);
		_testButton.setLayoutData(gridData);
		_testButton.setData("test");
		_testButton.setFont(fontText);



		layout();

	}

	public void enableValidation(boolean b) {
		_validationButton.setEnabled(b);
	}

	public void enableTest(boolean b) {
		_testButton.setEnabled(b);
	}

	public void setTestSucess(int success) {
		if (success == 0) {
			_testResult.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_GREEN));
			_testResult.setText("OK");
			_testButton.setEnabled(true);
		} else if (success < 0) {
			_testResult.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_RED));
			_testResult.setText("Echec");
			_testButton.setEnabled(true);
		} else {
			_testResult.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_GRAY));
			_testResult.setText("- - -");
			_testButton.setEnabled(false);
		}
		layout();
	}

	public void addSelectionListener(SelectionListener listener) {
		_validationButton.addSelectionListener(listener);
		_configs.addSelectionListener(listener);
		_drivers.addSelectionListener(listener);
		_testButton.addSelectionListener(listener);
	}

	public void setConfigs(List configs) {
		_configs.removeAll();
		configs.add(0, new String());
		Iterator iter = configs.iterator();
		while (iter.hasNext()) {
			_configs.add((String) iter.next());
		}
		_configs.select(0);

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
		_drivers.select(0);
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
		if (parity == null) {
			parity = "none";
		}
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
		if (speed == null) {
			speed = "9600";
		}
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
		if (bits == null) {
			bits = "8";
		}
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
		if (bits == null) {
			bits = "8";
		}
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
		if (port == null) {
			port = "com:1";
		}
		_ports.setText(port);
	}

	public String getProtocol() {
		int idx = _protocol.getSelectionIndex();
		String result = null;
		if (idx >= 0) {
			result = _protocol.getItem(idx);
		}
		return result;
	}

	public void setProtocol(String protocol) {
		if (protocol == null) {
			protocol = "unknown";
		}
		
		int idx=_protocol.indexOf(protocol);
		if (idx == -1){
			_protocol.add(protocol,0);
			_protocol.select(0);
		}
		else{
			_protocol.select(idx);
		}
		
		
	}

	public void setProtocols(List protocols) {
		_protocol.removeAll();
		Iterator iter = protocols.iterator();
		while (iter.hasNext()) {
			_protocol.add((String) iter.next());
		}
		_protocol.select(0);
	}

}
