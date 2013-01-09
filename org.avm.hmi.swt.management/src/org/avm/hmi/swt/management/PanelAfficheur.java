package org.avm.hmi.swt.management;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.hmi.swt.desktop.ABCKeyboard;
import org.avm.hmi.swt.desktop.DesktopStyle;
import org.avm.hmi.swt.desktop.KeyboardListener;
import org.avm.hmi.swt.desktop.MessageBox;
import org.avm.hmi.swt.management.bundle.ConfigImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PanelAfficheur extends AbstractPanel implements KeyboardListener,
		SelectionListener, ConfigurableService {

	private ABCKeyboard _keyb;

	private DriverSelection _driverSelection;

	private Hashtable _hashAfficheur;

	private Hashtable _hashConfigsAfficheurs;

	public PanelAfficheur(Composite parent, int style) {
		super(parent, style);
	}

	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
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
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = 10;
		// gridData.heightHint = 40;

		_driverSelection = new DriverSelection(this, SWT.NONE);
		_driverSelection.addSelectionListener(this);
		_driverSelection.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalIndent = 10;
		gridData.heightHint = 40;
		_keyb = new ABCKeyboard(this, SWT.NONE);
		_keyb.setData(gridData);
		_keyb.setListener(this);
		_keyb.setBackground(DesktopStyle.getBackgroundColor());
		_keyb.setLayoutData(gridData);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		_keyb.setText("Test " + df.format(new Date()));

		layout();
	}

	public void setConsoleFacade(ConsoleFacade console) {
		super.setConsoleFacade(console);
		configure();
	}

	private void configure() {
		_hashAfficheur = new Hashtable();

		String result = runConsoleCommand("/mana status generic.afficheur");
		StringTokenizer t = new StringTokenizer(result, "\n");
		while (t.hasMoreElements()) {
			String line = t.nextToken().trim();
			System.out.println("[DLA] PanelAfficheur Line:" + line);
			if (!line.equals("")) {
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
				System.out.println("[DLA] PanelAfficheur name:" + name + ", val=" + val);
				_hashAfficheur.put(name, val);
			}
		}

		Enumeration e = _hashAfficheur.keys();
		String[] drivers = new String[_hashAfficheur.size()];
		int i = 0;
		while (e.hasMoreElements()) {
			String keys = (String) e.nextElement();
			drivers[i] = keys;
			i++;
		}

		_driverSelection.setDrivers(drivers);

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
				_driverSelection.setParity(p.getProperty("parity"));
			} catch (IOException exp) {
				exp.printStackTrace();
			}
		}
		initConfig();

	}

	private void initConfig() {
		if (_hashConfigsAfficheurs != null && _hashAfficheur != null) {
			String[] configs = new String[_hashConfigsAfficheurs.size()];
			Enumeration en = _hashConfigsAfficheurs.keys();
			int i = 0;
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String cmd = (String) _hashConfigsAfficheurs.get(key);
				int idx = cmd.indexOf("|");
				cmd = cmd.substring(0, idx) + "";
				if (_hashAfficheur.get(cmd) != null) {
					configs[i] = key;
					i++;
				}
			}
			_driverSelection.setConfigs(configs);
		}
	}

	public void configure(Config config) {
		Properties p = ((ConfigImpl) config).getProperty("config-afficheurs");
		Set set = p.keySet();
		Iterator iter = set.iterator();
		_hashConfigsAfficheurs = new Hashtable();
		while (iter.hasNext()) {
			String value = (String) iter.next();
			_hashConfigsAfficheurs.put(value, p.get(value));
		}
		initConfig();
	}

	public void test(String message) {
		String result = runConsoleCommand("/afficheur print \"" + message
				+ "\"");
		if (result != null && !result.trim().equals("")) {
			MessageBox.setMessage("Erreur", result, MessageBox.MESSAGE_WARNING,
					SWT.NONE);
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
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() instanceof Button) {
			updateConfig();
		} else {
			setConfig();
		}
	}

	private void setConfig() {
		String configName = _driverSelection.getConfig();
		String params = (String) _hashConfigsAfficheurs.get(configName);
		StringTokenizer t = new StringTokenizer(params, "|");
		// --org.avm.device.girouette.duhamel|comm:0|1200|8|1|none
		String driver = t.nextToken();
		String port = t.nextToken();
		String speed = t.nextToken();
		String bitsperchar = t.nextToken();
		String stopbits = t.nextToken();
		String parity = t.nextToken();
		_driverSelection.setDriver(driver);
		_driverSelection.setPort(port);
		_driverSelection.setStopbits(stopbits);
		_driverSelection.setSpeed(speed);
		_driverSelection.setBitsperchar(bitsperchar);
		_driverSelection.setParity(parity);
	}

	private void updateConfig() {
		String port = _driverSelection.getPort();
		String driver = _driverSelection.getDriver();
		String speed = _driverSelection.getSpeed();
		String bitsperchar = _driverSelection.getBitsperchar();
		String stopbits = _driverSelection.getStopbits();
		String parity = _driverSelection.getParity();

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

		String desc = "\"Controleur afficheur " + driver + "\"";
		String manufacturer = driver;
		String pkge = (String) _hashAfficheur.get(driver);
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

		cmd = "/afficheur updateconfig ";
		result = runConsoleCommand(cmd);
		success &= (result != null && !result.trim().equals(""));

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
