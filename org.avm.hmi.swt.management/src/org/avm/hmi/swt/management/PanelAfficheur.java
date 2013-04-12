package org.avm.hmi.swt.management;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.hmi.swt.desktop.ABCKeyboard;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.Keyboard;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.management.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class PanelAfficheur extends AbstractPanel implements KeyboardListener,
		SelectionListener, ConsoleFacadeInjector, ConfigurableService {

	private Keyboard _keyb;

	private DriverSelection _driverSelection;

	private Hashtable _hashAfficheurPackage;

	private Hashtable _hashConfigsDrivers;

	private Hashtable _hashConfigs;

	private ConsoleFacade _console;

	private Config _config;

	private Logger logger = Logger.getInstance(this.getClass());

	public PanelAfficheur(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);
		create();
	}

	/**
	 * This method initializes itemBundles
	 * 
	 */
	private void create() {
		GridData gridData;

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		_driverSelection = new DriverSelection(this, SWT.NONE);
		_driverSelection.addSelectionListener(this);
		_driverSelection.setLayoutData(gridData);
		_driverSelection.enableTest(true);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		// gridData.horizontalIndent = 10;
		// gridData.heightHint = 40;
		_keyb = new ABCKeyboard(this, SWT.NONE);
		_keyb.setData(gridData);
		_keyb.setListener(this);
		_keyb.setBackground(DesktopStyle.getBackgroundColor());
		_keyb.setLayoutData(gridData);

		layout();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		_console = console;

		if (_console != null && _config != null) {
			configure();
			testAfficheur();
		}
	}

	private void configure() {

		_hashAfficheurPackage = new Hashtable();
		String result = runConsoleCommand("/management status generic.afficheur");
		StringTokenizer t = new StringTokenizer(result,
				System.getProperty("line.separator"));
		while (t.hasMoreElements()) {
			String line = t.nextToken();
			StringTokenizer t2 = new StringTokenizer(line, ":");
			String ignore = t2.nextToken();
			ignore = t2.nextToken();
			ignore = t2.nextToken();
			ignore = t2.nextToken();
			String val = t2.nextToken().trim();

			int idx = val.lastIndexOf("generic.");
			val = val.substring(0, idx)
					+ val.substring(idx + "generic.".length());

			idx = val.lastIndexOf(".");
			String name = val.substring(idx + 1);
			_hashAfficheurPackage.put(name, val);
		}

		Enumeration e = _hashAfficheurPackage.keys();
		String[] drivers = new String[_hashAfficheurPackage.size()];

		int i = 0;
		while (e.hasMoreElements()) {
			String keys = (String) e.nextElement();
			drivers[i] = keys;
			i++;
		}
		_driverSelection.setDrivers(drivers);

		String protocol = runConsoleCommand("/afficheur showparam protocol");
		if (protocol != null) {
			int idx = protocol.indexOf("protocol : ");
			protocol = protocol.substring(idx + "protocol : ".length());
			protocol = protocol.trim();
		} else {
			protocol = "defaut";
		}
		_driverSelection.setProtocol(protocol);

		String driver = runConsoleCommand("/afficheur showmodel");
		if (driver != null) {
			int idx = driver.lastIndexOf('.');
			driver = driver.substring(idx + 1);
			driver = driver.trim();
		} else {
			driver = "?";
		}

		result = runConsoleCommand("/afficheur showparam url");
		if (result != null) {
			int idx = result.indexOf("url : ");
			result = result.substring(idx + "url : ".length());
			result = "port=" + result;
			result = result.replace(';', '\n');
			Properties p = new Properties();
			try {
				p.load(new ByteArrayInputStream(result.getBytes()));
				_driverSelection.setDriver(driver);
				_driverSelection.setPort(p.getProperty("port"));
				_driverSelection.setStopbits(p.getProperty("stopbits"));
				_driverSelection.setSpeed(p.getProperty("baudrate"));
				_driverSelection.setBitsperchar(p.getProperty("bitsperchar"));
			} catch (IOException exp) {
				exp.printStackTrace();
			}
		}

	}

	public void configure(Config config) {
		_config = config;
		Properties p = ((ConfigImpl) config).getProperty("config-afficheurs");
		Set set = p.keySet();
		Iterator iter = set.iterator();
		_hashConfigsDrivers = new Hashtable();
		_hashConfigs = new Hashtable();
		List configNames = new ArrayList();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			String sconfig = p.getProperty(name);

			StringTokenizer t = new StringTokenizer(sconfig, "|");
			// --org.avm.device.afficheur.duhamel|comm:0|1200|8|1|none
			String driver = t.nextToken();

			List listConfigs = (List) _hashConfigsDrivers.get(driver);
			if (listConfigs == null) {
				listConfigs = new ArrayList();
			}

			SerialPortConfig gconfig = new SerialPortConfig();
			configNames.add(name);
			gconfig.setName(name);
			gconfig.setDriver(driver);
			gconfig.setPort(t.nextToken());
			gconfig.setSpeed(t.nextToken());
			gconfig.setBitsperchar(t.nextToken());
			gconfig.setStopbits(t.nextToken());
			gconfig.setParity(t.nextToken());
			String protocols = "default";
			if (t.hasMoreElements()) {
				protocols = t.nextToken();
			}
			t = new StringTokenizer(protocols, ",");
			List ps = new ArrayList();
			while (t.hasMoreElements()) {
				String e = (String) t.nextElement();
				ps.add(e.trim());
			}
			gconfig.setProtocols(ps);

			listConfigs.add(config);

			_hashConfigsDrivers.put(driver, listConfigs);
			_hashConfigs.put(name, gconfig);
		}
		_driverSelection.setConfigs(configNames);
		if (_console != null && _config != null) {
			configure();
			testAfficheur();
		}
	}

	public void validation(String str) {
		if (str != null) {
			test(str);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.getData().equals("configs")) {
				updateConfig();
			} else {
				testAfficheur();
			}

		} else if (event.getSource() instanceof Combo) {
			Combo combo = (Combo) event.getSource();
			if (combo.getData().equals("configs")) {
				_driverSelection.setTestSucess(1);
				setConfig();
			} else {

			}
		}
	}

	private void testAfficheur() {
		int success = 100;
		String result = null;
		String cmd = "/afficheur status";
		result = runConsoleCommand(cmd);
		logger.debug("PanelAfficheur test: " + result);
		if (result != null) {
			if (result.startsWith("OK")) {
				success = 0;
			} else if (result.startsWith("Err")) {
				success = -1;
			}
		}

		_driverSelection.setTestSucess(success);

	}

	private void setConfig() {
		String configName = _driverSelection.getConfig();
		if (configName.equals("")) {
			_driverSelection.enableValidation(false);
		} else {
			_driverSelection.enableValidation(true);
			SerialPortConfig config = (SerialPortConfig) _hashConfigs
					.get(configName);

			_driverSelection.setDriver(config.getDriver());
			_driverSelection.setPort(config.getPort());
			_driverSelection.setStopbits(config.getStopbits());
			_driverSelection.setSpeed(config.getSpeed());
			_driverSelection.setBitsperchar(config.getBitsperchar());
			_driverSelection.setParity(config.getParity());
			_driverSelection.setProtocols(config.getProtocols());
		}
	}

	private void updateConfig() {
		String port = _driverSelection.getPort();
		String driver = _driverSelection.getDriver();
		String speed = _driverSelection.getSpeed();
		String bitsperchar = _driverSelection.getBitsperchar();
		String stopbits = _driverSelection.getStopbits();
		String parity = _driverSelection.getParity();
		String protocol = _driverSelection.getProtocol();

		StringBuffer buf = new StringBuffer();
		buf.append(port);
		buf.append(";baudrate=");
		buf.append(speed);
		buf.append(";stopbits=");
		buf.append(stopbits);
		buf.append(";parity=");
		buf.append(parity);
		buf.append(";bitsperchar=");
		buf.append(bitsperchar);
		buf.append(";blocking=off");
		String url = "\"" + buf.toString() + "\"";

		String desc = "\"Controleur d'afficheur " + driver + "\"";
		String manufacturer = driver;

		String pkge = (String) _hashAfficheurPackage.get(driver + "");
		String name = pkge;
		String model = pkge;

		// "comm:1;baudrate=1200;stopbits=1;parity=none;bitsperchar=7;blocking=off"

		String cmd;

		boolean success = true;
		String result = null;
		cmd = "/afficheur setparameter url " + url;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		cmd = "/afficheur setdescription " + desc;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		cmd = "/afficheur setmanufacturer " + manufacturer;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		cmd = "/afficheur setname " + name;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		cmd = "/afficheur setmodel " + model;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		cmd = "/afficheur setparameter protocol " + protocol;
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

		logger.debug("Update config success : " + success);
		if (success) {
			cmd = "/afficheur updateconfig ";
			result = runConsoleCommand(cmd);
			logger.debug("Update config : " + result);

			cmd = "/management restart device.afficheur";
			result = runConsoleCommand(cmd);
			logger.debug("Restart device.afficheur : " + result);

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			testAfficheur();

		}

	}

	public void test(String message) {
		String result = runConsoleCommand("/afficheur print "
				+ message);
		if (result != null && !result.trim().equals("")) {
			MessageBox.setMessage("Erreur", result, MessageBox.MESSAGE_WARNING,
					SWT.NONE);
		}
	}

	public static class ItemAfficheurFactory extends PanelFactory {
		protected AbstractPanel create(Composite parent, int style) {
			return new PanelAfficheur(parent, style);
		}
	}

	static {
		PanelFactory.factories.put(PanelAfficheur.class.getName(),
				new ItemAfficheurFactory());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
